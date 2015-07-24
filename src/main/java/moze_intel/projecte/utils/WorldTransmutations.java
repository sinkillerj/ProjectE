package moze_intel.projecte.utils;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public final class WorldTransmutations
{
	private static final HashMap<IBlockState, Pair<IBlockState, IBlockState>> MAP = Maps.newHashMap();

	static
	{
		registerDefault(Blocks.stone, Blocks.cobblestone, Blocks.grass);
		registerDefault(Blocks.cobblestone, Blocks.stone, Blocks.grass);
		registerDefault(Blocks.grass, Blocks.sand, Blocks.cobblestone);
		registerDefault(Blocks.dirt, Blocks.sand, Blocks.cobblestone);
		registerDefault(Blocks.sand, Blocks.grass, Blocks.cobblestone);
		registerDefault(Blocks.gravel, Blocks.sandstone, null);
		registerDefault(Blocks.sandstone, Blocks.gravel, null);
		registerDefault(Blocks.water, Blocks.ice, null);
		registerDefault(Blocks.ice, Blocks.water, null);
		registerDefault(Blocks.lava, Blocks.obsidian, null);
		registerDefault(Blocks.obsidian, Blocks.lava, null);
		registerDefault(Blocks.melon_block, Blocks.pumpkin, null);
		registerDefault(Blocks.pumpkin, Blocks.melon_block, null);

		register(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK),
				Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE),
				Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK)
		);

		register(Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK),
				Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE),
				Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
		);

		for (int i = 1; i < 3; i++)
		{
			register(Blocks.log.getStateFromMeta(i),
					Blocks.log.getStateFromMeta(i + 1),
					Blocks.log.getStateFromMeta(i - 1)
			);

			register(Blocks.leaves.getStateFromMeta(i),
					Blocks.leaves.getStateFromMeta(i + 1),
					Blocks.leaves.getStateFromMeta(i - 1)
			);
		}

		register(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE),
				Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA),
				Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH)
		);

		register(Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE),
				Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA),
				Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
		);

		register(Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA),
				Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK),
				Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE)
		);

		register(Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA),
				Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK),
				Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
		);

		register(Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK),
				Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK),
				Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA)
		);

		register(Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK),
				Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK),
				Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
		);

		for (BlockPlanks.EnumType e : BlockPlanks.EnumType.values())
		{
			IBlockState state = Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, e);
			register(state, state.cycleProperty(BlockSapling.TYPE), cyclePropertyBackwards(state, BlockSapling.TYPE));
		}

		for (EnumDyeColor e : EnumDyeColor.values())
		{
			IBlockState state = Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, e);
			register(state, state.cycleProperty(BlockColored.COLOR), cyclePropertyBackwards(state, BlockColored.COLOR));
		}
	}

	private static IBlockState cyclePropertyBackwards(IBlockState state, IProperty property)
	{
		IBlockState result = state;
		for (int i = 0; i < property.getAllowedValues().size() - 1; i++)
		{
			result = result.cycleProperty(property);
		}
		return result;
	}

	public static IBlockState getWorldTransmutation(World world, BlockPos pos, boolean isSneaking)
	{
		return getWorldTransmutation(world.getBlockState(pos), isSneaking);
	}

	public static IBlockState getWorldTransmutation(IBlockState current, boolean isSneaking)
	{
		if (MAP.containsKey(current))
		{
			Pair<IBlockState, IBlockState> result = MAP.get(current);
			return isSneaking ? (result.getRight() == null ? result.getLeft() : result.getRight()) : result.getLeft();
		}

		return null;
	}

	public static void register(IBlockState from, IBlockState result, IBlockState altResult)
	{
		MAP.put(from, ImmutablePair.of(result, altResult));
	}

	public static void registerDefault(Block from, Block result, Block altResult)
	{
		MAP.put(from.getDefaultState(), ImmutablePair.of(result.getDefaultState(), altResult == null ? null : altResult.getDefaultState()));
	}
}
