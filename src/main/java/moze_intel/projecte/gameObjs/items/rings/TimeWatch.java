package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.PETags.BlockEntities;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;

public class TimeWatch extends PEToggleItem implements IPedestalItem, IItemCharge {

	public TimeWatch(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapper::new);
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			if (!ProjectEConfig.server.items.enableTimeWatch.get()) {
				player.sendMessage(PELang.TIME_WATCH_DISABLED.translate(), Util.NIL_UUID);
				return InteractionResultHolder.fail(stack);
			}
			byte current = getTimeBoost(stack);
			setTimeBoost(stack, (byte) (current == 2 ? 0 : current + 1));
			player.sendMessage(PELang.TIME_WATCH_MODE_SWITCH.translate(getTimeName(stack)), Util.NIL_UUID);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity entity, int invSlot, boolean isHeld) {
		super.inventoryTick(stack, world, entity, invSlot, isHeld);
		if (!(entity instanceof Player player) || invSlot >= Inventory.getSelectionSize() || !ProjectEConfig.server.items.enableTimeWatch.get()) {
			return;
		}
		byte timeControl = getTimeBoost(stack);
		if (!world.isClientSide && world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
			ServerLevel serverWorld = (ServerLevel) world;
			if (timeControl == 1) {
				serverWorld.setDayTime(Math.min(world.getDayTime() + (getCharge(stack) + 1) * 4L, Long.MAX_VALUE));
			} else if (timeControl == 2) {
				long charge = getCharge(stack) + 1;
				if (world.getDayTime() - charge * 4 < 0) {
					serverWorld.setDayTime(0);
				} else {
					serverWorld.setDayTime(world.getDayTime() - charge * 4);
				}
			}
		}
		if (world.isClientSide || !ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
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
		AABB bBox = player.getBoundingBox().inflate(8);
		speedUpTileEntities(world, bonusTicks, bBox);
		speedUpRandomTicks(world, bonusTicks, bBox);
		slowMobs(world, bBox, mobSlowdown);
	}

	private void slowMobs(Level world, AABB bBox, double mobSlowdown) {
		if (bBox == null) {
			// Sanity check for chunk unload weirdness
			return;
		}
		for (Mob ent : world.getEntitiesOfClass(Mob.class, bBox)) {
			ent.setDeltaMovement(ent.getDeltaMovement().multiply(mobSlowdown, 1, mobSlowdown));
		}
	}

	private void speedUpTileEntities(Level world, int bonusTicks, AABB bBox) {
		if (bBox == null || bonusTicks == 0) {
			// Sanity check the box for chunk unload weirdness
			return;
		}

		List<BlockEntity> list = WorldHelper.getTileEntitiesWithinAABB(world, bBox);
		//TODO - 1.18: Level#shouldTickBlocksAt
		for (BlockEntity tile : list) {
			if (!tile.isRemoved() && !tile.getType().isIn(BlockEntities.BLACKLIST_TIME_WATCH)) {
				//TODO - 1.18: Look into this more, maybe need to AT into LevelChunk#updateblockEntityTicker???
				// Level see how TickingBlockEntity is used
				/*if (tile instanceof TickableBlockEntity tickableTile) {
					for (int i = 0; i < bonusTicks; i++) {
						tickableTile.tick();
					}
				}*/
			}
		}
	}

	private void speedUpRandomTicks(Level world, int bonusTicks, AABB bBox) {
		if (bBox == null || bonusTicks == 0 || !(world instanceof ServerLevel level)) {
			// Sanity check the box for chunk unload weirdness
			return;
		}
		for (BlockPos pos : WorldHelper.getPositionsFromBox(bBox)) {
			for (int i = 0; i < bonusTicks; i++) {
				BlockState state = level.getBlockState(pos);
				Block block = state.getBlock();
				if (state.isRandomlyTicking() && !PETags.Blocks.BLACKLIST_TIME_WATCH.contains(block)
					&& !(block instanceof LiquidBlock) // Don't speed non-source fluid blocks - dupe issues
					&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) // All plants should be sped using Harvest Goddess
				{
					state.randomTick(level, pos.immutable(), level.random);
				}
			}
		}
	}

	private ILangEntry getTimeName(ItemStack stack) {
		byte mode = getTimeBoost(stack);
		return switch (mode) {
			case 0 -> PELang.TIME_WATCH_OFF;
			case 1 -> PELang.TIME_WATCH_FAST_FORWARD;
			case 2 -> PELang.TIME_WATCH_REWIND;
			default -> PELang.INVALID_MODE;
		};
	}

	private byte getTimeBoost(ItemStack stack) {
		return stack.hasTag() ? stack.getOrCreateTag().getByte(Constants.NBT_KEY_TIME_MODE) : 0;
	}

	private void setTimeBoost(ItemStack stack, byte time) {
		stack.getOrCreateTag().putByte(Constants.NBT_KEY_TIME_MODE, (byte) Mth.clamp(time, 0, 2));
	}

	public double getEmcPerTick(int charge) {
		return (charge + 2) / 2.0D;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_TIME_WATCH_1.translate());
		tooltips.add(PELang.TOOLTIP_TIME_WATCH_2.translate());
		if (stack.hasTag()) {
			tooltips.add(PELang.TIME_WATCH_MODE.translate(getTimeName(stack)));
		}
	}

	@Override
	public void updateInPedestal(@Nonnull Level world, @Nonnull BlockPos pos) {
		// Change from old EE2 behaviour (universally increased tickrate) for safety and impl reasons.
		if (!world.isClientSide && ProjectEConfig.server.items.enableTimeWatch.get()) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				AABB bBox = tile.getEffectBounds();
				if (ProjectEConfig.server.effects.timePedBonus.get() > 0) {
					speedUpTileEntities(world, ProjectEConfig.server.effects.timePedBonus.get(), bBox);
					speedUpRandomTicks(world, ProjectEConfig.server.effects.timePedBonus.get(), bBox);
				}
				if (ProjectEConfig.server.effects.timePedMobSlowness.get() < 1.0F) {
					slowMobs(world, bBox, ProjectEConfig.server.effects.timePedMobSlowness.get());
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
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
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 2;
	}

	@Override
	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(@Nonnull ItemStack stack) {
		return Math.round(13.0F - 13.0F * (float) (1.0D - getChargePercent(stack)));
	}
}