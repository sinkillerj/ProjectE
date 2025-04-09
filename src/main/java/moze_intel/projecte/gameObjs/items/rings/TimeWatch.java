package moze_intel.projecte.gameObjs.items.rings;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.PETags.Blocks;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.RegistryUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.BoundTickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk.RebindableTickingBlockEntityWrapper;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TimeWatch extends PEToggleItem implements IPedestalItem, IItemCharge, IBarHelper {

	private static final Predicate<BlockEntity> VALID_TARGET = be -> !be.isRemoved() && be.hasLevel() && !RegistryUtils.getBEHolder(be.getType()).is(BlockEntities.BLACKLIST_TIME_WATCH);

	public TimeWatch(Properties props) {
		super(props.component(PEDataComponentTypes.CHARGE, 0)
				.component(PEDataComponentTypes.STORED_EMC, 0L)
				.component(PEDataComponentTypes.UNPROCESSED_EMC, 0.0)
		);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			if (!ProjectEConfig.server.items.enableTimeWatch.get()) {
				player.sendSystemMessage(PELang.TIME_WATCH_DISABLED.translate());
				return InteractionResultHolder.fail(stack);
			}
			stack.update(PEDataComponentTypes.TIME_WATCH_MODE, TimeWatchMode.OFF, TimeWatchMode::next);
			player.sendSystemMessage(PELang.TIME_WATCH_MODE_SWITCH.translate(getTimeName(stack)));
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (!(entity instanceof Player player) || !hotBarOrOffHand(slot) || !ProjectEConfig.server.items.enableTimeWatch.get()) {
			return;
		}
		TimeWatchMode timeControl = stack.getOrDefault(PEDataComponentTypes.TIME_WATCH_MODE, TimeWatchMode.OFF);
		if (timeControl != TimeWatchMode.OFF && !level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
			ServerLevel serverWorld = (ServerLevel) level;
			long scaledCharge = 4L * (getCharge(stack) + 1);
			if (timeControl == TimeWatchMode.REWIND) {//rewind
				serverWorld.setDayTime(Math.max(level.getDayTime() - scaledCharge, 0));
			} else if (level.getDayTime() > Long.MAX_VALUE - scaledCharge) {//Fast forward (would go past max long)
				serverWorld.setDayTime(Long.MAX_VALUE);
			} else {//Fast forward
				serverWorld.setDayTime(level.getDayTime() + scaledCharge);
			}
		}
		if (level.isClientSide || !stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			return;
		}
		int charge = getCharge(stack);
		long reqEmc = EMCHelper.removeFractionalEMC(stack, getEmcPerTick(charge));
		if (!consumeFuel(player, stack, reqEmc, true)) {
			return;
		}
		int bonusTicks;
		float mobSlowdown;
		if (charge == 0) {
			bonusTicks = 8;
			mobSlowdown = 0.25F;
		} else if (charge == 1) {
			bonusTicks = 12;
			mobSlowdown = 0.16F;
		} else {
			bonusTicks = 16;
			mobSlowdown = 0.12F;
		}
		AABB effectBounds = player.getBoundingBox().inflate(8);
		speedUpBlocks(level, bonusTicks, effectBounds);
		slowMobs(level, effectBounds, mobSlowdown);
	}

	private void slowMobs(Level level, AABB effectBounds, double mobSlowdown) {
		if (mobSlowdown < 1) {
			for (Mob ent : level.getEntitiesOfClass(Mob.class, effectBounds)) {
				ent.setDeltaMovement(ent.getDeltaMovement().multiply(mobSlowdown, 1, mobSlowdown));
			}
		}
	}

	private void speedUpBlocks(Level level, int bonusTicks, AABB effectBounds) {
		if (bonusTicks > 0) {
			speedUpBlockEntities(level, bonusTicks, effectBounds);
			speedUpRandomTicks(level, bonusTicks, effectBounds);
		}
	}

	private void speedUpBlockEntities(Level level, int bonusTicks, AABB effectBounds) {
		for (BlockEntity blockEntity : WorldHelper.getBlockEntitiesWithinAABB(level, effectBounds, VALID_TARGET)) {
			BlockPos pos = blockEntity.getBlockPos();
			if (level.shouldTickBlocksAt(ChunkPos.asLong(pos))) {
				LevelChunk chunk = level.getChunkAt(pos);
				RebindableTickingBlockEntityWrapper tickingWrapper = chunk.tickersInLevel.get(pos);
				if (tickingWrapper != null && !tickingWrapper.isRemoved()) {
					if (tickingWrapper.ticker instanceof BoundTickingBlockEntity<?> tickingBE) {
						//In general this should always be the case, so we inline some of the logic
						// to optimize the calls to try and make extra ticks as cheap as possible
						if (chunk.isTicking(pos)) {
							ProfilerFiller profiler = level.getProfiler();
							//Note: We intentionally don't start tracking with the TimeTracker that neo patches in
							// because we don't want to override tracking for the pedestal
							profiler.push(tickingWrapper::getType);
							BlockState state = chunk.getBlockState(pos);
							provideBonusTicks(level, pos, state, blockEntity, tickingBE, bonusTicks);
							profiler.pop();
						}
					} else {
						//Fallback to just trying to make it tick extra
						for (int i = 0; i < bonusTicks; i++) {
							tickingWrapper.tick();
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends BlockEntity> void provideBonusTicks(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, BoundTickingBlockEntity<T> tickingBE,
			int bonusTicks) {
		if (blockEntity.getType().isValid(state)) {
			T be = (T) blockEntity;
			for (int i = 0; i < bonusTicks; i++) {
				tickingBE.ticker.tick(level, pos, state, be);
			}
		}
	}

	private void speedUpRandomTicks(Level level, int bonusTicks, AABB effectBounds) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		for (BlockPos pos : WorldHelper.getPositionsInBox(effectBounds)) {
			if (WorldHelper.isBlockLoaded(level, pos)) {
				BlockState state = level.getBlockState(pos);
				if (state.isRandomlyTicking() && !state.is(Blocks.BLACKLIST_TIME_WATCH)) {
					FluidState fluidState = state.getFluidState();
					if (fluidState.isEmpty() || fluidState.isSource()) {// Don't speed non-source fluid blocks - dupe issues
						if (!WorldHelper.isCrop(state)) {// All plants should be sped using Harvest Goddess
							pos = pos.immutable();
							for (int i = 0; i < bonusTicks; i++) {
								state.randomTick(serverLevel, pos, level.random);
							}
						}
					}
				}
			}
		}
	}

	private ILangEntry getTimeName(ItemStack stack) {
		return stack.getOrDefault(PEDataComponentTypes.TIME_WATCH_MODE, TimeWatchMode.OFF).name;
	}

	public double getEmcPerTick(int charge) {
		return (charge + 2) / 2.0D;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);
		tooltip.add(PELang.TOOLTIP_TIME_WATCH_1.translate());
		tooltip.add(PELang.TOOLTIP_TIME_WATCH_2.translate(Component.keybind("key.use")));
		tooltip.add(PELang.TIME_WATCH_MODE.translate(getTimeName(stack)));
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		// Change from old EE2 behaviour (universally increased tickrate) for safety and impl reasons.
		if (!level.isClientSide && ProjectEConfig.server.items.enableTimeWatch.get()) {
			AABB effectBounds = pedestal.getEffectBounds();
			speedUpBlocks(level, ProjectEConfig.server.effects.timePedBonus.get(), effectBounds);
			slowMobs(level, effectBounds, ProjectEConfig.server.effects.timePedMobSlowness.get());
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription(float tickRate) {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.effects.timePedBonus.get() > 0) {
			list.add(PELang.PEDESTAL_TIME_WATCH_1.translateColored(ChatFormatting.BLUE, ProjectEConfig.server.effects.timePedBonus.get()));
		}
		if (ProjectEConfig.server.effects.timePedMobSlowness.get() < 1.0F) {
			list.add(PELang.PEDESTAL_TIME_WATCH_2.translateColored(ChatFormatting.BLUE, String.format("%.3f", ProjectEConfig.server.effects.timePedMobSlowness.get())));
		}
		return list;
	}

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return 2;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		return 1 - getChargePercent(stack);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getColorForBar(stack);
	}

	public enum TimeWatchMode implements StringRepresentable {
		OFF(PELang.TIME_WATCH_OFF),
		FAST_FORWARD(PELang.TIME_WATCH_FAST_FORWARD),
		REWIND(PELang.TIME_WATCH_REWIND);

		public static final Codec<TimeWatchMode> CODEC = StringRepresentable.fromEnum(TimeWatchMode::values);
		public static final IntFunction<TimeWatchMode> BY_ID = ByIdMap.continuous(TimeWatchMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, TimeWatchMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, TimeWatchMode::ordinal);

		private final String serializedName;
		private final ILangEntry name;

		TimeWatchMode(ILangEntry name) {
			this.serializedName = name().toLowerCase(Locale.ROOT);
			this.name = name;
		}

		@NotNull
		@Override
		public String getSerializedName() {
			return serializedName;
		}

		public TimeWatchMode next() {
			return switch (this) {
				case OFF -> FAST_FORWARD;
				case FAST_FORWARD -> REWIND;
				case REWIND -> OFF;
			};
		}
	}
}