package moze_intel.projecte.gameObjs.registration;

import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class DoubleWrappedRegistryObject<PRIMARY_REGISTRY, PRIMARY extends PRIMARY_REGISTRY, SECONDARY_REGISTRY, SECONDARY extends SECONDARY_REGISTRY> implements INamedEntry {

	protected final DeferredHolder<PRIMARY_REGISTRY, PRIMARY> primaryRO;
	protected final DeferredHolder<SECONDARY_REGISTRY, SECONDARY> secondaryRO;

	public DoubleWrappedRegistryObject(DeferredHolder<PRIMARY_REGISTRY, PRIMARY> primaryRO, DeferredHolder<SECONDARY_REGISTRY, SECONDARY> secondaryRO) {
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
	public String getName() {
		return primaryRO.getId().getPath();
	}
}