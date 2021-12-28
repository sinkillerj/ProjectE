package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventRegistryObject<SOUND extends SoundEvent> extends WrappedRegistryObject<SOUND> implements ILangEntry {

	private final String translationKey;

	public SoundEventRegistryObject(RegistryObject<SOUND> registryObject) {
		super(registryObject);
		translationKey = Util.makeDescriptionId("sound_event", this.registryObject.getId());
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}