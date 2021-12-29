package moze_intel.projecte.client;

import java.util.function.Function;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PEBlockStateProvider extends BlockStateProvider {

	public PEBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		//TODO: Should we use simpleBlockItem here instead of blockParentModel in the item model provider
		simpleBlocks(PEBlocks.ALCHEMICAL_COAL, PEBlocks.MOBIUS_FUEL, PEBlocks.AETERNALIS_FUEL, PEBlocks.DARK_MATTER, PEBlocks.RED_MATTER);
		registerTieredOrientable("collectors", PEBlocks.COLLECTOR, PEBlocks.COLLECTOR_MK2, PEBlocks.COLLECTOR_MK3);
		registerTieredOrientable("relays", PEBlocks.RELAY, PEBlocks.RELAY_MK2, PEBlocks.RELAY_MK3);
		registerFurnace(PEBlocks.DARK_MATTER_FURNACE, "dm", "dark_matter_block");
		registerFurnace(PEBlocks.RED_MATTER_FURNACE, "rm", "red_matter_block");
		registerChests();
		registerExplosives();
		registerInterdictionTorch();
		registerPedestal();
		registerTransmutationTable();
	}

	private void registerChests() {
		models().withExistingParent("base_chest", "block/block")
				//Body
				.element()
				.from(1, 0, 1)
				.to(15, 10, 15)
				.face(Direction.NORTH).uvs(10.5F, 10.65F, 14, 8.25F).texture("#chest").end()
				.face(Direction.EAST).uvs(7, 10.65F, 10.5F, 8.25F).texture("#chest").end()
				.face(Direction.SOUTH).uvs(3.5F, 10.65F, 7, 8.25F).texture("#chest").end()
				.face(Direction.WEST).uvs(0, 10.7F, 3.5F, 8.3F).texture("#chest").end()
				.face(Direction.UP).uvs(7, 8.2F, 10.5F, 4.8F).texture("#chest").end()
				.face(Direction.DOWN).uvs(3.5F, 8.3F, 7, 4.7F).texture("#chest").end()
				.end()
				//Lid
				.element()
				.from(1, 10, 1)
				.to(15, 15, 15)
				.face(Direction.NORTH).uvs(10.5F, 4.65F, 14, 3.5F).texture("#chest").end()
				.face(Direction.EAST).uvs(7, 4.7F, 10.5F, 3.5F).texture("#chest").end()
				.face(Direction.SOUTH).uvs(3.5F, 4.7F, 7, 3.5F).texture("#chest").end()
				.face(Direction.WEST).uvs(0, 4.7F, 3.5F, 3.5F).texture("#chest").end()
				.face(Direction.UP).uvs(7, 3.5F, 10.5F, 0).texture("#chest").end()
				.face(Direction.DOWN).uvs(3.5F, 3.5F, 7, 0).texture("#chest").end()
				.end()
				//Top
				.element()
				.from(7, 8, 0)
				.to(9, 12, 1)
				.face(Direction.NORTH).uvs(0.75F, 1.25F, 0.25F, 0.25F).texture("#chest").end()
				.face(Direction.EAST).uvs(1.25F, 1.25F, 1.5F, 0.25F).texture("#chest").end()
				.face(Direction.SOUTH).uvs(0.75F, 1.25F, 1.25F, 0.25F).texture("#chest").end()
				.face(Direction.WEST).uvs(0, 1.25F, 0.25F, 0.25F).texture("#chest").end()
				.face(Direction.UP).uvs(0.25F, 0.25F, 0.75F, 0).texture("#chest").end()
				.face(Direction.DOWN).uvs(0.75F, 0.25F, 1.25F, 0).texture("#chest").end()
				.end();
		particleOnly(PEBlocks.ALCHEMICAL_CHEST);
		particleOnly(PEBlocks.CONDENSER);
		particleOnly(PEBlocks.CONDENSER_MK2);
	}

	private void particleOnly(BlockRegistryObject<?, ?> block) {
		String name = getName(block);
		simpleBlock(block.getBlock(), models().getBuilder(name).texture("particle", modLoc("block/" + name)));
	}

	private void registerPedestal() {
		ResourceLocation dm = modLoc("block/dark_matter_block");
		BlockModelBuilder model = models()
				.withExistingParent(getName(PEBlocks.DARK_MATTER_PEDESTAL), "block/block")
				.texture("pedestal", dm)
				.texture("particle", dm)
				//Base
				.element()
				.from(3, 0, 3)
				.to(13, 2, 13)
				.face(Direction.NORTH).uvs(3, 0, 10, 2).texture("#pedestal").end()
				.face(Direction.EAST).uvs(3, 0, 10, 2).texture("#pedestal").end()
				.face(Direction.SOUTH).uvs(3, 0, 10, 2).texture("#pedestal").end()
				.face(Direction.WEST).uvs(3, 0, 10, 2).texture("#pedestal").end()
				.face(Direction.UP).uvs(3, 3, 10, 10).texture("#pedestal").end()
				.face(Direction.DOWN).uvs(3, 3, 10, 10).texture("#pedestal").cullface(Direction.DOWN).end()
				.end()
				//Post
				.element()
				.from(6, 2, 6)
				.to(10, 9, 10)
				.face(Direction.NORTH).uvs(6, 4, 4, 7).texture("#pedestal").end()
				.face(Direction.EAST).uvs(6, 4, 4, 7).texture("#pedestal").end()
				.face(Direction.SOUTH).uvs(6, 4, 4, 7).texture("#pedestal").end()
				.face(Direction.WEST).uvs(6, 4, 4, 7).texture("#pedestal").end()
				.end()
				//Top
				.element()
				.from(5, 9, 5)
				.to(11, 10, 11)
				.face(Direction.NORTH).uvs(0, 0, 6, 1).texture("#pedestal").end()
				.face(Direction.EAST).uvs(0, 0, 6, 1).texture("#pedestal").end()
				.face(Direction.SOUTH).uvs(0, 0, 6, 1).texture("#pedestal").end()
				.face(Direction.WEST).uvs(0, 0, 6, 1).texture("#pedestal").end()
				.face(Direction.UP).uvs(6, 6, 6, 6).texture("#pedestal").end()
				.face(Direction.DOWN).uvs(6, 6, 6, 6).texture("#pedestal").end()
				.end();
		simpleBlock(PEBlocks.DARK_MATTER_PEDESTAL.getBlock(), model);
	}

	private void registerTransmutationTable() {
		ResourceLocation top = modLoc("block/transmutation_stone/top");
		BlockModelBuilder model = models()
				.withExistingParent(getName(PEBlocks.TRANSMUTATION_TABLE), "block/block")
				.texture("bottom", modLoc("block/transmutation_stone/bottom"))
				.texture("top", top)
				.texture("side", modLoc("block/transmutation_stone/side"))
				.texture("particle", top)
				.element()
				.from(0, 0, 0)
				.to(16, 4, 16)
				.face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
				.face(Direction.UP).texture("#top").end()
				.face(Direction.NORTH).texture("#side").cullface(Direction.NORTH).end()
				.face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).texture("#side").cullface(Direction.WEST).end()
				.face(Direction.EAST).texture("#side").cullface(Direction.EAST).end()
				.end();
		directionalBlock(PEBlocks.TRANSMUTATION_TABLE.getBlock(), state -> model, 180, BlockStateProperties.WATERLOGGED);
	}

	private void registerExplosives() {
		BlockModelBuilder catalyst = models().cubeBottomTop(getName(PEBlocks.NOVA_CATALYST), modLoc("block/explosives/nova_side"),
				modLoc("block/explosives/bottom"), modLoc("block/explosives/top"));
		simpleBlock(PEBlocks.NOVA_CATALYST.getBlock(), catalyst);
		simpleBlock(PEBlocks.NOVA_CATACLYSM.getBlock(), models().getBuilder(getName(PEBlocks.NOVA_CATACLYSM))
				.parent(catalyst)
				.texture("side", modLoc("block/explosives/nova1_side")));
	}

	private void registerInterdictionTorch() {
		simpleBlock(PEBlocks.INTERDICTION_TORCH.getBlock(), models().torch(getName(PEBlocks.INTERDICTION_TORCH), modLoc("block/interdiction_torch")));
		horizontalBlock(PEBlocks.INTERDICTION_TORCH.getWallBlock(), models().torchWall(PEBlocks.INTERDICTION_TORCH.getWallBlock().getRegistryName().getPath(),
				modLoc("block/interdiction_torch")), 90);
	}

	private void registerFurnace(BlockRegistryObject<?, ?> furnace, String prefix, String sideTexture) {
		String name = getName(furnace);
		ResourceLocation side = modLoc("block/" + sideTexture);
		BlockModelBuilder offModel = models().orientable(name, side, modLoc("block/matter_furnace/" + prefix + "_off"), side);
		BlockModelBuilder onModel = models().getBuilder(name + "_on")
				.parent(offModel)
				.texture("front", modLoc("block/matter_furnace/" + prefix + "_on"));
		horizontalBlock(furnace.getBlock(), state -> state.getValue(AbstractFurnaceBlock.LIT) ? onModel : offModel);
	}

	private void registerTieredOrientable(String type, BlockRegistryObject<?, ?> base, BlockRegistryObject<?, ?> mk2, BlockRegistryObject<?, ?> mk3) {
		ResourceLocation side = modLoc("block/" + type + "/other");
		BlockModelBuilder model = models().orientableWithBottom(getName(base), side, modLoc("block/" + type + "/front"), side,
				modLoc("block/" + type + "/top_1"));
		horizontalBlock(base.getBlock(), model);
		horizontalBlock(mk2.getBlock(), models().getBuilder(getName(mk2))
				.parent(model)
				.texture("top", modLoc("block/" + type + "/top_2")));
		horizontalBlock(mk3.getBlock(), models().getBuilder(getName(mk3))
				.parent(model)
				.texture("top", modLoc("block/" + type + "/top_3")));
	}

	private void simpleBlocks(BlockRegistryObject<?, ?>... blocks) {
		for (BlockRegistryObject<?, ?> block : blocks) {
			simpleBlock(block.getBlock());
		}
	}

	private void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset, Property<?>... toSkip) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			return ConfiguredModel.builder()
					.modelFile(modelFunc.apply(state))
					.rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
					.rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
					.build();
		}, toSkip);
	}

	private static String getName(ItemLike itemProvider) {
		return itemProvider.asItem().getRegistryName().getPath();
	}
}