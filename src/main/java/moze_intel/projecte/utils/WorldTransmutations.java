package moze_intel.projecte.utils;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.Map;

public final class WorldTransmutations
{
	private static final Map<IBlockState, Pair<IBlockState, IBlockState>> MAP = Maps.newHashMap();

	static
	{
		registerDefault(Blocks.STONE, Blocks.COBBLESTONE, Blocks.GRASS);
		registerDefault(Blocks.COBBLESTONE, Blocks.STONE, Blocks.GRASS);
		registerDefault(Blocks.GRASS, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.DIRT, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.SAND, Blocks.GRASS, Blocks.COBBLESTONE);
		registerDefault(Blocks.GRAVEL, Blocks.SANDSTONE, null);
		registerDefault(Blocks.SANDSTONE, Blocks.GRAVEL, null);
		registerDefault(Blocks.WATER, Blocks.ICE, null);
		registerDefault(Blocks.ICE, Blocks.WATER, null);
		registerDefault(Blocks.LAVA, Blocks.OBSIDIAN, null);
		registerDefault(Blocks.OBSIDIAN, Blocks.LAVA, null);
		register(Blocks.MELON_BLOCK.getDefaultState(), Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, EnumFacing.SOUTH), null);

		for (EnumFacing e : EnumFacing.HORIZONTALS)
		{
			register(Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, e), Blocks.MELON_BLOCK.getDefaultState(), null);
		}

		for (IBlockState s : Blocks.LOG.getBlockState().getValidStates())
		{
			if (s.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.OAK)
			{
				// Oak must loop backward to dark oak
				register(s, s.cycleProperty(BlockOldLog.VARIANT), 
						Blocks.LOG2.getDefaultState()
								.withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK)
								.withProperty(BlockNewLog.LOG_AXIS, s.getValue(BlockOldLog.LOG_AXIS)));
			} else if (s.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE)
			{
				// Jungle must loop forward to acacia
				register(s, 
						Blocks.LOG2.getDefaultState()
								.withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA)
								.withProperty(BlockNewLog.LOG_AXIS, s.getValue(BlockOldLog.LOG_AXIS)),
						cyclePropertyBackwards(s, BlockOldLog.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockOldLog.VARIANT), cyclePropertyBackwards(s, BlockOldLog.VARIANT));
			}
		}

		for (IBlockState s : Blocks.LEAVES.getBlockState().getValidStates())
		{
			if (s.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.OAK)
			{
				// Oak must loop backward to dark oak
				register(s, s.cycleProperty(BlockOldLeaf.VARIANT),
						Blocks.LEAVES2.getDefaultState()
								.withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
								.withProperty(BlockNewLeaf.CHECK_DECAY, s.getValue(BlockOldLeaf.CHECK_DECAY))
								.withProperty(BlockNewLeaf.DECAYABLE, s.getValue(BlockOldLeaf.DECAYABLE)));
			} else if (s.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.JUNGLE)
			{
				// Jungle must loop forward to acacia
				register(s,
						Blocks.LEAVES2.getDefaultState()
								.withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
								.withProperty(BlockNewLeaf.CHECK_DECAY, s.getValue(BlockNewLeaf.CHECK_DECAY))
								.withProperty(BlockNewLeaf.DECAYABLE, s.getValue(BlockOldLeaf.DECAYABLE)),
						cyclePropertyBackwards(s, BlockOldLeaf.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockOldLeaf.VARIANT), cyclePropertyBackwards(s, BlockOldLeaf.VARIANT));
			}
		}

		for (IBlockState s : Blocks.LOG2.getBlockState().getValidStates())
		{
			if (s.getValue(BlockNewLog.VARIANT) == BlockPlanks.EnumType.ACACIA)
			{
				// Acacia must loop backward to jungle
				register(s, s.cycleProperty(BlockNewLog.VARIANT),
						Blocks.LOG.getDefaultState()
								.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE)
								.withProperty(BlockOldLog.LOG_AXIS, s.getValue(BlockNewLog.LOG_AXIS)));
			} else if (s.getValue(BlockNewLog.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
			{
				// Dark oak must loop forward to oak
				register(s,
						Blocks.LOG.getDefaultState()
								.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK)
								.withProperty(BlockOldLog.LOG_AXIS, s.getValue(BlockNewLog.LOG_AXIS)),
						cyclePropertyBackwards(s, BlockNewLog.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockNewLog.VARIANT), cyclePropertyBackwards(s, BlockNewLog.VARIANT));
			}
		}

		for (IBlockState s : Blocks.LEAVES2.getBlockState().getValidStates())
		{
			if (s.getValue(BlockNewLeaf.VARIANT) == BlockPlanks.EnumType.ACACIA)
			{
				// Acacia must loop backward to jungle
				register(s, s.cycleProperty(BlockNewLeaf.VARIANT),
						Blocks.LEAVES.getDefaultState()
								.withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
								.withProperty(BlockOldLeaf.CHECK_DECAY, s.getValue(BlockNewLeaf.CHECK_DECAY))
								.withProperty(BlockOldLeaf.DECAYABLE, s.getValue(BlockNewLeaf.DECAYABLE)));
			} else if (s.getValue(BlockNewLeaf.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
			{
				// Dark oak must loop forward to oak
				register(s,
						Blocks.LEAVES.getDefaultState()
								.withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
								.withProperty(BlockOldLeaf.CHECK_DECAY, s.getValue(BlockNewLeaf.CHECK_DECAY))
								.withProperty(BlockOldLeaf.DECAYABLE, s.getValue(BlockNewLeaf.DECAYABLE)),
						cyclePropertyBackwards(s, BlockNewLeaf.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockNewLeaf.VARIANT), cyclePropertyBackwards(s, BlockNewLeaf.VARIANT));
			}
		}

		for (BlockPlanks.EnumType e : BlockPlanks.EnumType.values())
		{
			IBlockState state = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, e);
			register(state, state.cycleProperty(BlockSapling.TYPE), cyclePropertyBackwards(state, BlockSapling.TYPE));
		}

		for (EnumDyeColor e : EnumDyeColor.values())
		{
			IBlockState state = Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, e);
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

	public static Map<IBlockState, Pair<IBlockState, IBlockState>> getWorldTransmutations()
	{
		return Collections.unmodifiableMap(MAP);
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
