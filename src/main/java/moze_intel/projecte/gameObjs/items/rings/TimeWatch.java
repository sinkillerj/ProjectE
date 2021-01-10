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
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

public class TimeWatch extends PEToggleItem implements IPedestalItem, IItemCharge {

	public TimeWatch(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapper::new);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (!ProjectEConfig.server.items.enableTimeWatch.get()) {
				player.sendMessage(PELang.TIME_WATCH_DISABLED.translate(), Util.DUMMY_UUID);
				return ActionResult.resultFail(stack);
			}
			byte current = getTimeBoost(stack);
			setTimeBoost(stack, (byte) (current == 2 ? 0 : current + 1));
			player.sendMessage(PELang.TIME_WATCH_MODE_SWITCH.translate(getTimeName(stack)), Util.DUMMY_UUID);
		}
		return ActionResult.resultSuccess(stack);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int invSlot, boolean isHeld) {
		super.inventoryTick(stack, world, entity, invSlot, isHeld);
		if (!(entity instanceof PlayerEntity) || invSlot > 8) {
			return;
		}
		if (!ProjectEConfig.server.items.enableTimeWatch.get()) {
			return;
		}
		byte timeControl = getTimeBoost(stack);
		if (!world.isRemote && world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
			ServerWorld serverWorld = (ServerWorld) world;
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
		if (world.isRemote || !stack.hasTag() || !stack.getTag().getBoolean(Constants.NBT_KEY_ACTIVE)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;
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
		AxisAlignedBB bBox = player.getBoundingBox().grow(8);
		speedUpTileEntities(world, bonusTicks, bBox);
		speedUpRandomTicks(world, bonusTicks, bBox);
		slowMobs(world, bBox, mobSlowdown);
	}

	private void slowMobs(World world, AxisAlignedBB bBox, double mobSlowdown) {
		if (bBox == null) {
			// Sanity check for chunk unload weirdness
			return;
		}
		for (MobEntity ent : world.getEntitiesWithinAABB(MobEntity.class, bBox)) {
			ent.setMotion(ent.getMotion().mul(mobSlowdown, 1, mobSlowdown));
		}
	}

	private void speedUpTileEntities(World world, int bonusTicks, AxisAlignedBB bBox) {
		if (bBox == null || bonusTicks == 0) {
			// Sanity check the box for chunk unload weirdness
			return;
		}

		List<TileEntity> list = WorldHelper.getTileEntitiesWithinAABB(world, bBox);
		for (int i = 0; i < bonusTicks; i++) {
			for (TileEntity tile : list) {
				if (!tile.isRemoved() && tile instanceof ITickableTileEntity && !tile.getType().isIn(PETags.TileEntities.BLACKLIST_TIME_WATCH)) {
					((ITickableTileEntity) tile).tick();
				}
			}
		}
	}

	private void speedUpRandomTicks(World world, int bonusTicks, AxisAlignedBB bBox) {
		if (bBox == null || bonusTicks == 0 || !(world instanceof ServerWorld)) {
			// Sanity check the box for chunk unload weirdness
			return;
		}
		for (BlockPos pos : WorldHelper.getPositionsFromBox(bBox)) {
			for (int i = 0; i < bonusTicks; i++) {
				BlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (state.ticksRandomly() && !block.isIn(PETags.Blocks.BLACKLIST_TIME_WATCH)
					&& !(block instanceof FlowingFluidBlock) // Don't speed non-source fluid blocks - dupe issues
					&& !(block instanceof IGrowable) && !(block instanceof IPlantable)) // All plants should be sped using Harvest Goddess
				{
					state.randomTick((ServerWorld) world, pos.toImmutable(), random);
				}
			}
		}
	}

	private ILangEntry getTimeName(ItemStack stack) {
		byte mode = getTimeBoost(stack);
		switch (mode) {
			case 0:
				return PELang.TIME_WATCH_OFF;
			case 1:
				return PELang.TIME_WATCH_FAST_FORWARD;
			case 2:
				return PELang.TIME_WATCH_REWIND;
			default:
				return PELang.INVALID_MODE;
		}
	}

	private byte getTimeBoost(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getByte(Constants.NBT_KEY_TIME_MODE) : 0;
	}

	private void setTimeBoost(ItemStack stack, byte time) {
		stack.getOrCreateTag().putByte(Constants.NBT_KEY_TIME_MODE, (byte) MathHelper.clamp(time, 0, 2));
	}

	public double getEmcPerTick(int charge) {
		return (charge + 2) / 2.0D;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.addInformation(stack, world, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_TIME_WATCH_1.translate());
		tooltips.add(PELang.TOOLTIP_TIME_WATCH_2.translate());
		if (stack.hasTag()) {
			tooltips.add(PELang.TIME_WATCH_MODE.translate(getTimeName(stack)));
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		// Change from old EE2 behaviour (universally increased tickrate) for safety and impl reasons.
		if (!world.isRemote && ProjectEConfig.server.items.enableTimeWatch.get()) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				AxisAlignedBB bBox = tile.getEffectBounds();
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
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.effects.timePedBonus.get() > 0) {
			list.add(PELang.PEDESTAL_TIME_WATCH_1.translateColored(TextFormatting.BLUE, ProjectEConfig.server.effects.timePedBonus.get()));
		}
		if (ProjectEConfig.server.effects.timePedMobSlowness.get() < 1.0F) {
			//TODO - 1.16: Number format
			list.add(PELang.PEDESTAL_TIME_WATCH_2.translateColored(TextFormatting.BLUE, String.format("%.3f", ProjectEConfig.server.effects.timePedMobSlowness.get())));
		}
		return list;
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 2;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - getChargePercent(stack);
	}
}