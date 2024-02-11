package moze_intel.projecte.gameObjs.registration;

import net.minecraft.resources.ResourceLocation;

public interface INamedEntry {

	/**
	 * Used for retrieving the path/name of a registry object before the registry object has been fully initialized
	 */
	default String getName() {
		return getId().getPath();
	}

	ResourceLocation getId();
}