package moze_intel.projecte.events;

import java.util.EnumSet;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.items.IItemHandler;

@Mod.EventBusSubscriber(modid = PECore.MODID)
public class TickEvents {

	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Player player = event.player;
			IAlchBagProvider provider = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
			if (provider != null) {
				Set<DyeColor> colorsChanged = EnumSet.noneOf(DyeColor.class);
				for (DyeColor color : getBagColorsPresent(player)) {
					IItemHandler inv = provider.getBag(color);
					for (int i = 0; i < inv.getSlots(); i++) {
						ItemStack current = inv.getStackInSlot(i);
						if (!current.isEmpty()) {
							IAlchBagItem alchBagItem = current.getCapability(PECapabilities.ALCH_BAG_ITEM_CAPABILITY);
							if (alchBagItem != null && alchBagItem.updateInAlchBag(inv, player, current)) {
								colorsChanged.add(color);
							}
						}
					}
				}

				if (player instanceof ServerPlayer serverPlayer) {
					//Only sync for when it ticks on the server
					for (DyeColor e : colorsChanged) {
						if (serverPlayer.containerMenu instanceof AlchBagContainer container) {
							ItemStack heldItem = serverPlayer.getItemInHand(container.hand);
							if (heldItem.getItem() instanceof AlchemicalBag bag && bag.color == e) {
								// Do not sync if this color is open, the container system does it for us
								// and we'll stay out of its way.
								continue;
							}
						}
						provider.sync(e, serverPlayer);
					}
				}
			}

			player.getData(PEAttachmentTypes.COMMON_INTERNAL_ABILITIES).tick(player);
			if (event.side.isServer()) {
				player.getData(PEAttachmentTypes.INTERNAL_ABILITIES).tick(player);
				player.getData(PEAttachmentTypes.INTERNAL_TIMERS).tick();
				if (player.isOnFire() && shouldPlayerResistFire(player)) {
					player.clearFire();
				}
			}
		}
	}

	public static boolean shouldPlayerResistFire(Player player) {
		for (ItemStack stack : player.getArmorSlots()) {
			if (!stack.isEmpty() && stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, player)) {
				return true;
			}
		}
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, player)) {
				return true;
			}
		}
		IItemHandler curios = player.getCapability(IntegrationHelper.CURIO_ITEM_HANDLER);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, player)) {
					return true;
				}
			}
		}
		return false;
	}

	private static Set<DyeColor> getBagColorsPresent(Player player) {
		Set<DyeColor> bagsPresent = EnumSet.noneOf(DyeColor.class);
		IItemHandler inv = player.getCapability(ItemHandler.ENTITY);
		if (inv != null) {
			for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof AlchemicalBag bag) {
					bagsPresent.add(bag.color);
				}
			}
		}
		return bagsPresent;
	}
}