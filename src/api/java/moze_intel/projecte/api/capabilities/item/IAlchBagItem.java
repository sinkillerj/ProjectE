package moze_intel.projecte.api.capabilities.item;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;

/**
 * This interfaces specifies items that perform a specific function every tick when inside an Alchemical Bag, on a player
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, net.minecraft.core.Direction)}.
 *
 * @author williewillus
 */
public interface IAlchBagItem {

	/**
	 * Called on both client and server every time the alchemical bag ticks this item
	 *
	 * @param inv    The inventory of the bag
	 * @param player The player whose bag is being ticked
	 * @param stack  The ItemStack being ticked
	 *
	 * @return Whether the inventory was changed by this item ticking
	 */
	boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull Player player, @Nonnull ItemStack stack);
}