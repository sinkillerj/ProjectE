package moze_intel.projecte.api.capabilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

/**
 * This interface defines the contract for some object that exposes transmutation knowledge through the Capability system
 */
public interface IKnowledgeProvider extends INBTSerializable<NBTTagCompound>
{

    /**
     * Note: calling this clientside is not advised
     * Clears all knowledge
     */
    public void clearKnowledge();

    /**
     * @param stack The stack to query
     * @return Whether the player has transmutation knowledge for this stack
     */
    public boolean hasKnowledge(ItemStack stack);

    /**
     * Note: calling this clientside is not advised
     * @param stack The stack to add to knowledge
     * @return Whether the operation was successful
     */
    public boolean addKnowledge(ItemStack stack);

    /**
     * Note: calling this clientside is not advised
     * @param stack The stack to remove from knowledge
     * @return Whether the operation was successful
     */
    public boolean removeKnowledge(ItemStack stack);

    /**
     * @return An immutable copy of the knowledge list
     */
    public List<ItemStack> getKnowledge();

    /**
     * @return A copy of the player's input and lock slots
     */
    public ItemStack[] getInputAndLocks();

    /**
     * Note: calling this clientside is not advised
     * @param stacks The input and lock slots todo clarify indices, is this even needed?
     */
    public void setInputAndLocks(ItemStack[] stacks);

    /**
     * @return The emc in this player's transmutation tablet network
     */
    public double getEmc();

    /**
     * Note: calling this clientside is not advised
     * @param emc The emc to set in this player's transmutation tablet network
     */
    public void setEmc(double emc);

    /**
     * Syncs this to the player provided (usually the owner of this capability instance)
     * @param player The player to sync to.
     */
    public void sync(EntityPlayerMP player);

}
