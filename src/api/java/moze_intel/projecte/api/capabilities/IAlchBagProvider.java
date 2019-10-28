package moze_intel.projecte.api.capabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * This interface defines the contract for some object that exposes sixteen colored inventories, for the purpose of usage as Alchemical Bags.
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link net.minecraft.entity.Entity#getCapability(Capability, Direction)}.
 */
public interface IAlchBagProvider extends INBTSerializable<CompoundNBT>
{

    /**
     * Note: modifying this clientside is not advised
     * @param color The bag color to acquire
     * @return The inventory representing this alchemical bag
     */
    @Nonnull IItemHandler getBag(@Nonnull DyeColor color);

    /**
     * Syncs the bag inventory associated with this color to the player provided (usually the owner of this capability instance)
     * @param color The bag color to sync. If null, syncs every color.
     * @param player The player to sync the bags to.
     */
    void sync(DyeColor color, @Nonnull ServerPlayerEntity player);

}
