package moze_intel.projecte.client.sound;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PESoundProvider extends BaseSoundProvider {

	public PESoundProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, existingFileHelper, PECore.MODID);
	}

	@Override
	protected void addSoundEvents() {
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