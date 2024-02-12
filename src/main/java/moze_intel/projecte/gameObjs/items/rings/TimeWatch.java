package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.PETags.Blocks;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.RegistryUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.BoundTickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk.RebindableTickingBlockEntityWrapper;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeWatch extends PEToggleItem implements IPedestalItem, IItemCharge, IBarHelper {

	private static final Predicate<BlockEntity> VALID_TARGET = be -> !be.isRemoved() && !RegistryUtils.getBEHolder(be.getType()).is(BlockEntities.BLACKLIST_TIME_WATCH);

	public TimeWatch(Properties props) {
		super(props);
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
			stack.setData(PEAttachmentTypes.TIME_WATCH_MODE, stack.getData(PEAttachmentTypes.TIME_WATCH_MODE).next());
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
		TimeWatchMode timeControl = stack.getData(PEAttachmentTypes.TIME_WATCH_MODE);
		if (!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
			ServerLevel serverWorld = (ServerLevel) level;
			if (timeControl == TimeWatchMode.FAST_FORWARD) {
				serverWorld.setDayTime(Math.min(level.getDayTime() + (getCharge(stack) + 1) * 4L, Long.MAX_VALUE));
			} else if (timeControl == TimeWatchMode.REWIND) {
				long charge = getCharge(stack) + 1;
				if (level.getDayTime() - charge * 4 < 0) {
					serverWorld.setDayTime(0);
				} else {
					serverWorld.setDayTime(level.getDayTime() - charge * 4);
				}
			}
		}
		if (level.isClientSide || !stack.getData(PEAttachmentTypes.ACTIVE)) {
			return;
		}
		long reqEmc = EMCHelper.removeFractionalEMC(stack, getEmcPerTick(this.getCharge(stack)));
		if (!consumeFuel(player, stack, reqEmc, true)) {
			return;
		}
		int charge = this.getCharge(stack);
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
					//TODO - 1.20.4: Look at TimeTracker (basically what neo patches in for BoundTickingBlockEntity#tick)
					// And whether the tracking data for the pedestal gets screwed up if we have to fallback to the other if branch
					if (tickingWrapper.ticker instanceof BoundTickingBlockEntity tickingBE) {
						//In general this should always be the case, so we inline some of the logic
						// to optimize the calls to try and make extra ticks as cheap as possible
						if (chunk.isTicking(pos)) {
							ProfilerFiller profiler = level.getProfiler();
							profiler.push(tickingWrapper::getType);
							BlockState state = chunk.getBlockState(pos);
							if (blockEntity.getType().isValid(state)) {
								for (int i = 0; i < bonusTicks; i++) {
									tickingBE.ticker.tick(level, pos, state, blockEntity);
								}
							}
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

	private void speedUpRandomTicks(Level level, int bonusTicks, AABB effectBounds) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		for (BlockPos pos : WorldHelper.getPositionsInBox(effectBounds)) {
			if (WorldHelper.isBlockLoaded(level, pos)) {
				BlockState state = level.getBlockState(pos);
				Block block = state.getBlock();
				if (state.isRandomlyTicking() && !state.is(Blocks.BLACKLIST_TIME_WATCH)
					&& !(block instanceof LiquidBlock) // Don't speed non-source fluid blocks - dupe issues
					&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) {// All plants should be sped using Harvest Goddess
					pos = pos.immutable();
					for (int i = 0; i < bonusTicks; i++) {
						state.randomTick(serverLevel, pos, level.random);
					}
				}
			}
		}
	}

	private ILangEntry getTimeName(ItemStack stack) {
		return stack.getData(PEAttachmentTypes.TIME_WATCH_MODE).name;
	}

	public double getEmcPerTick(int charge) {
		return (charge + 2) / 2.0D;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_TIME_WATCH_1.translate());
		tooltips.add(PELang.TOOLTIP_TIME_WATCH_2.translate());
		tooltips.add(PELang.TIME_WATCH_MODE.translate(getTimeName(stack)));
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

	public enum TimeWatchMode {
		OFF(PELang.TIME_WATCH_OFF),
		FAST_FORWARD(PELang.TIME_WATCH_FAST_FORWARD),
		REWIND(PELang.TIME_WATCH_REWIND);

		private final ILangEntry name;

		TimeWatchMode(ILangEntry name) {
			this.name = name;
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