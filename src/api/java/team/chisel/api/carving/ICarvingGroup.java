package team.chisel.api.carving;

import java.util.List;

/**
 * Represents a group of chiselable blocks.
 * <p>
 * This is an object that contains a collection of {@link ICarvingVariation} objects, and keeps them sorted. You may sort them in any way (or not at all), and {@link ICarvingVariation#getOrder()} will
 * likely be of use to do so.
 * <p>
 * It also defines what sound and oredict name the group as a whole has.
 */
public interface ICarvingGroup {

	/**
	 * The name of this group. Used for internal identification.
	 * 
	 * @return A string name for the group
	 */
	String getName();

	/**
	 * Can return null. If null, fallback sound will be used (the standard chisel sound).
	 * 
	 * @return The string resource path of the sound to use for chiseling items in this group
	 */
	String getSound();

	/**
	 * Sets the sound of this group
	 * 
	 * @param sound
	 *            A string resource path for the sound this group makes when chiseled
	 */
	void setSound(String sound);

	/**
	 * The oredict name to match to this group. All items with this oredict name will be assumed to be part of this group.
	 * 
	 * @return An ore dictionary name
	 */
	String getOreName();

	/**
	 * Sets the oredict name for this group.
	 * 
	 * @param oreName
	 *            The String oredict name to be associated with this group.
	 */
	void setOreName(String oreName);

	/**
	 * Gets all carving variations associated with this group.
	 * 
	 * @return A {@link List} of {@link ICarvingVariation}s
	 */
	List<ICarvingVariation> getVariations();

	/**
	 * Adds a variation to this group. Do not call this from external code, as it will fail to remove the inverse lookup from the registry.
	 * 
	 * @param variation
	 *            An {@link ICarvingVariation} to add to this group
	 */
	void addVariation(ICarvingVariation variation);

	/**
	 * Removes a variation to this group. Do not call this from external code, as it will fail to remove the inverse lookup from the registry.
	 * 
	 * @param variation
	 *            An {@link ICarvingVariation} to add to this group
	 */
	boolean removeVariation(ICarvingVariation variation);
}
