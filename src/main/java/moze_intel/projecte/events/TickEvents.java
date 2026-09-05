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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.IItemHandler;

@EventBusSubscriber(modid = PECore.MODID)
public class TickEvents {

	//Reusable EnumSets to avoid per-tick allocation. Player tick events fire sequentially on the main thread.
	private static final EnumSet<DyeColor> COLORS_PRESENT = EnumSet.noneOf(DyeColor.class);
	private static final EnumSet<DyeColor> COLORS_CHANGED = EnumSet.noneOf(DyeColor.class);

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		IAlchBagProvider provider = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
		if (provider != null) {
			COLORS_CHANGED.clear();
			collectBagColorsPresent(player, COLORS_PRESENT);
			for (DyeColor color : COLORS_PRESENT) {
				IItemHandler inv = provider.getBag(color);
				for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
					ItemStack current = inv.getStackInSlot(i);
					IAlchBagItem alchBagItem = current.getCapability(PECapabilities.ALCH_BAG_ITEM_CAPABILITY);
					if (alchBagItem != null && alchBagItem.updateInAlchBag(inv, player, current)) {
						COLORS_CHANGED.add(color);
					}
				}
			}
			COLORS_PRESENT.clear();

			if (player instanceof ServerPlayer serverPlayer) {
				//Only sync for when it ticks on the server
				if (serverPlayer.containerMenu instanceof AlchBagContainer container && serverPlayer.getItemInHand(container.hand).getItem() instanceof AlchemicalBag bag) {
					// Do not sync if this color is open, the container system does it for us and we'll stay out of its way.
					COLORS_CHANGED.remove(bag.color);
				}
				provider.sync(serverPlayer, COLORS_CHANGED);
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
		for (ItemStack stack : player.getArmorSlots()) {
			if (!stack.isEmpty() && stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, player)) {
				return true;
			}
		}
		return PlayerHelper.checkHotbarCurios(player, (p, stack) -> stack.getItem() instanceof IFireProtector protector && protector.canProtectAgainstFire(stack, p));
	}

	private static void collectBagColorsPresent(Player player, Set<DyeColor> bagsPresent) {
		bagsPresent.clear();
		IItemHandler inv = player.getCapability(ItemHandler.ENTITY);
		if (inv != null) {
			for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof AlchemicalBag bag) {
					bagsPresent.add(bag.color);
				}
			}
		}
	}
}