package moze_intel.projecte.utils;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.HashMap;

public final class WorldTransmutations
{
	public static final HashMap<MetaBlock, MetaBlock[]> MAP = Maps.newHashMap();

	static
	{
		register(Blocks.stone, Blocks.cobblestone, Blocks.grass);
		register(Blocks.cobblestone, Blocks.stone, Blocks.grass);
		register(Blocks.grass, Blocks.sand, Blocks.cobblestone);
		register(Blocks.dirt, Blocks.sand, Blocks.cobblestone);
		register(Blocks.sand, Blocks.grass, Blocks.cobblestone);
		register(Blocks.gravel, Blocks.sandstone);
		register(Blocks.water, Blocks.ice);
		register(Blocks.lava, Blocks.obsidian);
		register(Blocks.melon_block, Blocks.pumpkin);


		register(new MetaBlock(Blocks.log, 0), new MetaBlock[] {new MetaBlock(Blocks.log, 1), new MetaBlock(Blocks.log2, 1)});
		register(new MetaBlock(Blocks.leaves, 0), new MetaBlock[] {new MetaBlock(Blocks.leaves, 1), new MetaBlock(Blocks.leaves, 1)});

		for (int i = 1; i < 3; i++)
		{
			register(new MetaBlock(Blocks.log, i), new MetaBlock[]{new MetaBlock(Blocks.log, i + 1), new MetaBlock(Blocks.log, i - 1)});
			register(new MetaBlock(Blocks.leaves, i), new MetaBlock[] {new MetaBlock(Blocks.leaves, i + 1), new MetaBlock(Blocks.leaves, i - 1)});
		}

		register(new MetaBlock(Blocks.log, 3), new MetaBlock[]{new MetaBlock(Blocks.log2, 0), new MetaBlock(Blocks.log, 2)});
		register(new MetaBlock(Blocks.leaves, 3), new MetaBlock[]{new MetaBlock(Blocks.leaves2, 0), new MetaBlock(Blocks.leaves, 2)});
		register(new MetaBlock(Blocks.log2, 0), new MetaBlock[] {new MetaBlock(Blocks.log2, 1), new MetaBlock(Blocks.log, 3)});
		register(new MetaBlock(Blocks.leaves2, 0), new MetaBlock[] {new MetaBlock(Blocks.leaves2, 1), new MetaBlock(Blocks.leaves, 3)});
		register(new MetaBlock(Blocks.log2, 1), new MetaBlock[] {new MetaBlock(Blocks.log, 0), new MetaBlock(Blocks.log2, 0)});
		register(new MetaBlock(Blocks.leaves2, 1), new MetaBlock[] {new MetaBlock(Blocks.leaves, 0), new MetaBlock(Blocks.leaves2, 0)});

		register(new MetaBlock(Blocks.sapling, 0), new MetaBlock[] {new MetaBlock(Blocks.sapling, 1), new MetaBlock(Blocks.sapling, 5)});

		for (int i = 1; i < 5; i++)
		{
			register(new MetaBlock(Blocks.sapling, i), new MetaBlock[] {new MetaBlock(Blocks.sapling, i + 1), new MetaBlock(Blocks.sapling, i - 1)});
		}

		register(new MetaBlock(Blocks.sapling, 5), new MetaBlock[] {new MetaBlock(Blocks.sapling, 0), new MetaBlock(Blocks.sapling, 4)});

		register(new MetaBlock(Blocks.wool, 0), new MetaBlock[] {new MetaBlock(Blocks.wool, 1), new MetaBlock(Blocks.wool, 15)});

		for (int i = 1; i < 15; i++)
		{
			register(new MetaBlock(Blocks.wool, i), new MetaBlock[] {new MetaBlock(Blocks.wool, i + 1), new MetaBlock(Blocks.wool, i - 1)});
		}

		register(new MetaBlock(Blocks.wool, 15), new MetaBlock[] {new MetaBlock(Blocks.wool, 0), new MetaBlock(Blocks.wool, 14)});
	}

	public static MetaBlock getWorldTransmutation(World world, int x, int y, int z, boolean isSneaking)
	{
		MetaBlock block = new MetaBlock(world, x, y, z);

		if (MAP.containsKey(block))
		{
			return MAP.get(block)[isSneaking ? 1 : 0];
		}

		return null;
	}

	public static MetaBlock getWorldTransmutation(MetaBlock block, boolean isSneaking)
	{
		if (MAP.containsKey(block))
		{
			return MAP.get(block)[isSneaking ? 1 : 0];
		}

		return null;
	}

	private static void register(Block block, Block result)
	{
		MAP.put(new MetaBlock(block), new MetaBlock[] {new MetaBlock(result), new MetaBlock(result)});
		MAP.put(new MetaBlock(result), new MetaBlock[] {new MetaBlock(block), new MetaBlock(block)});
	}

	private static void register(Block block, Block b1, Block b2)
	{
		MAP.put(new MetaBlock(block), new MetaBlock[] {new MetaBlock(b1), new MetaBlock(b2)});
	}

	private static void register(MetaBlock block, MetaBlock[] result)
	{
		MAP.put(block, result);
	}

	public static void register(MetaBlock origin, MetaBlock result1, MetaBlock result2)
	{
		// Stopgap method for API impl. This class is much improved in 1.8 in terms of cleanliness
		if (result2 != null)
		{
			MAP.put(origin, new MetaBlock[]{result1, result2});
		}
		else
		{
			MAP.put(origin, new MetaBlock[]{result1});
		}
	}
}
