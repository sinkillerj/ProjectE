package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventDeferredRegister extends WrappedDeferredRegister<SoundEvent> {

    public SoundEventDeferredRegister() {
        super(ForgeRegistries.SOUND_EVENTS);
    }

    public SoundEventRegistryObject<SoundEvent> register(String name) {
        return register(name, () -> new SoundEvent(PECore.rl(name)), SoundEventRegistryObject::new);
    }
}