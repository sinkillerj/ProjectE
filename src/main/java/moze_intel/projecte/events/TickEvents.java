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
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.server.level.ServerPlayer;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;

@EventBusSubscriber(modid = PECore.MODID)
public class TickEvents {

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		IAlchBagProvider provider = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
		if (provider != null) {
			Set<DyeColor> colorsChanged = EnumSet.noneOf(DyeColor.class);
			for (DyeColor color : getBagColorsPresent(player)) {
				PEItemStacksHandler inv = provider.getBag(color);
				for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
					ItemStack current = inv.getStackInSlot(i);
					IAlchBagItem alchBagItem = current.getCapability(PECapabilities.ALCH_BAG_ITEM_CAPABILITY);
					if (alchBagItem != null && alchBagItem.updateInAlchBag(inv, player, current)) {
						colorsChanged.add(color);
					}
				}
			}

			if (player instanceof ServerPlayer serverPlayer) {
				//Only sync for when it ticks on the server
				if (serverPlayer.containerMenu instanceof AlchBagContainer container && serverPlayer.getItemInHand(container.hand).getItem() instanceof AlchemicalBag bag) {
					// Do not sync if this color is open, the container system does it for us and we'll stay out of its way.
					colorsChanged.remove(bag.color);
				}
				provider.sync(serverPlayer, colorsChanged);
			}
		}

		InternalAbilities.tick(player);
		if (!player.level().isClientSide()) {
			if (player.isOnFire() && shouldPlayerResistFire(player)) {
				player.clearFire();
			}
		}
	}

	public static boolean shouldPlayerResistFire(Player player) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!slot.isArmor()) {
				continue;
			}
			ItemStack stack = player.getItemBySlot(slot);
			if (!stack.isEmpty() && stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, player)) {
				return true;
			}
		}
		return PlayerHelper.checkHotbarCurios(player, (p, stack) -> stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, p));
	}

	private static Set<DyeColor> getBagColorsPresent(Player player) {
		Set<DyeColor> bagsPresent = EnumSet.noneOf(DyeColor.class);
		for (ItemStack stack : ItemHelper.getInventoryStacks(player.getInventory())) {
			if (!stack.isEmpty() && stack.getItem() instanceof AlchemicalBag bag) {
				bagsPresent.add(bag.color);
			}
		}
		return bagsPresent;
	}
}
