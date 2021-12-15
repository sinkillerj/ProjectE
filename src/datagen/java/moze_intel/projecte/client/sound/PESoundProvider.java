package moze_intel.projecte.client.sound;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.SoundEventRegistryObject;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class PESoundProvider extends SoundDefinitionsProvider {

	public PESoundProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, PECore.MODID, existingFileHelper);
	}

	protected void addSoundEventWithSubtitle(SoundEventRegistryObject<?> soundEventRO, ResourceLocation location) {
		add(soundEventRO.get(), SoundDefinition.definition().subtitle(soundEventRO.getTranslationKey()).with(sound(location)));
	}

	@Override
	public void registerSounds() {
		addSoundEventWithSubtitle(PESoundEvents.WIND_MAGIC, PECore.rl("item/pewindmagic"));
		addSoundEventWithSubtitle(PESoundEvents.WATER_MAGIC, PECore.rl("item/pewatermagic"));
		addSoundEventWithSubtitle(PESoundEvents.POWER, PECore.rl("item/pepower"));
		addSoundEventWithSubtitle(PESoundEvents.HEAL, PECore.rl("item/peheal"));
		addSoundEventWithSubtitle(PESoundEvents.DESTRUCT, PECore.rl("item/pedestruct"));
		addSoundEventWithSubtitle(PESoundEvents.CHARGE, PECore.rl("item/pecharge"));
		addSoundEventWithSubtitle(PESoundEvents.UNCHARGE, PECore.rl("item/peuncharge"));
		addSoundEventWithSubtitle(PESoundEvents.TRANSMUTE, PECore.rl("item/petransmute"));
		//TODO: Evaluate the remaining sounds that we don't actually use anywhere
	}
}