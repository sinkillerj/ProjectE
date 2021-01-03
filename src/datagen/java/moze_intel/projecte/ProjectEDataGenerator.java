package moze_intel.projecte;

import moze_intel.projecte.client.lang.PELangProvider;
import moze_intel.projecte.common.tag.PEEntityTypeTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = PECore.MODID, bus = Bus.MOD)
public class ProjectEDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		if (event.includeClient()) {
			//Client side data generators
			gen.addProvider(new PELangProvider(gen));
		}
		if (event.includeServer()) {
			//Server side data generators
			gen.addProvider(new PEEntityTypeTagsProvider(gen, existingFileHelper));
		}
	}
}