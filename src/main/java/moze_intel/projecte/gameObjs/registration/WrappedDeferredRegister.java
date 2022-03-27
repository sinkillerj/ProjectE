package moze_intel.projecte.gameObjs.registration;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class WrappedDeferredRegister<T extends IForgeRegistryEntry<T>> {

	@NotNull
	protected final DeferredRegister<T> internal;

	protected WrappedDeferredRegister(IForgeRegistry<T> registry, String modid) {
		internal = DeferredRegister.create(registry, modid);
	}

	protected <I extends T, W extends WrappedRegistryObject<I>> W register(String name, Supplier<? extends I> sup, Function<RegistryObject<I>, W> objectWrapper) {
		return objectWrapper.apply(internal.register(name, sup));
	}

	public void register(IEventBus bus) {
		internal.register(bus);
	}
}