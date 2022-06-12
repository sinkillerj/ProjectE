package moze_intel.projecte.gameObjs.registration;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class DoubleDeferredRegister<PRIMARY, SECONDARY> {

	@NotNull
	protected final DeferredRegister<PRIMARY> primaryRegister;
	@NotNull
	protected final DeferredRegister<SECONDARY> secondaryRegister;

	public DoubleDeferredRegister(IForgeRegistry<PRIMARY> primaryRegistry, IForgeRegistry<SECONDARY> secondaryRegistry, String modid) {
		primaryRegister = DeferredRegister.create(primaryRegistry, modid);
		secondaryRegister = DeferredRegister.create(secondaryRegistry, modid);
	}

	public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W register(String name, Supplier<? extends P> primarySupplier,
			Supplier<? extends S> secondarySupplier, BiFunction<RegistryObject<P>, RegistryObject<S>, W> objectWrapper) {
		return objectWrapper.apply(primaryRegister.register(name, primarySupplier), secondaryRegister.register(name, secondarySupplier));
	}

	public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W register(String name, Supplier<? extends P> primarySupplier,
			Function<P, S> secondarySupplier, BiFunction<RegistryObject<P>, RegistryObject<S>, W> objectWrapper) {
		RegistryObject<P> primaryObject = primaryRegister.register(name, primarySupplier);
		return objectWrapper.apply(primaryObject, secondaryRegister.register(name, () -> secondarySupplier.apply(primaryObject.get())));
	}

	public void register(IEventBus bus) {
		primaryRegister.register(bus);
		secondaryRegister.register(bus);
	}
}