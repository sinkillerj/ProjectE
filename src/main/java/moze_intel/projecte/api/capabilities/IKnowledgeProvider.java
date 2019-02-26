package moze_intel.projecte.api.capabilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This interface defines the contract for some object that exposes transmutation knowledge through the Capability system.
 * Acquire an instance of this using {@link net.minecraft.entity.Entity#getCapability(Capability, EnumFacing)}.
 */
public interface IKnowledgeProvider extends INBTSerializable<NBTTagCompound>
{

    /**
     * @return Whether the player has the "tome" flag set, meaning all knowledge checks automatically return true
     */
    boolean hasFullKnowledge();

    /**
     * @param fullKnowledge Whether the player has the "tome" flag set, meaning all knowledge checks automatically return true
     */
    void setFullKnowledge(boolean fullKnowledge);

    /**
     * Clears all knowledge. Additionally, clears the "tome" flag.
     */
    void clearKnowledge();

    /**
     * @param stack The stack to query
     * @return Whether the player has transmutation knowledge for this stack
     */
    boolean hasKnowledge(@Nonnull ItemStack stack);

    /**
     * @param stack The stack to add to knowledge
     * @return Whether the operation was successful
     */
    boolean addKnowledge(@Nonnull ItemStack stack);

    /**
     * @param stack The stack to remove from knowledge
     * @return Whether the operation was successful
     */
    boolean removeKnowledge(@Nonnull ItemStack stack);

    /**
     * @return An unmodifiable but live view of the knowledge list.
     */
    @Nonnull List<ItemStack> getKnowledge();

    /**
     * @return The player's input and lock slots
     */
    @Nonnull IItemHandler getInputAndLocks();

    /**
     * @return The emc in this player's transmutation tablet network
     */
    long getEmc();

    /**
     * @param emc The emc to set in this player's transmutation tablet network
     */
    void setEmc(long emc);

    /**
     * @param emc The emc to set in this player's transmutation tablet network
     * @deprecated 
     */
    @Deprecated
    default void setEmc(double emc) {
        setEmc((long) emc);
    }

    /**
     * @param player The player to sync to.
     */
    void sync(@Nonnull EntityPlayerMP player);

}
