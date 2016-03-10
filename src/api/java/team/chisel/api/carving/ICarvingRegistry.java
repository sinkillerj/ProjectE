package team.chisel.api.carving;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Represents a registry of {@link ICarvingGroup}s
 * <p>
 * To obtain chisel's instance of this class, use {@link CarvingUtils#getChiselRegistry()}
 */
public interface ICarvingRegistry {

    /* Getters */

    /**
     * Finds the group the block/meta pair belongs to in the registry.
     * 
     * @param state
     *            The state of the variation
     * @return The {@link ICarvingGroup} that the block/meta pair belongs to
     */
    ICarvingGroup getGroup(IBlockState state);

    /**
     * Finds the group the ItemStack belongs to in the registry.
     * 
     * @param stack
     *            The ItemStack of the variation
     * @return The {@link ICarvingGroup} that the ItemStack pair belongs to
     */
    ICarvingGroup getGroup(ItemStack stack);

    /**
     * Gets an {@link ICarvingGroup} by its name.
     * 
     * @param name
     *            The name of the group
     * @return An {@link ICarvingGroup}
     */
    ICarvingGroup getGroup(String name);

    /**
     * Gets the {@link ICarvingVariation} instance represented by this block/meta pair.
     * 
     * @param state
     *            The state of the variation
     * @return The {@link ICarvingVariation} containing this block/meta pair
     */
    ICarvingVariation getVariation(IBlockState state);

    /**
     * Gets the {@link ICarvingVariation} instance represented by this stack.
     * 
     * @param stack
     *            The ItemStack of the variation
     * 
     * @return The {@link ICarvingVariation} containing this stack
     */
    ICarvingVariation getVariation(ItemStack stack);

    /**
     * Gets the list of {@link ICarvingVariation}s from the group that contains this block/meta pair.
     * 
     * @param state
     *            The state of the variation
     * @return All of the {@link ICarvingVariation}s in the group that contains this block/meta pair
     */
    List<ICarvingVariation> getGroupVariations(IBlockState state);

    /**
     * Gets the oredict name for the group that contains this block/meta pair.
     * 
     * @param state
     *            The state of the variation
     * @return A string oredict name for the group
     */
    String getOreName(IBlockState state);

    /**
     * Gets the possible output items for this {@link ItemStack}. To be used for machines/GUIs that chisel items.
     * 
     * @param chiseled
     *            The {@link ItemStack} being chiseled
     * @return A list of stacks that can be chiseled from the passed {@link ItemStack stack}
     */
    List<ItemStack> getItemsForChiseling(ItemStack chiseled);

    /**
     * Gets the sound resource string for the group represented by this block/meta pair.
     * 
     * @param state
     *            The state of the variation
     * @return The string resource for the sound that can be used in {@link World#playSound(double, double, double, String, float, float, boolean)} and other methods.
     */
    public String getVariationSound(IBlockState state);

    /**
     * Gets the sound resource string for the group represented by this ItemStack.
     * 
     * @param stack
     *            The ItemStack of the variation
     * 
     * @return The string resource for the sound that can be used in {@link World#playSound(double, double, double, String, float, float, boolean)} and other methods.
     */
    public String getVariationSound(ItemStack stack);

    /**
     * @return A list of all registered group names, sorted alphabetically.
     */
    List<String> getSortedGroupNames();

    /* Setters */

    /**
     * Adds a variation to the registry.
     * 
     * @param groupName
     *            The name of the group to add to.
     * @param state
     *            The state of the variation
     * @param order
     *            The order of the variation in the list of all variations in the group. Higher numbers are sorted at the end.
     */
    void addVariation(String groupName, IBlockState state, int order);

    /**
     * Adds a variation to the registry.
     * 
     * @param groupName
     *            The name of the group to add to
     * @param variation
     *            The {@link ICarvingVariation} to add
     */
    void addVariation(String groupName, ICarvingVariation variation);

    /**
     * Adds a group to the registry.
     * 
     * @param group
     *            The {@link ICarvingGroup} to add.
     */
    void addGroup(ICarvingGroup group);

    /**
     * Removes a group from the registry.
     * <p>
     * This in effect removes all variations associated with the group, though they are not explicitly removed from the object. If you maintain a reference to the {@link ICarvingGroup} that is
     * removed, it will still contain its variations.
     * 
     * @param groupName
     *            The name of the group to remove.
     * @return The {@link ICarvingGroup} that was removed.
     */
    ICarvingGroup removeGroup(String groupName);

    /**
     * Removes a varaition with the passed {@link Block} and metadata from the registry. If this variation is registered with mutiple groups, it will remove it from all of them.
     * 
     * @param state
     *            The {@link IBlockState} of the {@link ICarvingVariation variation}
     * @return The ICarvingVariation that was removed. Null if nothing was removed.
     */
    ICarvingVariation removeVariation(IBlockState state);

    /**
     * Removes a varaition with the passed {@link Block} and metadata from the registry, but only from the specified {@link ICarvingGroup} name.
     * 
     * @param state
     *            The {@link IBlockState} of the {@link ICarvingVariation variation}
     * @param group
     *            The name of the group that the variation should be removed from
     * @return The ICarvingVariation that was removed. Null if nothing was removed.
     */
    ICarvingVariation removeVariation(IBlockState state, String group);

    /**
     * Registers a group to an oredict name.
     * <p>
     * Doing this means that all blocks that are registered to this oredict name will act as if they are a part of this group.
     * 
     * @param groupName
     *            The name of the group
     * @param oreName
     *            The oredict name
     */
    void registerOre(String groupName, String oreName);

    /**
     * Sets the sound resource for a group.
     * <p>
     * This is the sound that is used when a block from this group is chiseled. <br/>
     * Does <i>not</i> need to be explicitly set.
     * 
     * @param name
     *            The name of the group
     * @param sound
     *            The resource string for the sound
     */
    void setVariationSound(String name, String sound);
}
