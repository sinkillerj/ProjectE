package moze_intel.projecte.gameObjs.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Internal interface for PlayerChecks.
 */
public interface IFlightProvider {

	/**
	 * @return If this stack currently should provide its bearer flight
	 */
	boolean canProvideFlight(ItemStack stack, ServerPlayer player);
}