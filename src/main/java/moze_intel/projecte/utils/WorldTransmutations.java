package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableList;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import net.minecraft.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.InterModComms;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: 1.14 decide if World Transmutations should support tags
public final class WorldTransmutations
{
	private static List<WorldTransmutationEntry> DEFAULT_ENTRIES = Collections.emptyList();
	private static List<WorldTransmutationEntry> ENTRIES = Collections.emptyList();

	public static void init()
	{
		registerDefault(Blocks.STONE, Blocks.COBBLESTONE, Blocks.GRASS_BLOCK);
		registerDefault(Blocks.COBBLESTONE, Blocks.STONE, Blocks.GRASS_BLOCK);
		registerDefault(Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.DIRT, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.SAND, Blocks.GRASS_BLOCK, Blocks.COBBLESTONE);
		registerDefault(Blocks.GRAVEL, Blocks.SANDSTONE, null);
		registerDefault(Blocks.SANDSTONE, Blocks.GRAVEL, null);
		registerDefault(Blocks.WATER, Blocks.ICE, null);
		registerDefault(Blocks.ICE, Blocks.WATER, null);
		registerDefault(Blocks.LAVA, Blocks.OBSIDIAN, null);
		registerDefault(Blocks.OBSIDIAN, Blocks.LAVA, null);
		registerDefault(Blocks.MELON, Blocks.PUMPKIN, null);
		registerDefault(Blocks.PUMPKIN, Blocks.MELON, null);
		registerDefault(Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE);
		registerDefault(Blocks.DIORITE, Blocks.ANDESITE, Blocks.GRANITE);
		registerDefault(Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE);

		Block[] logs = { Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG };
		registerConsecutivePairs(logs);

		Block[] leaves = { Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES };
		registerConsecutivePairs(leaves);

		Block[] saplings = { Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING };
		registerConsecutivePairs(saplings);

		Block[] wools = {
				Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL,
				Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL,
				Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL
		};
		registerConsecutivePairs(wools);

		Block[] terracottas = {
				Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
				Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA,
				Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA
		};
		registerConsecutivePairs(terracottas);

		Block[] carpets = {
				Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET,
				Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET,
				Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET
		};
		registerConsecutivePairs(carpets);
	}

	public static BlockState getWorldTransmutation(IBlockReader world, BlockPos pos, boolean isSneaking)
	{
		return getWorldTransmutation(world.getBlockState(pos), isSneaking);
	}

	public static BlockState getWorldTransmutation(BlockState current, boolean isSneaking)
	{
		for (WorldTransmutationEntry e : ENTRIES)
		{
			if (e.getOrigin() == current)
			{
				return isSneaking ? e.getAltResult() : e.getResult();
			}
		}

		return null;
	}

	public static List<WorldTransmutationEntry> getWorldTransmutations()
	{
		return ENTRIES;
	}

	public static void setWorldTransmutation(List<WorldTransmutationEntry> entries)
	{
		DEFAULT_ENTRIES = ImmutableList.copyOf(entries);
		resetWorldTransmutations();
	}

	public static void resetWorldTransmutations()
	{
		//Make it so that ENTRIES are mutable so we can modify it with CraftTweaker
		ENTRIES = new ArrayList<>(DEFAULT_ENTRIES);
	}

	public static void register(BlockState from, BlockState result, @Nullable BlockState altResult)
	{
		ENTRIES.add(new WorldTransmutationEntry(from, result, altResult));
	}

	private static void registerDefault(Block from, Block result, Block altResult)
	{
		InterModComms.sendTo(PECore.MODID, IMCMethods.REGISTER_WORLD_TRANSMUTATION, () -> new WorldTransmutationEntry(from.getDefaultState(), result.getDefaultState(), altResult == null ? null : altResult.getDefaultState()));
	}

	private static void registerConsecutivePairs(Block[] blocks)
	{
		for (int i = 0; i < blocks.length; i++)
		{
			Block prev = i == 0 ? blocks[blocks.length - 1] : blocks[i - 1];
			Block cur = blocks[i];
			Block next = i == blocks.length - 1 ? blocks[0] : blocks[i + 1];
			registerDefault(cur, next, prev);
		}
	}
}
