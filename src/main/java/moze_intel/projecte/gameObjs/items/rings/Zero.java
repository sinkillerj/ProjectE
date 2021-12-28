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
import moze_intel.projecte.gameObjs.block_entities.DMPedestalTile;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

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
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity entity, int slot, boolean held) {
		super.inventoryTick(stack, world, entity, slot, held);
		if (!world.isClientSide && entity instanceof Player && slot < Inventory.getSelectionSize() && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			AABB box = new AABB(entity.getX() - 3, entity.getY() - 3, entity.getZ() - 3,
					entity.getX() + 3, entity.getY() + 3, entity.getZ() + 3);
			WorldHelper.freezeInBoundingBox(world, box, (Player) entity, true);
		}
	}


	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			int offset = 3 + this.getCharge(stack);
			AABB box = player.getBoundingBox().inflate(offset);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			WorldHelper.freezeInBoundingBox(world, box, player, false);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void updateInPedestal(@Nonnull Level world, @Nonnull BlockPos pos) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.zero.get() != -1) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				if (tile.getActivityCooldown() == 0) {
					AABB aabb = tile.getEffectBounds();
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
	public List<Component> getPedestalDescription() {
		//Only used on the client
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.zero.get() != -1) {
			list.add(PELang.PEDESTAL_ZERO_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_ZERO_2.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_ZERO_3.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.zero.get())));
		}
		return list;
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 4;
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