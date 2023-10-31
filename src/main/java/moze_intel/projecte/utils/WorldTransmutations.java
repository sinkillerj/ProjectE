package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fml.InterModComms;
import org.jetbrains.annotations.Nullable;

public final class WorldTransmutations {

	private static List<WorldTransmutationEntry> DEFAULT_ENTRIES = Collections.emptyList();
	private static List<WorldTransmutationEntry> ENTRIES = Collections.emptyList();

	public static void init() {
		registerDefault(Blocks.STONE, Blocks.COBBLESTONE, Blocks.GRASS_BLOCK);
		registerDefault(Blocks.COBBLESTONE, Blocks.STONE, Blocks.GRASS_BLOCK);
		registerDefault(Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.DIRT, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.SAND, Blocks.GRASS_BLOCK, Blocks.COBBLESTONE);
		registerBackAndForth(Blocks.GRAVEL, Blocks.SANDSTONE);
		registerBackAndForth(Blocks.WATER, Blocks.ICE);
		registerBackAndForth(Blocks.LAVA, Blocks.OBSIDIAN);
		registerBackAndForth(Blocks.MELON, Blocks.PUMPKIN);
		registerDefault(Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE);
		registerDefault(Blocks.DIORITE, Blocks.ANDESITE, Blocks.GRANITE);
		registerDefault(Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE);

		registerConsecutivePairsAllStates(Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG,
				Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG);
		registerConsecutivePairsAllStates(Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_JUNGLE_LOG,
				Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_CHERRY_LOG);
		registerConsecutivePairsAllStates(Blocks.OAK_WOOD, Blocks.BIRCH_WOOD, Blocks.SPRUCE_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD,
				Blocks.MANGROVE_WOOD, Blocks.CHERRY_WOOD);
		registerConsecutivePairsAllStates(Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD,
				Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.STRIPPED_CHERRY_WOOD);
		registerConsecutivePairsAllStates(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES,
				Blocks.MANGROVE_LEAVES, Blocks.CHERRY_LEAVES);
		registerConsecutivePairs(Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING,
				Blocks.MANGROVE_PROPAGULE, Blocks.CHERRY_SAPLING);
		registerConsecutivePairs(Blocks.OAK_PLANKS, Blocks.BIRCH_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS,
				Blocks.MANGROVE_PLANKS, Blocks.CHERRY_PLANKS, Blocks.BAMBOO_PLANKS);
		registerConsecutivePairsAllStates(Blocks.OAK_SLAB, Blocks.BIRCH_SLAB, Blocks.SPRUCE_SLAB, Blocks.JUNGLE_SLAB, Blocks.ACACIA_SLAB, Blocks.DARK_OAK_SLAB,
				Blocks.MANGROVE_SLAB, Blocks.CHERRY_SLAB, Blocks.BAMBOO_SLAB);
		registerConsecutivePairsAllStates(Blocks.OAK_STAIRS, Blocks.BIRCH_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.DARK_OAK_STAIRS,
				Blocks.MANGROVE_STAIRS, Blocks.CHERRY_STAIRS, Blocks.BAMBOO_STAIRS);
		registerConsecutivePairsAllStates(Blocks.OAK_FENCE, Blocks.BIRCH_FENCE, Blocks.SPRUCE_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE,
				Blocks.MANGROVE_FENCE, Blocks.CHERRY_FENCE, Blocks.BAMBOO_FENCE);
		registerConsecutivePairs(Blocks.OAK_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE,
				Blocks.ACACIA_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.MANGROVE_PRESSURE_PLATE, Blocks.CHERRY_PRESSURE_PLATE, Blocks.BAMBOO_PRESSURE_PLATE);
		registerConsecutivePairs(Blocks.WHITE_CONCRETE, Blocks.ORANGE_CONCRETE, Blocks.MAGENTA_CONCRETE, Blocks.LIGHT_BLUE_CONCRETE, Blocks.YELLOW_CONCRETE,
				Blocks.LIME_CONCRETE, Blocks.PINK_CONCRETE, Blocks.GRAY_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE, Blocks.CYAN_CONCRETE, Blocks.PURPLE_CONCRETE,
				Blocks.BLUE_CONCRETE, Blocks.BROWN_CONCRETE, Blocks.GREEN_CONCRETE, Blocks.RED_CONCRETE, Blocks.BLACK_CONCRETE);
		registerConsecutivePairs(Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER,
				Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER,
				Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER,
				Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
		registerConsecutivePairs(Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET,
				Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET,
				Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET);
		registerConsecutivePairs(Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL,
				Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL,
				Blocks.RED_WOOL, Blocks.BLACK_WOOL);
		registerConsecutivePairs(Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
				Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA,
				Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA);
		registerConsecutivePairs(Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS,
				Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS,
				Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS,
				Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS);
		registerConsecutivePairsAllStates(Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE,
				Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE,
				Blocks.GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE,
				Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE,
				Blocks.BLACK_STAINED_GLASS_PANE);
		registerBackAndForth(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
		registerDefault(Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM);
		registerDefault(Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHERRACK);
		registerDefault(Blocks.WARPED_NYLIUM, Blocks.CRIMSON_NYLIUM, Blocks.NETHERRACK);
		registerBackAndForthAllStates(Blocks.CRIMSON_STEM, Blocks.WARPED_STEM);
		registerBackAndForthAllStates(Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_WARPED_STEM);
		registerBackAndForth(Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE);
		registerBackAndForth(Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE);
		registerBackAndForth(Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK);
		registerBackAndForth(Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS);
		registerBackAndForth(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS);
		registerBackAndForth(Blocks.CRIMSON_PLANKS, Blocks.WARPED_PLANKS);
		registerBackAndForthAllStates(Blocks.CRIMSON_SLAB, Blocks.WARPED_SLAB);
		registerBackAndForthAllStates(Blocks.CRIMSON_STAIRS, Blocks.WARPED_STAIRS);
		registerBackAndForthAllStates(Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE);
		registerBackAndForthAllStates(Blocks.CRIMSON_PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE);
	}

	@Nullable
	public static BlockState getWorldTransmutation(BlockState current, boolean isSneaking) {
		for (WorldTransmutationEntry e : ENTRIES) {
			if (e.origin() == current) {
				return isSneaking ? e.altResult() : e.result();
			}
		}
		return null;
	}

	public static List<WorldTransmutationEntry> getWorldTransmutations() {
		return ENTRIES;
	}

	public static void setWorldTransmutation(List<WorldTransmutationEntry> entries) {
		DEFAULT_ENTRIES = ImmutableList.copyOf(entries);
		resetWorldTransmutations();
	}

	public static void resetWorldTransmutations() {
		//Make it so that ENTRIES are mutable, so we can modify it with CraftTweaker
		ENTRIES = new ArrayList<>(DEFAULT_ENTRIES);
	}

	public static void register(BlockState from, BlockState result, @Nullable BlockState altResult) {
		ENTRIES.add(new WorldTransmutationEntry(from, result, altResult));
	}

	private static void registerIMC(BlockState from, BlockState result, @Nullable BlockState altResult) {
		InterModComms.sendTo(PECore.MODID, IMCMethods.REGISTER_WORLD_TRANSMUTATION, () -> new WorldTransmutationEntry(from, result, altResult));
	}

	private static void registerDefault(Block from, Block result, @Nullable Block altResult) {
		registerIMC(from.defaultBlockState(), result.defaultBlockState(), null);
	}

	private static void registerAllStates(Block from, Block result, @Nullable Block altResult) {
		StateDefinition<Block, BlockState> stateContainer = from.getStateDefinition();
		ImmutableList<BlockState> validStates = stateContainer.getPossibleStates();
		for (BlockState validState : validStates) {
			try {
				BlockState resultState = copyProperties(validState, result.defaultBlockState());
				BlockState altResultState = altResult == null ? null : copyProperties(validState, altResult.defaultBlockState());
				registerIMC(validState, resultState, altResultState);
			} catch (IllegalArgumentException e) {
				//Something went wrong skip adding a conversion for this but log that we failed
				// This should never happen unless some mod is doing really weird things to the
				// BlockStates like try to add more BlockStates for a block (this will fail in
				// a lot of other ways, but just in case don't hard crash the game let them do
				// so instead). The other case this may fail is if something changed between
				// MC versions, and we need to fix some conversion that no longer necessarily
				// makes sense
				PECore.LOGGER.error("Something went wrong registering conversions for {}", RegistryUtils.getName(from), e);
			}
		}
	}

	private static BlockState copyProperties(BlockState source, BlockState target) {
		ImmutableMap<Property<?>, Comparable<?>> values = source.getValues();
		for (Entry<Property<?>, Comparable<?>> entry : values.entrySet()) {
			target = applyProperty(target, entry.getKey(), entry.getValue());
		}
		return target;
	}

	private static <T extends Comparable<T>, V extends T> BlockState applyProperty(BlockState target, Property<T> property, Comparable<?> value) {
		return target.setValue(property, (V) value);
	}

	private static void registerBackAndForth(Block first, Block second) {
		registerDefault(first, second, null);
		registerDefault(second, first, null);
	}

	private static void registerBackAndForthAllStates(Block first, Block second) {
		registerAllStates(first, second, null);
		registerAllStates(second, first, null);
	}

	private static void registerConsecutivePairs(RegisterBlock registerMethod, Block... blocks) {
		for (int i = 0; i < blocks.length; i++) {
			Block prev = i == 0 ? blocks[blocks.length - 1] : blocks[i - 1];
			Block cur = blocks[i];
			Block next = i == blocks.length - 1 ? blocks[0] : blocks[i + 1];
			registerMethod.register(cur, next, prev);
		}
	}

	private static void registerConsecutivePairs(Block... blocks) {
		registerConsecutivePairs(WorldTransmutations::registerDefault, blocks);
	}

	private static void registerConsecutivePairsAllStates(Block... blocks) {
		registerConsecutivePairs(WorldTransmutations::registerAllStates, blocks);
	}

	@FunctionalInterface
	private interface RegisterBlock {

		void register(Block from, Block result, @Nullable Block altResult);
	}
}
