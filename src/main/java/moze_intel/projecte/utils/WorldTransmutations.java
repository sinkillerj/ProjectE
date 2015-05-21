package moze_intel.projecte.utils;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
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
		// TODO 1.8 States should exist when this class initializes - this message is here to be seen if it crashes
		register(Blocks.stone.getDefaultState(), Blocks.cobblestone.getDefaultState(), Blocks.grass.getDefaultState());
		register(Blocks.cobblestone.getDefaultState(), Blocks.stone.getDefaultState(), Blocks.grass.getDefaultState());
		register(Blocks.grass.getDefaultState(), Blocks.sand.getDefaultState(), Blocks.cobblestone.getDefaultState());
		register(Blocks.dirt.getDefaultState(), Blocks.sand.getDefaultState(), Blocks.cobblestone.getDefaultState());
		register(Blocks.sand.getDefaultState(), Blocks.grass.getDefaultState(), Blocks.cobblestone.getDefaultState());
		register(Blocks.gravel.getDefaultState(), Blocks.sandstone.getDefaultState(), null);
		register(Blocks.water.getDefaultState(), Blocks.ice.getDefaultState(), null);
		register(Blocks.lava.getDefaultState(), Blocks.obsidian.getDefaultState(), null);
		register(Blocks.melon_block.getDefaultState(), Blocks.pumpkin.getDefaultState(), null);

		register(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK),
				Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE),
				Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK)
		);

		register(Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK),
				Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE),
				Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
		);

		for (int i = 1; i < 3; i++) // TODO 1.8 Find better way :( (besides typing them all out)
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

		register(Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK),
				Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE),
				Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK)
		);

		for (int i = 1; i < 5; i++) // TODO 1.8 Find better way :( (besides typing them all out)
		{
			register(Blocks.sapling.getStateFromMeta(i),
					Blocks.sapling.getStateFromMeta(i + 1),
					Blocks.sapling.getStateFromMeta(i - 1)
			);
		}

		register(Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK),
				Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK),
				Blocks.sapling.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA)
		);

		register(Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE),
				Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE),
				Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK)
		);

		for (int i = 1; i < 15; i++) // TODO 1.8 Find better way :( (besides typing them all out)
		{
			register(Blocks.wool.getStateFromMeta(i),
					Blocks.wool.getStateFromMeta(i + 1),
					Blocks.wool.getStateFromMeta(i - 1)
			);
		}

		register(Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK),
				Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE),
				Blocks.wool.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED)
		);
	}

	public static IBlockState getWorldTransmutation(World world, BlockPos pos, boolean isSneaking)
	{
		return getWorldTransmutation(world.getBlockState(pos), isSneaking);
	}

	public static IBlockState getWorldTransmutation(IBlockState current, boolean isSneaking)
	{
		if (MAP.containsKey(current))
		{
			return isSneaking ? MAP.get(current).getLeft() : MAP.get(current).getRight();
		}

		return null;
	}

	public static void register(IBlockState from, IBlockState result, IBlockState altResult)
	{
		MAP.put(from, ImmutablePair.of(result, altResult));
	}
}
