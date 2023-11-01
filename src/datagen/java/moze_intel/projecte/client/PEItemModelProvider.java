package moze_intel.projecte.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import moze_intel.projecte.ClientRegistration;
import moze_intel.projecte.FieldReflectionHelper;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.RegistryUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PEItemModelProvider extends ItemModelProvider {

	@SuppressWarnings("rawtypes")
	private final FieldReflectionHelper<ModelBuilder, Map<String, String>> MODEL_TEXTURES = new FieldReflectionHelper<>(ModelBuilder.class, "textures", HashMap::new);
	private static final TrimModelDataHelper<?> TRIM_HELPER = new TrimModelDataHelper<>();

	public PEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		blockParentModel(PEBlocks.ALCHEMICAL_COAL, PEBlocks.MOBIUS_FUEL, PEBlocks.AETERNALIS_FUEL, PEBlocks.DARK_MATTER, PEBlocks.RED_MATTER,
				PEBlocks.DARK_MATTER_PEDESTAL, PEBlocks.DARK_MATTER_FURNACE, PEBlocks.RED_MATTER_FURNACE, PEBlocks.COLLECTOR, PEBlocks.COLLECTOR_MK2,
				PEBlocks.COLLECTOR_MK3, PEBlocks.NOVA_CATALYST, PEBlocks.NOVA_CATACLYSM, PEBlocks.TRANSMUTATION_TABLE, PEBlocks.RELAY, PEBlocks.RELAY_MK2,
				PEBlocks.RELAY_MK3);
		registerGenerated(PEItems.CATALYTIC_LENS, PEItems.DESTRUCTION_CATALYST, PEItems.LOW_DIVINING_ROD, PEItems.MEDIUM_DIVINING_ROD, PEItems.HIGH_DIVINING_ROD,
				PEItems.HYPERKINETIC_LENS, PEItems.MERCURIAL_EYE, PEItems.PHILOSOPHERS_STONE, PEItems.REPAIR_TALISMAN, PEItems.TOME_OF_KNOWLEDGE,
				PEItems.TRANSMUTATION_TABLET);
		generated(PEItems.ALCHEMICAL_COAL, modLoc("item/fuels/alchemical_coal"));
		generated(PEItems.MOBIUS_FUEL, modLoc("item/fuels/mobius"));
		generated(PEItems.AETERNALIS_FUEL, modLoc("item/fuels/aeternalis"));
		generated(PEItems.DARK_MATTER, modLoc("item/matter/dark"));
		generated(PEItems.RED_MATTER, modLoc("item/matter/red"));
		generated(PEItems.LOW_COVALENCE_DUST, modLoc("item/covalence_dust/low"));
		generated(PEItems.MEDIUM_COVALENCE_DUST, modLoc("item/covalence_dust/medium"));
		generated(PEItems.HIGH_COVALENCE_DUST, modLoc("item/covalence_dust/high"));
		generated(PEBlocks.INTERDICTION_TORCH, modLoc("block/interdiction_torch"));
		generateAlchemicalBags();
		generateChests();
		generateRings();
		generateKleinStars();
		generateGear();
		generated(PEItems.GEM_OF_ETERNAL_DENSITY, modLoc("item/dense_gem_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("gem_of_eternal_density_on", modLoc("item/dense_gem_on")))
				.end();
		//Note: We don't actually have a manual, but I moved this model over to data gen anyways
		generated("manual", modLoc("item/book"));
	}

	private void generateAlchemicalBags() {
		for (DyeColor color : DyeColor.values()) {
			generated(PEItems.getBag(color), modLoc("item/alchemy_bags/" + color));
		}
	}

	private void generateChests() {
		generateChest(PEBlocks.ALCHEMICAL_CHEST);
		generateChest(PEBlocks.CONDENSER);
		generateChest(PEBlocks.CONDENSER_MK2);
	}

	private void generateChest(BlockRegistryObject<?, ?> block) {
		String name = getName(block);
		withExistingParent(name, modLoc("block/base_chest")).texture("chest", modLoc("block/" + name));
	}

	private void generateRings() {
		getBuilder(getName(PEItems.ARCANA_RING))
				//Zero Off
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 0)
				.predicate(ClientRegistration.MODE_OVERRIDE, 0)
				.model(generated("arcana_zero_off", modLoc("item/rings/arcana_0")))
				.end()
				//Zero On
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.predicate(ClientRegistration.MODE_OVERRIDE, 0)
				.model(generated("arcana_zero_on", modLoc("item/rings/arcana_0_on")))
				.end()
				//Ignition Off
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 0)
				.predicate(ClientRegistration.MODE_OVERRIDE, 1)
				.model(generated("arcana_ignition_off", modLoc("item/rings/arcana_1")))
				.end()
				//Ignition On
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.predicate(ClientRegistration.MODE_OVERRIDE, 1)
				.model(generated("arcana_ignition_on", modLoc("item/rings/arcana_1_on")))
				.end()
				//Harvest Off
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 0)
				.predicate(ClientRegistration.MODE_OVERRIDE, 2)
				.model(generated("arcana_harv_off", modLoc("item/rings/arcana_2")))
				.end()
				//Harvest On
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.predicate(ClientRegistration.MODE_OVERRIDE, 2)
				.model(generated("arcana_harv_on", modLoc("item/rings/arcana_2_on")))
				.end()
				//SWRG Off
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 0)
				.predicate(ClientRegistration.MODE_OVERRIDE, 3)
				.model(generated("arcana_swrg_off", modLoc("item/rings/arcana_3")))
				.end()
				//SWRG On
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.predicate(ClientRegistration.MODE_OVERRIDE, 3)
				.model(generated("arcana_swrg_on", modLoc("item/rings/arcana_3_on")))
				.end();
		generated(PEItems.ARCHANGEL_SMITE, modLoc("item/rings/archangel_smite"));
		generated(PEItems.BLACK_HOLE_BAND, modLoc("item/rings/black_hole_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("black_hole_band_on", modLoc("item/rings/black_hole_on")))
				.end();
		generated(PEItems.BODY_STONE, modLoc("item/rings/body_stone_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("body_stone_on", modLoc("item/rings/body_stone_on")))
				.end();
		generated(PEItems.EVERTIDE_AMULET, modLoc("item/rings/evertide_amulet"));
		generated(PEItems.HARVEST_GODDESS_BAND, modLoc("item/rings/harvest_god_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("harvest_goddess_band_on", modLoc("item/rings/harvest_god_on")))
				.end();
		generated(PEItems.IGNITION_RING, modLoc("item/rings/ignition_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("ignition_on", modLoc("item/rings/ignition_on")))
				.end();
		generated(PEItems.IRON_BAND, modLoc("item/rings/iron_band"));
		generated(PEItems.LIFE_STONE, modLoc("item/rings/life_stone_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("life_stone_on", modLoc("item/rings/life_stone_on")))
				.end();
		generated(PEItems.MIND_STONE, modLoc("item/rings/mind_stone_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("mind_stone_on", modLoc("item/rings/mind_stone_on")))
				.end();
		generated(PEItems.SOUL_STONE, modLoc("item/rings/soul_stone_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("soul_stone_on", modLoc("item/rings/soul_stone_on")))
				.end();
		generated(PEItems.SWIFTWOLF_RENDING_GALE, modLoc("item/rings/swrg_off"))
				//Fly only
				.override()
				.predicate(ClientRegistration.MODE_OVERRIDE, 1)
				.model(generated("swiftwolf_rending_gale_fly", modLoc("item/rings/swrg_on1")))
				.end()
				//Repel only
				.override()
				.predicate(ClientRegistration.MODE_OVERRIDE, 2)
				.model(generated("swiftwolf_rending_gale_repel", modLoc("item/rings/swrg_on3")))
				.end()
				//Fly and Repel
				.override()
				.predicate(ClientRegistration.MODE_OVERRIDE, 3)
				.model(generated("swiftwolf_rending_gale_fly_repel", modLoc("item/rings/swrg_on2")))
				.end();
		generated(PEItems.VOID_RING, modLoc("item/rings/void_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("void_ring_on", modLoc("item/rings/void_on")))
				.end();
		generated(PEItems.VOLCANITE_AMULET, modLoc("item/rings/volcanite_amulet"));
		generated(PEItems.WATCH_OF_FLOWING_TIME, modLoc("item/rings/time_watch_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("watch_of_flowing_time_on", modLoc("item/rings/time_watch_on")))
				.end();
		generated(PEItems.ZERO_RING, modLoc("item/rings/zero_off"))
				.override()
				.predicate(ClientRegistration.ACTIVE_OVERRIDE, 1)
				.model(generated("zero_ring_on", modLoc("item/rings/zero_on")))
				.end();
	}

	private void generateKleinStars() {
		EnumKleinTier[] tiers = EnumKleinTier.values();
		for (int tier = 0; tier < tiers.length; tier++) {
			generated(PEItems.getStar(tiers[tier]), modLoc("item/stars/klein_star_" + (tier + 1)));
		}
	}

	private void generateGear() {
		//Dark Matter
		armorWithTrim(PEItems.DARK_MATTER_HELMET, modLoc("item/dm_armor/head"));
		armorWithTrim(PEItems.DARK_MATTER_CHESTPLATE, modLoc("item/dm_armor/chest"));
		armorWithTrim(PEItems.DARK_MATTER_LEGGINGS, modLoc("item/dm_armor/legs"));
		armorWithTrim(PEItems.DARK_MATTER_BOOTS, modLoc("item/dm_armor/feet"));
		handheld(PEItems.DARK_MATTER_AXE, modLoc("item/dm_tools/axe"));
		handheld(PEItems.DARK_MATTER_HAMMER, modLoc("item/dm_tools/hammer"));
		handheld(PEItems.DARK_MATTER_HOE, modLoc("item/dm_tools/hoe"));
		handheld(PEItems.DARK_MATTER_PICKAXE, modLoc("item/dm_tools/pickaxe"));
		handheld(PEItems.DARK_MATTER_SHEARS, modLoc("item/dm_tools/shears"));
		handheld(PEItems.DARK_MATTER_SHOVEL, modLoc("item/dm_tools/shovel"));
		handheld(PEItems.DARK_MATTER_SWORD, modLoc("item/dm_tools/sword"));
		//Red Matter
		armorWithTrim(PEItems.RED_MATTER_HELMET, modLoc("item/rm_armor/head"));
		armorWithTrim(PEItems.RED_MATTER_CHESTPLATE, modLoc("item/rm_armor/chest"));
		armorWithTrim(PEItems.RED_MATTER_LEGGINGS, modLoc("item/rm_armor/legs"));
		armorWithTrim(PEItems.RED_MATTER_BOOTS, modLoc("item/rm_armor/feet"));
		handheld(PEItems.RED_MATTER_AXE, modLoc("item/rm_tools/axe"));
		handheld(PEItems.RED_MATTER_HAMMER, modLoc("item/rm_tools/hammer"));
		handheld(PEItems.RED_MATTER_HOE, modLoc("item/rm_tools/hoe"));
		handheld(PEItems.RED_MATTER_PICKAXE, modLoc("item/rm_tools/pickaxe"));
		handheld(PEItems.RED_MATTER_SHEARS, modLoc("item/rm_tools/shears"));
		handheld(PEItems.RED_MATTER_SHOVEL, modLoc("item/rm_tools/shovel"));
		handheld(PEItems.RED_MATTER_SWORD, modLoc("item/rm_tools/sword"));
		handheld(PEItems.RED_MATTER_KATAR, modLoc("item/rm_tools/katar"));
		handheld(PEItems.RED_MATTER_MORNING_STAR, modLoc("item/rm_tools/morning_star"));
		//Gem
		armorWithTrim(PEItems.GEM_HELMET, modLoc("item/gem_armor/head"));
		armorWithTrim(PEItems.GEM_CHESTPLATE, modLoc("item/gem_armor/chest"));
		armorWithTrim(PEItems.GEM_LEGGINGS, modLoc("item/gem_armor/legs"));
		armorWithTrim(PEItems.GEM_BOOTS, modLoc("item/gem_armor/feet"));
	}

	private void blockParentModel(BlockRegistryObject<?, ?>... blocks) {
		for (BlockRegistryObject<?, ?> block : blocks) {
			String name = getName(block);
			withExistingParent(name, modLoc("block/" + name));
		}
	}

	protected ResourceLocation itemTexture(ItemLike itemProvider) {
		return modLoc("item/" + getName(itemProvider));
	}

	protected void registerGenerated(ItemLike... itemProviders) {
		for (ItemLike itemProvider : itemProviders) {
			generated(itemProvider);
		}
	}

	protected ItemModelBuilder generated(ItemLike itemProvider) {
		return generated(itemProvider, itemTexture(itemProvider));
	}

	protected ItemModelBuilder generated(ItemLike itemProvider, ResourceLocation texture) {
		return generated(getName(itemProvider), texture);
	}

	protected ItemModelBuilder generated(String name, ResourceLocation texture) {
		return withExistingParent(name, "item/generated").texture("layer0", texture);
	}

	protected ItemModelBuilder handheld(ItemLike itemProvider, ResourceLocation texture) {
		return handheld(getName(itemProvider), texture);
	}

	protected ItemModelBuilder handheld(String name, ResourceLocation texture) {
		return withExistingParent(name, "item/handheld").texture("layer0", texture);
	}

	private static String getName(ItemLike itemProvider) {
		return RegistryUtils.getPath(itemProvider.asItem());
	}

	protected ItemModelBuilder armorWithTrim(ItemLike itemProvider, ResourceLocation texture) {
		ItemModelBuilder builder = generated(itemProvider, texture);
		ArmorItem.Type type = ((ArmorItem) itemProvider.asItem()).getType();
		TRIM_HELPER.forEachTrim((trimId, itemModelIndex) -> {
					ItemModelBuilder override = withExistingParent(builder.getLocation().withSuffix("_" + trimId + "_trim").getPath(), "item/generated")
							.texture("layer0", texture);
					//Directly add the layer1 to the texture map as the file doesn't actually exist
					MODEL_TEXTURES.getValue(override).put("layer1", new ResourceLocation(type.getName() + "_trim_" + trimId).withPrefix("trims/items/").toString());
					builder.override()
							.predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, itemModelIndex)
							.model(override);
				}
		);
		return builder;
	}

	private static class TrimModelDataHelper<TMD_CLASS> {

		private final FieldReflectionHelper<ItemModelGenerators, List<TMD_CLASS>> generatedTrimModels = new FieldReflectionHelper<>(ItemModelGenerators.class, "f_265952_", Collections::emptyList);
		private final FieldReflectionHelper<TMD_CLASS, String> name;
		private final FieldReflectionHelper<TMD_CLASS, Float> itemModelIndex;

		public TrimModelDataHelper() {
			Class<TMD_CLASS> tmdClass;
			try {
				tmdClass = (Class<TMD_CLASS>) Class.forName("net.minecraft.data.models.ItemModelGenerators$TrimModelData");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			name = new FieldReflectionHelper<>(tmdClass, "f_265890_", () -> null);
			itemModelIndex = new FieldReflectionHelper<>(tmdClass, "f_265849_", () -> null);
		}

		public void forEachTrim(BiConsumer<String, Float> consumer) {
			List<TMD_CLASS> trims = generatedTrimModels.getValue(null);
			for (TMD_CLASS trim : trims) {
				String trimName = name.getValue(trim);
				Float modelIndex = itemModelIndex.getValue(trim);
				consumer.accept(trimName, modelIndex);
			}
		}
	}
}
