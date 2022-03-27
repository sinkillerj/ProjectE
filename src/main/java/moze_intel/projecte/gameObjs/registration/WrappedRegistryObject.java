package moze_intel.projecte.gameObjs.registration;

import java.util.function.Supplier;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class WrappedRegistryObject<T extends IForgeRegistryEntry<? super T>> implements Supplier<T>, INamedEntry {

	@NotNull
	protected RegistryObject<T> registryObject;

	protected WrappedRegistryObject(@NotNull RegistryObject<T> registryObject) {
		this.registryObject = registryObject;
	}

	@NotNull
	@Override
	public T get() {
		return registryObject.get();
	}

	@Override
	public String getInternalRegistryName() {
		return registryObject.getId().getPath();
	}
}