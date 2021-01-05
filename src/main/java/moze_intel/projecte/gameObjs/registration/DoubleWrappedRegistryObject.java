package moze_intel.projecte.gameObjs.registration;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
public class DoubleWrappedRegistryObject<PRIMARY extends IForgeRegistryEntry<? super PRIMARY>, SECONDARY extends IForgeRegistryEntry<? super SECONDARY>> implements INamedEntry {

	@Nonnull
	private final RegistryObject<PRIMARY> primaryRO;
	@Nonnull
	private final RegistryObject<SECONDARY> secondaryRO;

	public DoubleWrappedRegistryObject(RegistryObject<PRIMARY> primaryRO, RegistryObject<SECONDARY> secondaryRO) {
		this.primaryRO = primaryRO;
		this.secondaryRO = secondaryRO;
	}

	@Nonnull
	public PRIMARY getPrimary() {
		return primaryRO.get();
	}

	@Nonnull
	public SECONDARY getSecondary() {
		return secondaryRO.get();
	}

	@Override
	public String getInternalRegistryName() {
		return primaryRO.getId().getPath();
	}
}