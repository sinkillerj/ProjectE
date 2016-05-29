package moze_intel.projecte.api.capabilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public boolean hasFullKnowledge();

    /**
     * @param fullKnowledge Whether the player has the "tome" flag set, meaning all knowledge checks automatically return true
     */
    public void setFullKnowledge(boolean fullKnowledge);

    /**
     * Clears all knowledge. Additionally, clears the "tome" flag.
     */
    public void clearKnowledge();

    /**
     * @param stack The stack to query
     * @return Whether the player has transmutation knowledge for this stack
     */
    public boolean hasKnowledge(@Nullable ItemStack stack);

    /**
     * @param stack The stack to add to knowledge
     * @return Whether the operation was successful
     */
    public boolean addKnowledge(@Nonnull ItemStack stack);

    /**
     * @param stack The stack to remove from knowledge
     * @return Whether the operation was successful
     */
    public boolean removeKnowledge(@Nonnull ItemStack stack);

    /**
     * @return A mutable copy of the knowledge list. Changes to this List do not change the actual knowledge
     */
    public @Nonnull List<ItemStack> getKnowledge();

    /**
     * @return The player's input and lock slots
     */
    public @Nonnull IItemHandler getInputAndLocks();

    /**
     * @return The emc in this player's transmutation tablet network
     */
    public double getEmc();

    /**
     * @param emc The emc to set in this player's transmutation tablet network
     */
    public void setEmc(double emc);

    /**
     * @param player The player to sync to.
     */
    public void sync(@Nonnull EntityPlayerMP player);

}
