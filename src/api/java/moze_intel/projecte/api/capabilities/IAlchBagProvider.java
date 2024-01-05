package moze_intel.projecte.api.capabilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines the contract for some object that exposes sixteen colored inventories, for the purpose of usage as Alchemical Bags.
 * <p>
 * This is exposed through the Capability system.
 * <p>
 * Acquire an instance of this using {@link net.minecraft.world.entity.Entity#getCapability(EntityCapability)}.
 */
public interface IAlchBagProvider {

	/**
	 * Note: modifying this clientside is not advised
	 *
	 * @param color The bag color to acquire
	 *
	 * @return The inventory representing this alchemical bag
	 */
	@NotNull
	IItemHandler getBag(@NotNull DyeColor color);

	/**
	 * Syncs the bag inventory associated with this color to the player provided (usually the owner of this capability instance)
	 *
	 * @param color  The bag color to sync. If null, syncs every color.
	 * @param player The player to sync the bags to.
	 */
	void sync(DyeColor color, @NotNull ServerPlayer player);
}