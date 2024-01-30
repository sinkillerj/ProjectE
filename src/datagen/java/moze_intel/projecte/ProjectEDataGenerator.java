package moze_intel.projecte;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.client.PEBlockStateProvider;
import moze_intel.projecte.client.PEItemModelProvider;
import moze_intel.projecte.client.PESpriteSourceProvider;
import moze_intel.projecte.client.lang.PELangProvider;
import moze_intel.projecte.client.sound.PESoundProvider;
import moze_intel.projecte.common.PEAdvancementsGenerator;
import moze_intel.projecte.common.PECustomConversionProvider;
import moze_intel.projecte.common.PEDatapackRegistryProvider;
import moze_intel.projecte.common.PEPackMetadataGenerator;
import moze_intel.projecte.common.loot.PEBlockLootTable;
import moze_intel.projecte.common.recipe.PERecipeProvider;
import moze_intel.projecte.common.tag.PEBlockEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEBlockTagsProvider;
import moze_intel.projecte.common.tag.PEDamageTypeTagsProvider;
import moze_intel.projecte.common.tag.PEEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEItemTagsProvider;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PECore.MODID, bus = Bus.MOD)
public class ProjectEDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		PEDatapackRegistryProvider drProvider = gen.addProvider(event.includeServer(), new PEDatapackRegistryProvider(output, event.getLookupProvider()));
		CompletableFuture<Provider> lookupProvider = drProvider.getRegistryProvider();

		gen.addProvider(true, new PEPackMetadataGenerator(output, PELang.PACK_DESCRIPTION));
		//Client side data generators
		gen.addProvider(event.includeClient(), new PELangProvider(output));
		gen.addProvider(event.includeClient(), new PESoundProvider(output, existingFileHelper));
		gen.addProvider(event.includeClient(), new PEBlockStateProvider(output, existingFileHelper));
		gen.addProvider(event.includeClient(), new PEItemModelProvider(output, existingFileHelper));
		gen.addProvider(event.includeClient(), new PESpriteSourceProvider(output, lookupProvider, existingFileHelper));
		//Server side data generators
		//Tag data generators
		PEBlockTagsProvider blockTagsProvider = gen.addProvider(event.includeServer(), new PEBlockTagsProvider(output, lookupProvider, existingFileHelper));
		gen.addProvider(event.includeServer(), new PEItemTagsProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
		gen.addProvider(event.includeServer(), new PEEntityTypeTagsProvider(output, lookupProvider, existingFileHelper));
		gen.addProvider(event.includeServer(), new PEBlockEntityTypeTagsProvider(output, lookupProvider, existingFileHelper));
		gen.addProvider(event.includeServer(), new PEDamageTypeTagsProvider(output, lookupProvider, existingFileHelper));
		//Other generators (after tags in case we need them to exist)
		gen.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, List.of(new PEAdvancementsGenerator())));
		gen.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), List.of(
				new SubProviderEntry(PEBlockLootTable::new, LootContextParamSets.BLOCK)
		)));
		gen.addProvider(event.includeServer(), new PERecipeProvider(output));
		gen.addProvider(event.includeServer(), new PECustomConversionProvider(output, lookupProvider));
	}
}