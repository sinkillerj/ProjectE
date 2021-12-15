package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Zero extends PEToggleItem implements IPedestalItem, IItemCharge {

	public Zero(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(ChargeItemCapabilityWrapper::new);
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
		if (!world.isClientSide && entity instanceof PlayerEntity && slot < PlayerInventory.getSelectionSize() && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			AxisAlignedBB box = new AxisAlignedBB(entity.getX() - 3, entity.getY() - 3, entity.getZ() - 3,
					entity.getX() + 3, entity.getY() + 3, entity.getZ() + 3);
			WorldHelper.freezeInBoundingBox(world, box, (PlayerEntity) entity, true);
		}
	}


	@Nonnull
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			int offset = 3 + this.getCharge(stack);
			AxisAlignedBB box = player.getBoundingBox().inflate(offset);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			WorldHelper.freezeInBoundingBox(world, box, player, false);
		}
		return ActionResult.success(stack);
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.zero.get() != -1) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				if (tile.getActivityCooldown() == 0) {
					AxisAlignedBB aabb = tile.getEffectBounds();
					WorldHelper.freezeInBoundingBox(world, aabb, null, false);
					List<Entity> list = world.getEntitiesOfClass(Entity.class, aabb);
					for (Entity ent : list) {
						if (ent.isOnFire()) {
							ent.clearFire();
						}
					}
					tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.zero.get());
				} else {
					tile.decrementActivityCooldown();
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		//Only used on the client
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.zero.get() != -1) {
			list.add(PELang.PEDESTAL_ZERO_1.translateColored(TextFormatting.BLUE));
			list.add(PELang.PEDESTAL_ZERO_2.translateColored(TextFormatting.BLUE));
			list.add(PELang.PEDESTAL_ZERO_3.translateColored(TextFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.zero.get())));
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