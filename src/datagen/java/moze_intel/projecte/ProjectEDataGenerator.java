package moze_intel.projecte;

import moze_intel.projecte.client.lang.PELangProvider;
import moze_intel.projecte.client.sound.PESoundProvider;
import moze_intel.projecte.common.PEAdvancementsProvider;
import moze_intel.projecte.common.loot.PELootProvider;
import moze_intel.projecte.common.recipe.PERecipeProvider;
import moze_intel.projecte.common.tag.PEBlockTagsProvider;
import moze_intel.projecte.common.tag.PEEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEItemTagsProvider;
import moze_intel.projecte.common.tag.PETileEntityTypeTagsProvider;
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
			gen.addProvider(new PESoundProvider(gen, existingFileHelper));
		}
		if (event.includeServer()) {
			//Server side data generators
			gen.addProvider(new PEAdvancementsProvider(gen));
			gen.addProvider(new PELootProvider(gen));
			gen.addProvider(new PERecipeProvider(gen));
			//Tag data generators
			PEBlockTagsProvider blockTagsProvider = new PEBlockTagsProvider(gen, existingFileHelper);
			gen.addProvider(blockTagsProvider);
			gen.addProvider(new PEItemTagsProvider(gen, blockTagsProvider, existingFileHelper));
			gen.addProvider(new PEEntityTypeTagsProvider(gen, existingFileHelper));
			gen.addProvider(new PETileEntityTypeTagsProvider(gen, existingFileHelper));
		}
	}
}