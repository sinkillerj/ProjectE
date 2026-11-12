package moze_intel.projecte.client;

import com.google.gson.JsonObject;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.KleinStar.KleinTier;
import moze_intel.projecte.gameObjs.registration.INamedEntry;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

public class PEModelProvider extends ModelProvider {

	private static final TextureSlot CHEST = TextureSlot.create("chest");
	private static final TextureSlot PEDESTAL = TextureSlot.create("pedestal");
	private static final TextureSlot BOTTOM = TextureSlot.create("bottom");
	private static final TextureSlot TOP = TextureSlot.create("top");
	private static final TextureSlot SIDE = TextureSlot.create("side");

	public PEModelProvider(PackOutput output) {
		super(output, PECore.MODID);
	}

	private Material texture(String path) {
		return new Material(modLocation(path));
	}

	private Material texture(Identifier path) {
		return new Material(path);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		registerBlockModels(blockModels);
		registerItemModels(itemModels);
	}

	private void registerBlockModels(BlockModelGenerators blockModels) {
		simpleBlocks(blockModels, PEBlocks.ALCHEMICAL_COAL, PEBlocks.MOBIUS_FUEL, PEBlocks.AETERNALIS_FUEL, PEBlocks.DARK_MATTER, PEBlocks.RED_MATTER);
		registerTieredOrientable(blockModels, "collectors", PEBlocks.COLLECTOR, PEBlocks.COLLECTOR_MK2, PEBlocks.COLLECTOR_MK3);
		registerTieredOrientable(blockModels, "relays", PEBlocks.RELAY, PEBlocks.RELAY_MK2, PEBlocks.RELAY_MK3);
		registerFurnace(blockModels, PEBlocks.DARK_MATTER_FURNACE, "dm", "dark_matter_block");
		registerFurnace(blockModels, PEBlocks.RED_MATTER_FURNACE, "rm", "red_matter_block");
		registerChests(blockModels);
		registerExplosives(blockModels);
		registerInterdictionTorch(blockModels);
		registerPedestal(blockModels);
		registerTransmutationTable(blockModels);
	}

	private void registerItemModels(ItemModelGenerators itemModels) {
		blockParentModel(itemModels, PEBlocks.ALCHEMICAL_COAL, PEBlocks.MOBIUS_FUEL, PEBlocks.AETERNALIS_FUEL, PEBlocks.DARK_MATTER, PEBlocks.RED_MATTER,
				PEBlocks.DARK_MATTER_FURNACE, PEBlocks.RED_MATTER_FURNACE, PEBlocks.COLLECTOR, PEBlocks.COLLECTOR_MK2,
				PEBlocks.COLLECTOR_MK3, PEBlocks.NOVA_CATALYST, PEBlocks.NOVA_CATACLYSM, PEBlocks.RELAY, PEBlocks.RELAY_MK2,
				PEBlocks.RELAY_MK3);
		generated(itemModels, PEBlocks.DARK_MATTER_PEDESTAL.getBlock().asItem(), modLocation("item/dm_pedestal"));
		generated(itemModels, PEBlocks.TRANSMUTATION_TABLE.getBlock().asItem(), modLocation("item/transmutation_table"));
		registerGenerated(itemModels, PEItems.CATALYTIC_LENS, PEItems.DESTRUCTION_CATALYST, PEItems.LOW_DIVINING_ROD, PEItems.MEDIUM_DIVINING_ROD, PEItems.HIGH_DIVINING_ROD,
				PEItems.HYPERKINETIC_LENS, PEItems.MERCURIAL_EYE, PEItems.PHILOSOPHERS_STONE, PEItems.REPAIR_TALISMAN, PEItems.TOME_OF_KNOWLEDGE,
				PEItems.TRANSMUTATION_TABLET);
		generated(itemModels, PEItems.ALCHEMICAL_COAL, modLocation("item/fuels/alchemical_coal"));
		generated(itemModels, PEItems.MOBIUS_FUEL, modLocation("item/fuels/mobius"));
		generated(itemModels, PEItems.AETERNALIS_FUEL, modLocation("item/fuels/aeternalis"));
		generated(itemModels, PEItems.DARK_MATTER, modLocation("item/matter/dark"));
		generated(itemModels, PEItems.RED_MATTER, modLocation("item/matter/red"));
		generated(itemModels, PEItems.LOW_COVALENCE_DUST, modLocation("item/covalence_dust/low"));
		generated(itemModels, PEItems.MEDIUM_COVALENCE_DUST, modLocation("item/covalence_dust/medium"));
		generated(itemModels, PEItems.HIGH_COVALENCE_DUST, modLocation("item/covalence_dust/high"));
		generated(itemModels, PEBlocks.INTERDICTION_TORCH.getBlock().asItem(), modLocation("block/interdiction_torch"));
		generateAlchemicalBags(itemModels);
		generateChests(itemModels);
		generateRings(itemModels);
		generateKleinStars(itemModels);
		generateGear(itemModels);
		activeItem(itemModels, PEItems.GEM_OF_ETERNAL_DENSITY, "item/dense_gem_off", "item/dense_gem_on");
		generatedManual(itemModels);
	}

	private void simpleBlocks(BlockModelGenerators blockModels, BlockRegistryObject<?, ?>... blocks) {
		for (BlockRegistryObject<?, ?> block : blocks) {
			blockModels.createTrivialCube(block.getBlock());
		}
	}

	private void registerChests(BlockModelGenerators blockModels) {
		ExtendedModelTemplate baseChest = chestModelBuilder().build();
		baseChest.create(modLocation("block/base_chest"), new TextureMapping().put(CHEST, texture("block/alchemical_chest")), blockModels.modelOutput);
		registerChestBlock(blockModels, PEBlocks.ALCHEMICAL_CHEST);
		registerChestBlock(blockModels, PEBlocks.CONDENSER);
		registerChestBlock(blockModels, PEBlocks.CONDENSER_MK2);
	}

	private void registerChestBlock(BlockModelGenerators blockModels, BlockRegistryObject<?, ?> block) {
		String name = block.getName();
		Identifier model = modLocation("block/" + name);
		blockModels.modelOutput.accept(model, () -> {
			JsonObject json = new JsonObject();
			json.addProperty("parent", modLocation("block/base_chest").toString());
			JsonObject textures = new JsonObject();
			textures.addProperty("chest", modLocation("block/" + name).toString());
			json.add("textures", textures);
			return json;
		});
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.getBlock(), BlockModelGenerators.plainVariant(model))
				.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
	}

	private ExtendedModelTemplateBuilder chestModelBuilder() {
		ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
				.parent(Identifier.withDefaultNamespace("block/block"))
				.requiredTextureSlot(CHEST)
				.element(element -> element
						.from(1, 0, 1).to(15, 10, 15)
						.face(Direction.NORTH, face -> face.texture(CHEST).uvs(10.5F, 10.65F, 14, 8.25F))
						.face(Direction.EAST, face -> face.texture(CHEST).uvs(7, 10.65F, 10.5F, 8.25F))
						.face(Direction.SOUTH, face -> face.texture(CHEST).uvs(3.5F, 10.65F, 7, 8.25F))
						.face(Direction.WEST, face -> face.texture(CHEST).uvs(0, 10.7F, 3.5F, 8.3F))
						.face(Direction.UP, face -> face.texture(CHEST).uvs(7, 8.2F, 10.5F, 4.8F))
						.face(Direction.DOWN, face -> face.texture(CHEST).uvs(3.5F, 8.3F, 7, 4.7F)))
				.element(element -> element
						.from(1, 10, 1).to(15, 15, 15)
						.face(Direction.NORTH, face -> face.texture(CHEST).uvs(10.5F, 4.65F, 14, 3.5F))
						.face(Direction.EAST, face -> face.texture(CHEST).uvs(7, 4.7F, 10.5F, 3.5F))
						.face(Direction.SOUTH, face -> face.texture(CHEST).uvs(3.5F, 4.7F, 7, 3.5F))
						.face(Direction.WEST, face -> face.texture(CHEST).uvs(0, 4.7F, 3.5F, 3.5F))
						.face(Direction.UP, face -> face.texture(CHEST).uvs(7, 3.5F, 10.5F, 0))
						.face(Direction.DOWN, face -> face.texture(CHEST).uvs(3.5F, 3.5F, 7, 0)))
				.element(element -> element
						.from(7, 8, 0).to(9, 12, 1)
						.face(Direction.NORTH, face -> face.texture(CHEST).uvs(0.75F, 1.25F, 0.25F, 0.25F))
						.face(Direction.EAST, face -> face.texture(CHEST).uvs(1.25F, 1.25F, 1.5F, 0.25F))
						.face(Direction.SOUTH, face -> face.texture(CHEST).uvs(0.75F, 1.25F, 1.25F, 0.25F))
						.face(Direction.WEST, face -> face.texture(CHEST).uvs(0, 1.25F, 0.25F, 0.25F))
						.face(Direction.UP, face -> face.texture(CHEST).uvs(0.25F, 0.25F, 0.75F, 0))
						.face(Direction.DOWN, face -> face.texture(CHEST).uvs(0.75F, 0.25F, 1.25F, 0)));
		chestItemDisplay(builder, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, 0.375F, 2.5F);
		chestItemDisplay(builder, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, 0.375F, 2.5F);
		chestItemDisplay(builder, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, 0.4F, 0F);
		chestItemDisplay(builder, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, 0.4F, 0F);
		builder.transform(ItemDisplayContext.GROUND, transform -> transform
				.translation(0, 3, 0)
				.scale(0.25F));
		builder.transform(ItemDisplayContext.GUI, transform -> transform
				.rotation(30, 225, 0)
				.scale(0.625F));
		builder.transform(ItemDisplayContext.FIXED, transform -> transform
				.scale(0.5F));
		return builder;
	}

	private static void chestItemDisplay(ExtendedModelTemplateBuilder builder, ItemDisplayContext context, float scale, float yTranslation) {
		builder.transform(context, transform -> transform
				.rotation(90, 45, 0)
				.translation(0, yTranslation, 0)
				.scale(scale));
	}

	private void particleOnly(BlockModelGenerators blockModels, BlockRegistryObject<?, ?> block) {
		String name = block.getName();
		Identifier model = ModelTemplates.CUBE_ALL.create(modLocation("block/" + name),
				new TextureMapping().put(TextureSlot.ALL, texture("block/" + name)), blockModels.modelOutput);
		blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block.getBlock(), BlockModelGenerators.plainVariant(model)));
	}

	private void registerPedestal(BlockModelGenerators blockModels) {
		Material dm = texture("block/dark_matter_block");
		ExtendedModelTemplate model = ExtendedModelTemplateBuilder.builder()
				.parent(Identifier.withDefaultNamespace("block/block"))
				.requiredTextureSlot(PEDESTAL)
				.requiredTextureSlot(TextureSlot.PARTICLE)
				.element(element -> element
						.from(3, 0, 3).to(13, 2, 13)
						.face(Direction.NORTH, face -> face.texture(PEDESTAL).uvs(3, 0, 10, 2))
						.face(Direction.EAST, face -> face.texture(PEDESTAL).uvs(3, 0, 10, 2))
						.face(Direction.SOUTH, face -> face.texture(PEDESTAL).uvs(3, 0, 10, 2))
						.face(Direction.WEST, face -> face.texture(PEDESTAL).uvs(3, 0, 10, 2))
						.face(Direction.UP, face -> face.texture(PEDESTAL).uvs(3, 3, 10, 10))
						.face(Direction.DOWN, face -> face.texture(PEDESTAL).uvs(3, 3, 10, 10).cullface(Direction.DOWN)))
				.element(element -> element
						.from(6, 2, 6).to(10, 9, 10)
						.face(Direction.NORTH, face -> face.texture(PEDESTAL).uvs(6, 4, 4, 7))
						.face(Direction.EAST, face -> face.texture(PEDESTAL).uvs(6, 4, 4, 7))
						.face(Direction.SOUTH, face -> face.texture(PEDESTAL).uvs(6, 4, 4, 7))
						.face(Direction.WEST, face -> face.texture(PEDESTAL).uvs(6, 4, 4, 7)))
				.element(element -> element
						.from(5, 9, 5).to(11, 10, 11)
						.face(Direction.NORTH, face -> face.texture(PEDESTAL).uvs(0, 0, 6, 1))
						.face(Direction.EAST, face -> face.texture(PEDESTAL).uvs(0, 0, 6, 1))
						.face(Direction.SOUTH, face -> face.texture(PEDESTAL).uvs(0, 0, 6, 1))
						.face(Direction.WEST, face -> face.texture(PEDESTAL).uvs(0, 0, 6, 1))
						.face(Direction.UP, face -> face.texture(PEDESTAL).uvs(6, 6, 6, 6))
						.face(Direction.DOWN, face -> face.texture(PEDESTAL).uvs(6, 6, 6, 6)))
				.build();
		Identifier modelLoc = model.create(PEBlocks.DARK_MATTER_PEDESTAL.getBlock(),
				new TextureMapping().put(PEDESTAL, dm).put(TextureSlot.PARTICLE, dm), blockModels.modelOutput);
		blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(PEBlocks.DARK_MATTER_PEDESTAL.getBlock(), BlockModelGenerators.plainVariant(modelLoc)));
	}

	private void registerTransmutationTable(BlockModelGenerators blockModels) {
		Material top = texture("block/transmutation_stone/top");
		ExtendedModelTemplate model = ExtendedModelTemplateBuilder.builder()
				.parent(Identifier.withDefaultNamespace("block/block"))
				.requiredTextureSlot(BOTTOM)
				.requiredTextureSlot(TOP)
				.requiredTextureSlot(SIDE)
				.element(element -> element
						.from(0, 0, 0).to(16, 4, 16)
						.face(Direction.DOWN, face -> face.texture(BOTTOM).cullface(Direction.DOWN))
						.face(Direction.UP, face -> face.texture(TOP))
						.face(Direction.NORTH, face -> face.texture(SIDE).cullface(Direction.NORTH))
						.face(Direction.SOUTH, face -> face.texture(SIDE).cullface(Direction.SOUTH))
						.face(Direction.WEST, face -> face.texture(SIDE).cullface(Direction.WEST))
						.face(Direction.EAST, face -> face.texture(SIDE).cullface(Direction.EAST)))
				.build();
		TextureMapping textures = new TextureMapping()
				.put(BOTTOM, texture("block/transmutation_stone/bottom"))
				.put(TOP, top)
				.put(SIDE, texture("block/transmutation_stone/side"))
				.put(TextureSlot.PARTICLE, top);
		Identifier modelLoc = model.create(PEBlocks.TRANSMUTATION_TABLE.getBlock(), textures, blockModels.modelOutput);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(PEBlocks.TRANSMUTATION_TABLE.getBlock(), BlockModelGenerators.plainVariant(modelLoc))
				.with(BlockModelGenerators.ROTATION_FACING));
	}

	private void registerExplosives(BlockModelGenerators blockModels) {
		TexturedModel.Provider catalystProvider = TexturedModel.createDefault(block -> new TextureMapping()
				.put(TextureSlot.SIDE, texture("block/explosives/nova_side"))
				.put(TextureSlot.BOTTOM, texture("block/explosives/bottom"))
				.put(TextureSlot.TOP, texture("block/explosives/top")), ModelTemplates.CUBE_BOTTOM_TOP);
		blockModels.createTrivialBlock(PEBlocks.NOVA_CATALYST.getBlock(), catalystProvider);
		TexturedModel.Provider cataclysmProvider = TexturedModel.createDefault(block -> new TextureMapping()
				.put(TextureSlot.SIDE, texture("block/explosives/nova1_side"))
				.put(TextureSlot.BOTTOM, texture("block/explosives/bottom"))
				.put(TextureSlot.TOP, texture("block/explosives/top")), ModelTemplates.CUBE_BOTTOM_TOP);
		blockModels.createTrivialBlock(PEBlocks.NOVA_CATACLYSM.getBlock(), cataclysmProvider);
	}

	private void registerInterdictionTorch(BlockModelGenerators blockModels) {
		TextureMapping textures = TextureMapping.torch(texture("block/interdiction_torch"));
		Identifier groundModel = ModelTemplates.TORCH.create(PEBlocks.INTERDICTION_TORCH.getBlock(), textures, blockModels.modelOutput);
		blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(PEBlocks.INTERDICTION_TORCH.getBlock(), BlockModelGenerators.plainVariant(groundModel)));
		Identifier wallModel = ModelTemplates.WALL_TORCH.create(PEBlocks.INTERDICTION_TORCH.getWallBlock(), textures, blockModels.modelOutput);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(PEBlocks.INTERDICTION_TORCH.getWallBlock(), BlockModelGenerators.plainVariant(wallModel))
				.with(BlockModelGenerators.ROTATION_TORCH));
	}

	private void registerFurnace(BlockModelGenerators blockModels, BlockRegistryObject<?, ?> furnace, String prefix, String sideTexture) {
		TexturedModel.Provider provider = TexturedModel.createDefault(block -> new TextureMapping()
				.put(TextureSlot.SIDE, texture("block/" + sideTexture))
				.put(TextureSlot.FRONT, texture("block/matter_furnace/" + prefix + "_off"))
				.put(TextureSlot.TOP, texture("block/" + sideTexture)), ModelTemplates.CUBE_ORIENTABLE);
		blockModels.createFurnace(furnace.getBlock(), provider);
	}

	private void registerTieredOrientable(BlockModelGenerators blockModels, String type, BlockRegistryObject<?, ?> base, BlockRegistryObject<?, ?> mk2, BlockRegistryObject<?, ?> mk3) {
		Material side = texture("block/" + type + "/other");
		TexturedModel.Provider baseProvider = TexturedModel.createDefault(block -> new TextureMapping()
				.put(TextureSlot.SIDE, side)
				.put(TextureSlot.FRONT, texture("block/" + type + "/front"))
				.put(TextureSlot.TOP, texture("block/" + type + "/top_1"))
				.put(TextureSlot.BOTTOM, side), ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
		blockModels.createHorizontallyRotatedBlock(base.getBlock(), baseProvider);
		TexturedModel.Provider mk2Provider = TexturedModel.createDefault(block -> new TextureMapping()
				.put(TextureSlot.SIDE, side)
				.put(TextureSlot.FRONT, texture("block/" + type + "/front"))
				.put(TextureSlot.TOP, texture("block/" + type + "/top_2"))
				.put(TextureSlot.BOTTOM, side), ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
		blockModels.createHorizontallyRotatedBlock(mk2.getBlock(), mk2Provider);
		TexturedModel.Provider mk3Provider = TexturedModel.createDefault(block -> new TextureMapping()
				.put(TextureSlot.SIDE, side)
				.put(TextureSlot.FRONT, texture("block/" + type + "/front"))
				.put(TextureSlot.TOP, texture("block/" + type + "/top_3"))
				.put(TextureSlot.BOTTOM, side), ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
		blockModels.createHorizontallyRotatedBlock(mk3.getBlock(), mk3Provider);
	}

	private void blockParentModel(ItemModelGenerators itemModels, BlockRegistryObject<?, ?>... blocks) {
		for (BlockRegistryObject<?, ?> block : blocks) {
			itemModels.itemModelOutput.accept(block.getBlock().asItem(), ItemModelUtils.plainModel(modLocation("block/" + block.getName())));
		}
	}

	private void generateAlchemicalBags(ItemModelGenerators itemModels) {
		for (DyeColor color : Constants.COLORS) {
			generated(itemModels, PEItems.getBagReference(color), modLocation("item/alchemy_bags/" + color));
		}
	}

	private void generateChests(ItemModelGenerators itemModels) {
		generateChest(itemModels, PEBlocks.ALCHEMICAL_CHEST);
		generateChest(itemModels, PEBlocks.CONDENSER);
		generateChest(itemModels, PEBlocks.CONDENSER_MK2);
	}

	private void generateChest(ItemModelGenerators itemModels, BlockRegistryObject<?, ?> block) {
		String name = block.getName();
		Identifier modelId = ModelLocationUtils.getModelLocation(block.getBlock().asItem());
		itemModels.modelOutput.accept(modelId, () -> {
			JsonObject json = new JsonObject();
			json.addProperty("parent", modLocation("block/base_chest").toString());
			JsonObject textures = new JsonObject();
			textures.addProperty("chest", modLocation("block/" + name).toString());
			json.add("textures", textures);
			return json;
		});
		itemModels.itemModelOutput.accept(block.getBlock().asItem(), ItemModelUtils.plainModel(modelId));
	}

	private void generateRings(ItemModelGenerators itemModels) {
		var arcanaCases = new java.util.ArrayList<net.minecraft.client.renderer.item.SelectItemModel.SwitchCase<moze_intel.projecte.gameObjs.items.rings.Arcana.ArcanaMode>>();
        for (var mode : moze_intel.projecte.gameObjs.items.rings.Arcana.ArcanaMode.values()) {
            arcanaCases.add(ItemModelUtils.when(mode, activeModel(itemModels, PEItems.ARCANA_RING,
                    "arcana_" + mode.ordinal(), "item/rings/arcana_" + mode.ordinal(), "item/rings/arcana_" + mode.ordinal() + "_on")));
        }
        itemModels.itemModelOutput.accept(PEItems.ARCANA_RING.get(), ItemModelUtils.select(
                new net.minecraft.client.renderer.item.properties.select.ComponentContents<>(moze_intel.projecte.gameObjs.registries.PEDataComponentTypes.ARCANA_MODE.get()),
                arcanaCases.getFirst().model(), arcanaCases));
		generated(itemModels, PEItems.ARCHANGEL_SMITE, modLocation("item/rings/archangel_smite"));
		activeItem(itemModels, PEItems.BLACK_HOLE_BAND, "item/rings/black_hole_off", "item/rings/black_hole_on");
		activeItem(itemModels, PEItems.BODY_STONE, "item/rings/body_stone_off", "item/rings/body_stone_on");
		generated(itemModels, PEItems.EVERTIDE_AMULET, modLocation("item/rings/evertide_amulet"));
		activeItem(itemModels, PEItems.HARVEST_GODDESS_BAND, "item/rings/harvest_god_off", "item/rings/harvest_god_on");
		activeItem(itemModels, PEItems.IGNITION_RING, "item/rings/ignition_off", "item/rings/ignition_on");
		generated(itemModels, PEItems.IRON_BAND, modLocation("item/rings/iron_band"));
		activeItem(itemModels, PEItems.LIFE_STONE, "item/rings/life_stone_off", "item/rings/life_stone_on");
		activeItem(itemModels, PEItems.MIND_STONE, "item/rings/mind_stone_off", "item/rings/mind_stone_on");
		activeItem(itemModels, PEItems.SOUL_STONE, "item/rings/soul_stone_off", "item/rings/soul_stone_on");
		var swrgCases = new java.util.ArrayList<net.minecraft.client.renderer.item.SelectItemModel.SwitchCase<moze_intel.projecte.gameObjs.items.rings.SWRG.SWRGMode>>();
        for (var mode : moze_intel.projecte.gameObjs.items.rings.SWRG.SWRGMode.values()) {
            String texture = mode.ordinal() == 0 ? "item/rings/swrg_off" : "item/rings/swrg_on" + mode.ordinal();
            swrgCases.add(ItemModelUtils.when(mode, variant(itemModels, PEItems.SWIFTWOLF_RENDING_GALE,
                    "mode_" + mode.ordinal(), texture)));
        }
        itemModels.itemModelOutput.accept(PEItems.SWIFTWOLF_RENDING_GALE.get(), ItemModelUtils.select(
                new net.minecraft.client.renderer.item.properties.select.ComponentContents<>(moze_intel.projecte.gameObjs.registries.PEDataComponentTypes.SWRG_MODE.get()),
                swrgCases.getFirst().model(), swrgCases));
		activeItem(itemModels, PEItems.VOID_RING, "item/rings/void_off", "item/rings/void_on");
		generated(itemModels, PEItems.VOLCANITE_AMULET, modLocation("item/rings/volcanite_amulet"));
		activeItem(itemModels, PEItems.WATCH_OF_FLOWING_TIME, "item/rings/time_watch_off", "item/rings/time_watch_on");
		activeItem(itemModels, PEItems.ZERO_RING, "item/rings/zero_off", "item/rings/zero_on");
	}

    private net.minecraft.client.renderer.item.ItemModel.Unbaked variant(ItemModelGenerators models, ItemLike item, String suffix, String texture) {
        Identifier id = ModelLocationUtils.getModelLocation(item.asItem()).withSuffix("_" + suffix);
        ModelTemplates.FLAT_ITEM.create(id, TextureMapping.layer0(texture(texture)), models.modelOutput);
        return ItemModelUtils.plainModel(id);
    }

    private net.minecraft.client.renderer.item.ItemModel.Unbaked activeModel(ItemModelGenerators models, ItemLike item,
            String suffix, String offTexture, String onTexture) {
        var off = variant(models, item, suffix + "_off", offTexture);
        var on = variant(models, item, suffix + "_on", onTexture);
        return ItemModelUtils.select(new net.minecraft.client.renderer.item.properties.select.ComponentContents<>(
                moze_intel.projecte.gameObjs.registries.PEDataComponentTypes.ACTIVE.get()), off, ItemModelUtils.when(true, on));
    }

    private void activeItem(ItemModelGenerators models, ItemLike item, String offTexture, String onTexture) {
        models.itemModelOutput.accept(item.asItem(), activeModel(models, item, "active", offTexture, onTexture));
    }

	private void generateKleinStars(ItemModelGenerators itemModels) {
		for (KleinTier tier : KleinTier.values()) {
			generated(itemModels, PEItems.getStar(tier), modLocation("item/stars/klein_star_" + (tier.ordinal() + 1)));
		}
	}

	private void generateGear(ItemModelGenerators itemModels) {
		armorWithTrim(itemModels, PEItems.DARK_MATTER_HELMET, modLocation("item/dm_armor/head"), ItemModelGenerators.TRIM_PREFIX_HELMET);
		armorWithTrim(itemModels, PEItems.DARK_MATTER_CHESTPLATE, modLocation("item/dm_armor/chest"), ItemModelGenerators.TRIM_PREFIX_CHESTPLATE);
		armorWithTrim(itemModels, PEItems.DARK_MATTER_LEGGINGS, modLocation("item/dm_armor/legs"), ItemModelGenerators.TRIM_PREFIX_LEGGINGS);
		armorWithTrim(itemModels, PEItems.DARK_MATTER_BOOTS, modLocation("item/dm_armor/feet"), ItemModelGenerators.TRIM_PREFIX_BOOTS);
		handheld(itemModels, PEItems.DARK_MATTER_AXE, modLocation("item/dm_tools/axe"));
		handheld(itemModels, PEItems.DARK_MATTER_HAMMER, modLocation("item/dm_tools/hammer"));
		handheld(itemModels, PEItems.DARK_MATTER_HOE, modLocation("item/dm_tools/hoe"));
		handheld(itemModels, PEItems.DARK_MATTER_PICKAXE, modLocation("item/dm_tools/pickaxe"));
		handheld(itemModels, PEItems.DARK_MATTER_SHEARS, modLocation("item/dm_tools/shears"));
		handheld(itemModels, PEItems.DARK_MATTER_SHOVEL, modLocation("item/dm_tools/shovel"));
		handheld(itemModels, PEItems.DARK_MATTER_SWORD, modLocation("item/dm_tools/sword"));
		armorWithTrim(itemModels, PEItems.RED_MATTER_HELMET, modLocation("item/rm_armor/head"), ItemModelGenerators.TRIM_PREFIX_HELMET);
		armorWithTrim(itemModels, PEItems.RED_MATTER_CHESTPLATE, modLocation("item/rm_armor/chest"), ItemModelGenerators.TRIM_PREFIX_CHESTPLATE);
		armorWithTrim(itemModels, PEItems.RED_MATTER_LEGGINGS, modLocation("item/rm_armor/legs"), ItemModelGenerators.TRIM_PREFIX_LEGGINGS);
		armorWithTrim(itemModels, PEItems.RED_MATTER_BOOTS, modLocation("item/rm_armor/feet"), ItemModelGenerators.TRIM_PREFIX_BOOTS);
		handheld(itemModels, PEItems.RED_MATTER_AXE, modLocation("item/rm_tools/axe"));
		handheld(itemModels, PEItems.RED_MATTER_HAMMER, modLocation("item/rm_tools/hammer"));
		handheld(itemModels, PEItems.RED_MATTER_HOE, modLocation("item/rm_tools/hoe"));
		handheld(itemModels, PEItems.RED_MATTER_PICKAXE, modLocation("item/rm_tools/pickaxe"));
		handheld(itemModels, PEItems.RED_MATTER_SHEARS, modLocation("item/rm_tools/shears"));
		handheld(itemModels, PEItems.RED_MATTER_SHOVEL, modLocation("item/rm_tools/shovel"));
		handheld(itemModels, PEItems.RED_MATTER_SWORD, modLocation("item/rm_tools/sword"));
		handheld(itemModels, PEItems.RED_MATTER_KATAR, modLocation("item/rm_tools/katar"));
		handheld(itemModels, PEItems.RED_MATTER_MORNING_STAR, modLocation("item/rm_tools/morning_star"));
		armorWithTrim(itemModels, PEItems.GEM_HELMET, modLocation("item/gem_armor/head"), ItemModelGenerators.TRIM_PREFIX_HELMET);
		armorWithTrim(itemModels, PEItems.GEM_CHESTPLATE, modLocation("item/gem_armor/chest"), ItemModelGenerators.TRIM_PREFIX_CHESTPLATE);
		armorWithTrim(itemModels, PEItems.GEM_LEGGINGS, modLocation("item/gem_armor/legs"), ItemModelGenerators.TRIM_PREFIX_LEGGINGS);
		armorWithTrim(itemModels, PEItems.GEM_BOOTS, modLocation("item/gem_armor/feet"), ItemModelGenerators.TRIM_PREFIX_BOOTS);
	}

	private void registerGenerated(ItemModelGenerators itemModels, INamedEntry... itemProviders) {
		for (INamedEntry itemProvider : itemProviders) {
			generated(itemModels, itemProvider);
		}
	}

	private void generated(ItemModelGenerators itemModels, INamedEntry itemProvider) {
		generated(itemModels, (ItemLike) itemProvider, modLocation("item/" + itemProvider.getName()));
	}

	private void generated(ItemModelGenerators itemModels, ItemLike itemProvider, Identifier texture) {
		Identifier model = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(itemProvider.asItem()),
				TextureMapping.layer0(texture(texture)), itemModels.modelOutput);
		itemModels.itemModelOutput.accept(itemProvider.asItem(), ItemModelUtils.plainModel(model));
	}

	private void generatedManual(ItemModelGenerators itemModels) {
		Identifier model = ModelTemplates.FLAT_ITEM.create(modLocation("item/manual"), TextureMapping.layer0(texture("item/book")), itemModels.modelOutput);
		itemModels.itemModelOutput.register(modLocation("manual"), new ClientItem(ItemModelUtils.plainModel(model), ClientItem.Properties.DEFAULT));
	}

	private void handheld(ItemModelGenerators itemModels, ItemLike itemProvider, Identifier texture) {
		Identifier model = ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(itemProvider.asItem()),
				TextureMapping.layer0(texture(texture)), itemModels.modelOutput);
		itemModels.itemModelOutput.accept(itemProvider.asItem(), ItemModelUtils.plainModel(model));
	}

	private void armorWithTrim(ItemModelGenerators itemModels, ItemLike itemProvider, Identifier texture, Identifier trimPrefix) {
		Item item = itemProvider.asItem();
		Identifier model = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
				TextureMapping.layer0(texture(texture)), itemModels.modelOutput);
        var cases = new java.util.ArrayList<net.minecraft.client.renderer.item.SelectItemModel.SwitchCase<
                net.minecraft.resources.ResourceKey<net.minecraft.world.item.equipment.trim.TrimMaterial>>>();
        for (var material : ItemModelGenerators.TRIM_MATERIAL_MODELS) {
            Identifier trimModel = model.withSuffix("_" + material.palette().suffix() + "_trim");
            Material overlay = new Material(trimPrefix.withSuffix("_" + material.palette().suffix()));
            ModelTemplates.TWO_LAYERED_ITEM.create(trimModel, TextureMapping.layered(texture(texture), overlay), itemModels.modelOutput);
            cases.add(ItemModelUtils.when(material.materialKey(), ItemModelUtils.plainModel(trimModel)));
        }
        itemModels.itemModelOutput.accept(item, ItemModelUtils.select(
                new net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty(), ItemModelUtils.plainModel(model), cases));
	}
}
