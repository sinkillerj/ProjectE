package moze_intel.projecte.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

// TODO 1.8 get rid of this (World Transmutations will need to be reworked)
public class MetaBlock
{
	private Block block;
	private int meta;

	public MetaBlock(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		this.block = state.getBlock();
		this.meta = this.block.getMetaFromState(state);
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

	public void setInWorld(World world, BlockPos pos)
	{
		world.setBlockState(pos, this.getBlock().getStateFromMeta(meta), 2);
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
			MetaBlock other = (MetaBlock) obj;

			if (this.block == other.getBlock())
			{
				return this.meta == other.meta || this.block.damageDropped(this.block.getStateFromMeta(meta)) == other.block.damageDropped(other.block.getStateFromMeta(other.meta));
			}
		}

		return false;
	}
}
