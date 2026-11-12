package moze_intel.projecte.gameObjs.registration;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class DeferredCodecRegister<T> extends PEDeferredRegister<MapCodec<? extends T>> {

	public DeferredCodecRegister(ResourceKey<? extends Registry<MapCodec<? extends T>>> registryKey, String namespace) {
		this(registryKey, namespace, DeferredCodecHolder::new);
	}

	public DeferredCodecRegister(ResourceKey<? extends Registry<MapCodec<? extends T>>> registryKey, String namespace,
			Function<ResourceKey<MapCodec<? extends T>>, ? extends DeferredCodecHolder<T, ? extends T>> holderCreator) {
		super(registryKey, namespace, holderCreator);
	}

	public <I extends T> DeferredCodecHolder<T, I> registerUnit(String name, Supplier<I> sup) {
		return registerCodec(name, () -> MapCodec.unit(sup));
	}

	public <I extends T> DeferredCodecHolder<T, I> registerCodec(String name, Function<Identifier, MapCodec<I>> func) {
		return (DeferredCodecHolder<T, I>) super.register(name, func);
	}

	public <I extends T> DeferredCodecHolder<T, I> registerCodec(String name, Supplier<MapCodec<I>> sup) {
		return (DeferredCodecHolder<T, I>) register(name, sup);
	}
}
