package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.AlchBagItemCapabilityWrapper;
import moze_intel.projecte.capability.AlchChestItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.block_entities.AlchChestTile;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalTile;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RepairTalisman extends ItemPE implements IAlchBagItem, IAlchChestItem, IPedestalItem {

	private static final Predicate<ItemStack> CAN_REPAIR_ITEM = stack -> !stack.isEmpty() &&
																		 !stack.getCapability(ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY).isPresent() &&
																		 ItemHelper.isRepairableDamagedItem(stack);

	public RepairTalisman(Properties props) {
		super(props);
		addItemCapability(AlchBagItemCapabilityWrapper::new);
		addItemCapability(AlchChestItemCapabilityWrapper::new);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, Level world, @Nonnull Entity entity, int invSlot, boolean isSelected) {
		if (!world.isClientSide && entity instanceof Player) {
			Player player = (Player) entity;
			player.getCapability(InternalTimers.CAPABILITY).ifPresent(timers -> {
				timers.activateRepair();
				if (timers.canRepair()) {
					repairAllItems(player);
				}
			});
		}
	}

	@Override
	public void updateInPedestal(@Nonnull Level world, @Nonnull BlockPos pos) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.repair.get() != -1) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				if (tile.getActivityCooldown() == 0) {
					world.getEntitiesOfClass(ServerPlayer.class, tile.getEffectBounds()).forEach(RepairTalisman::repairAllItems);
					tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.repair.get());
				} else {
					tile.decrementActivityCooldown();
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.repair.get() != -1) {
			list.add(PELang.PEDESTAL_REPAIR_TALISMAN_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_REPAIR_TALISMAN_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.repair.get())));
		}
		return list;
	}

	@Override
	public void updateInAlchChest(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
		if (!world.isClientSide) {
			AlchChestTile tile = WorldHelper.getTileEntity(AlchChestTile.class, world, pos, true);
			if (tile != null) {
				CompoundTag nbt = stack.getOrCreateTag();
				byte coolDown = nbt.getByte(Constants.NBT_KEY_COOLDOWN);
				if (coolDown > 0) {
					nbt.putByte(Constants.NBT_KEY_COOLDOWN, (byte) (coolDown - 1));
				} else {
					tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
						if (repairAllItems(inv, CAN_REPAIR_ITEM)) {
							nbt.putByte(Constants.NBT_KEY_COOLDOWN, (byte) 19);
							//Note: We don't need to recheck comparators as repairing doesn't change the number
							// of items in slots
							tile.markDirty(false);
						}
					});
				}
			}
		}
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull Player player, @Nonnull ItemStack stack) {
		if (player.getCommandSenderWorld().isClientSide) {
			return false;
		}
		CompoundTag nbt = stack.getOrCreateTag();
		byte coolDown = nbt.getByte(Constants.NBT_KEY_COOLDOWN);
		if (coolDown > 0) {
			nbt.putByte(Constants.NBT_KEY_COOLDOWN, (byte) (coolDown - 1));
		} else if (repairAllItems(inv, CAN_REPAIR_ITEM)) {
			nbt.putByte(Constants.NBT_KEY_COOLDOWN, (byte) 19);
			return true;
		}
		return false;
	}

	private static void repairAllItems(Player player) {
		Predicate<ItemStack> canRepairPlayerItem = CAN_REPAIR_ITEM.and(stack -> stack != player.getMainHandItem() || !player.swinging);
		player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> repairAllItems(inv, canRepairPlayerItem));
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			repairAllItems(curios, canRepairPlayerItem);
		}
	}

	private static boolean repairAllItems(IItemHandler inv, Predicate<ItemStack> canRepairStack) {
		boolean hasAction = false;
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack invStack = inv.getStackInSlot(i);
			if (canRepairStack.test(invStack)) {
				invStack.setDamageValue(invStack.getDamageValue() - 1);
				if (!hasAction) {
					hasAction = true;
				}
			}
		}
		return hasAction;
	}
}