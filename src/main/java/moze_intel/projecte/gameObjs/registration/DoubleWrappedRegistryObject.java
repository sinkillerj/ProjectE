package moze_intel.projecte.gameObjs.registration;

import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class DoubleWrappedRegistryObject<PRIMARY extends IForgeRegistryEntry<? super PRIMARY>, SECONDARY extends IForgeRegistryEntry<? super SECONDARY>> implements INamedEntry {

	@NotNull
	private final RegistryObject<PRIMARY> primaryRO;
	@NotNull
	private final RegistryObject<SECONDARY> secondaryRO;

	public DoubleWrappedRegistryObject(@NotNull RegistryObject<PRIMARY> primaryRO, @NotNull RegistryObject<SECONDARY> secondaryRO) {
		this.primaryRO = primaryRO;
		this.secondaryRO = secondaryRO;
	}

	@NotNull
	public PRIMARY getPrimary() {
		return primaryRO.get();
	}

	@NotNull
	public SECONDARY getSecondary() {
		return secondaryRO.get();
	}

	@Override
	public String getInternalRegistryName() {
		return primaryRO.getId().getPath();
	}
}