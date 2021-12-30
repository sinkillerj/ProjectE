package moze_intel.projecte.api.block_entity;

import net.minecraft.world.phys.AABB;

public interface IDMPedestal {

	/**
	 * @return Pedestal's current cooldown
	 */
	int getActivityCooldown();

	/**
	 * Sets the pedestal's cooldown
	 */
	void setActivityCooldown(int cooldown);

	/**
	 * Decrement pedestal cooldown
	 */
	void decrementActivityCooldown();

	/**
	 * @return Inclusive bounding box of all positions this pedestal should apply effects in
	 */
	AABB getEffectBounds();
}