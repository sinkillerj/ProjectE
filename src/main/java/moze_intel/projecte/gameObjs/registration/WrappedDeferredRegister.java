package moze_intel.projecte.gameObjs.registration;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class WrappedDeferredRegister<T> {

	@NotNull
	protected final DeferredRegister<T> internal;

	protected WrappedDeferredRegister(@NotNull DeferredRegister<T> internal) {
		this.internal = internal;
	}

	protected WrappedDeferredRegister(IForgeRegistry<T> registry, String modid) {
		this(DeferredRegister.create(registry, modid));
	}

	protected WrappedDeferredRegister(ResourceKey<? extends Registry<T>> registryName, String modid) {
		this(DeferredRegister.create(registryName, modid));
	}

	protected <I extends T, W extends WrappedRegistryObject<I>> W register(String name, Supplier<? extends I> sup, Function<RegistryObject<I>, W> objectWrapper) {
		return objectWrapper.apply(internal.register(name, sup));
	}

	public void register(IEventBus bus) {
		internal.register(bus);
	}
}