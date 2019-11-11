package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class Zero extends PEToggleItem implements IPedestalItem, IItemCharge {

	public Zero(Properties props) {
		super(props);
		addItemCapability(new PedestalItemCapabilityWrapper());
		addItemCapability(new ChargeItemCapabilityWrapper());
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.copy();
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean held) {
		super.inventoryTick(stack, world, entity, slot, held);
		if (world.isRemote || !(entity instanceof PlayerEntity) || slot > 8 || !stack.getOrCreateTag().getBoolean(Constants.NBT_KEY_ACTIVE)) {
			return;
		}
		AxisAlignedBB box = new AxisAlignedBB(entity.posX - 3, entity.posY - 3, entity.posZ - 3, entity.posX + 3, entity.posY + 3, entity.posZ + 3);
		WorldHelper.freezeInBoundingBox(world, box, (PlayerEntity) entity, true);
	}


	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			int offset = 3 + this.getCharge(stack);
			AxisAlignedBB box = player.getBoundingBox().grow(offset);
			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			WorldHelper.freezeInBoundingBox(world, box, player, false);
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote && ProjectEConfig.server.cooldown.pedestal.zero.get() != -1) {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile)) {
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0) {
				AxisAlignedBB aabb = tile.getEffectBounds();
				WorldHelper.freezeInBoundingBox(world, aabb, null, false);
				List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
				for (Entity ent : list) {
					if (ent.isBurning()) {
						ent.extinguish();
					}
				}
				tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.zero.get());
			} else {
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		//Only used on the client
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.zero.get() != -1) {
			list.add(new TranslationTextComponent("pe.zero.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.zero.pedestal2").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.zero.pedestal3", MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.zero.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 4;
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