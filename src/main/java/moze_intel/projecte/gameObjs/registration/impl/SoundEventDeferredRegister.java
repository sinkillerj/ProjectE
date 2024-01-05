package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

public class SoundEventDeferredRegister extends PEDeferredRegister<SoundEvent> {

	public SoundEventDeferredRegister(String modid) {
		super(Registries.SOUND_EVENT, modid, SoundEventRegistryObject::new);
	}

	public SoundEventRegistryObject<SoundEvent> register(String name) {
		return register(name, SoundEvent::createVariableRangeEvent);
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <SOUND extends SoundEvent> SoundEventRegistryObject<SOUND> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends SOUND> func) {
		return (SoundEventRegistryObject<SOUND>) super.register(name, func);
	}

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public <SOUND extends SoundEvent> SoundEventRegistryObject<SOUND> register(@NotNull String name, @NotNull Supplier<? extends SOUND> sup) {
		return (SoundEventRegistryObject<SOUND>) super.register(name, sup);
	}

	@NotNull
	@Override
	protected <SOUND extends SoundEvent> SoundEventRegistryObject<SOUND> createHolder(@NotNull ResourceKey<? extends Registry<SoundEvent>> registryKey, @NotNull ResourceLocation key) {
		return new SoundEventRegistryObject<>(ResourceKey.create(registryKey, key));
	}
}