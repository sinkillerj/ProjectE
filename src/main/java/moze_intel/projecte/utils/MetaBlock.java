package moze_intel.projecte.utils;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MetaBlock
{
	private Block block;
	private int meta;

	public MetaBlock(World world, int x, int y, int z)
	{
		this.block = world.getBlock(x, y, z);
		this.meta = world.getBlockMetadata(x, y, z);
	}

	public MetaBlock(Block block)
	{
		this.block = block;
	}

	public MetaBlock(Block block, int meta)
	{
		this.block = block;
		this.meta = meta;
	}

	public MetaBlock(ItemStack stack)
	{
		this.block = Block.getBlockFromItem(stack.getItem());
		this.meta = stack.getItemDamage();
	}

	public ItemStack toItemStack()
	{
		return new ItemStack(block, 1, meta);
	}

	public void setInWorld(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, block);
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
	}

	public Block getBlock()
	{
		return block;
	}

	public void setBlock(Block block)
	{
		this.block = block;
	}

	public int getMeta()
	{
		return meta;
	}

	public void setMeta(int meta)
	{
		this.meta = meta;
	}

	@Override
	public String toString()
	{
		return block.toString();
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MetaBlock)
		{
			MetaBlock block = (MetaBlock) obj;

			if (this.block == block.getBlock())
			{
				return this.meta == block.meta || this.block.damageDropped(this.meta) == block.block.damageDropped(block.meta);
			}
		}

		return false;
	}
}
