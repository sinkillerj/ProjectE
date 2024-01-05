package moze_intel.projecte.gameObjs.registration;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class PEDeferredRegister<T> extends DeferredRegister<T> {

	@NotNull
	private final Function<ResourceKey<T>, ? extends PEDeferredHolder<T, ?>> holderCreator;

	public PEDeferredRegister(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull String namespace) {
		this(registryKey, namespace, PEDeferredHolder::new);
	}

	public PEDeferredRegister(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull String namespace,
			@NotNull Function<ResourceKey<T>, ? extends PEDeferredHolder<T, ? extends T>> holderCreator) {
		super(registryKey, namespace);
		this.holderCreator = holderCreator;
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <I extends T> PEDeferredHolder<T, I> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends I> func) {
		return (PEDeferredHolder<T, I>) super.register(name, func);
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <I extends T> PEDeferredHolder<T, I> register(@NotNull String name, @NotNull Supplier<? extends I> sup) {
		return (PEDeferredHolder<T, I>) super.register(name, sup);
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	protected <I extends T> PEDeferredHolder<T, I> createHolder(@NotNull ResourceKey<? extends Registry<T>> registryKey, @NotNull ResourceLocation key) {
		return (PEDeferredHolder<T, I>) holderCreator.apply(ResourceKey.create(registryKey, key));
	}
}