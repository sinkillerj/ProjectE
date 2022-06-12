package moze_intel.projecte;

import moze_intel.projecte.client.PEBlockStateProvider;
import moze_intel.projecte.client.PEItemModelProvider;
import moze_intel.projecte.client.lang.PELangProvider;
import moze_intel.projecte.client.sound.PESoundProvider;
import moze_intel.projecte.common.PEAdvancementsProvider;
import moze_intel.projecte.common.PECustomConversionProvider;
import moze_intel.projecte.common.loot.PELootProvider;
import moze_intel.projecte.common.recipe.PERecipeProvider;
import moze_intel.projecte.common.tag.PEBlockEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEBlockTagsProvider;
import moze_intel.projecte.common.tag.PEEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = PECore.MODID, bus = Bus.MOD)
public class ProjectEDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		//Client side data generators
		gen.addProvider(event.includeClient(), new PELangProvider(gen));
		gen.addProvider(event.includeClient(), new PESoundProvider(gen, existingFileHelper));
		gen.addProvider(event.includeClient(), new PEBlockStateProvider(gen, existingFileHelper));
		gen.addProvider(event.includeClient(), new PEItemModelProvider(gen, existingFileHelper));
		//Server side data generators
		//Tag data generators
		PEBlockTagsProvider blockTagsProvider = new PEBlockTagsProvider(gen, existingFileHelper);
		gen.addProvider(event.includeServer(), blockTagsProvider);
		gen.addProvider(event.includeServer(), new PEItemTagsProvider(gen, blockTagsProvider, existingFileHelper));
		gen.addProvider(event.includeServer(), new PEEntityTypeTagsProvider(gen, existingFileHelper));
		gen.addProvider(event.includeServer(), new PEBlockEntityTypeTagsProvider(gen, existingFileHelper));
		//Other generators (after tags in case we need them to exist)
		gen.addProvider(event.includeServer(), new PEAdvancementsProvider(gen, existingFileHelper));
		gen.addProvider(event.includeServer(), new PELootProvider(gen));
		gen.addProvider(event.includeServer(), new PERecipeProvider(gen));
		gen.addProvider(event.includeServer(), new PECustomConversionProvider(gen));
	}
}