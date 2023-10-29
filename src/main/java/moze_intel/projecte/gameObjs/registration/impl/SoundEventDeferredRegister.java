package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventDeferredRegister extends WrappedDeferredRegister<SoundEvent> {

	public SoundEventDeferredRegister(String modid) {
		super(ForgeRegistries.SOUND_EVENTS, modid);
	}

	public SoundEventRegistryObject<SoundEvent> register(String name) {
		return register(name, () -> SoundEvent.createVariableRangeEvent(PECore.rl(name)), SoundEventRegistryObject::new);
	}
}