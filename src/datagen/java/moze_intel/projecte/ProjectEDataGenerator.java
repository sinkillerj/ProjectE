package moze_intel.projecte;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.client.PEModelProvider;
import moze_intel.projecte.client.PESpriteSourceProvider;
import moze_intel.projecte.client.lang.PELangProvider;
import moze_intel.projecte.client.sound.PESoundProvider;
import moze_intel.projecte.common.PECustomConversionProvider;
import moze_intel.projecte.common.PEAdvancementsGenerator;
import moze_intel.projecte.common.loot.PEBlockLootTable;
import moze_intel.projecte.common.recipe.PERecipeProvider;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import moze_intel.projecte.common.PEPackMetadataGenerator;
import moze_intel.projecte.common.PEWorldTransmutationProvider;
import moze_intel.projecte.common.tag.PEBlockEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEBlockTagsProvider;
import moze_intel.projecte.common.tag.PEDamageTypeTagsProvider;
import moze_intel.projecte.common.tag.PEEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEItemTagsProvider;
import moze_intel.projecte.common.tag.PEPotionsTagsProvider;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.HolderLookup.Provider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PECore.MODID)
public class ProjectEDataGenerator {

	@SubscribeEvent
	public static void gatherRegistries(GatherDataRegistryEntriesEvent event) {
        event.add(Registries.DAMAGE_TYPE, context -> {
            for (var damageType : PEDamageTypes.DAMAGE_TYPES.values()) {
                context.register(damageType.key(), new DamageType(damageType.msgId(), damageType.exhaustion()));
            }
        }).advancement(PEAdvancementsGenerator::new)
          .recipe(PERecipeProvider::new)
          .lootTable(new LootTableProvider.SubProviderEntry(PEBlockLootTable::new, LootContextParamSets.BLOCK));
    }

	@SubscribeEvent
	public static void gatherServerData(GatherDataEvent.Server event) {
        gatherCommonData(event);
    }

    private static void gatherCommonData(GatherDataEvent event) {
		EMCMappingHandler.loadMappers();

		CompletableFuture<Provider> lookupProvider = event.getReloadableLookupProvider();

		event.createProvider((output, lookup) -> new PEBlockTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEItemTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEEntityTypeTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEBlockEntityTypeTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEDamageTypeTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEPotionsTagsProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PECustomConversionProvider(output, lookupProvider));
		event.createProvider((output, lookup) -> new PEWorldTransmutationProvider(output, lookupProvider));
	}

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
        gatherCommonData(event);

		CompletableFuture<Provider> lookupProvider = event.getReloadableLookupProvider();

		event.addProvider(new PEPackMetadataGenerator(event.getGenerator().getPackOutput(), PELang.PACK_DESCRIPTION));
		event.createProvider(PELangProvider::new);
		event.createProvider(PESoundProvider::new);
		event.createProvider(PEModelProvider::new);
		event.createProvider((output, lookup) -> new PESpriteSourceProvider(output, lookupProvider));

		if (ModList.get().isLoaded(IntegrationHelper.EMI_MODID)) {
			registerEmiProviders(event, lookupProvider);
		}
	}

	private static void registerEmiProviders(GatherDataEvent.Client event, CompletableFuture<Provider> lookupProvider) {
		try {
			Class<?> aliasMappingClass = Class.forName("moze_intel.projecte.integration.recipe_viewer.alias.ProjectEAliasMapping");
			Class<?> aliasProvider = Class.forName("moze_intel.projecte.client.integration.emi.EmiAliasProvider");
			Class<?> emiDefaults = Class.forName("moze_intel.projecte.client.integration.emi.ProjectEEmiDefaults");
			java.util.function.Supplier<?> mappingSupplier = () -> {
				try {
					return aliasMappingClass.getDeclaredConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			};
			event.createProvider((output, lookup) -> {
				try {
					return (net.minecraft.data.DataProvider) aliasProvider
							.getConstructor(net.minecraft.data.PackOutput.class, CompletableFuture.class, String.class, java.util.function.Supplier.class)
							.newInstance(output, lookupProvider, PECore.MODID, mappingSupplier);
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			});
			event.createProvider((output, lookup) -> {
				try {
					return (net.minecraft.data.DataProvider) emiDefaults
							.getConstructor(net.minecraft.data.PackOutput.class, CompletableFuture.class)
							.newInstance(output, lookupProvider);
				} catch (ReflectiveOperationException e) {
					throw new IllegalStateException(e);
				}
			});
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to register EMI data providers", e);
		}
	}
}
