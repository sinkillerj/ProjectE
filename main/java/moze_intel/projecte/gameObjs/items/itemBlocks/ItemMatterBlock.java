package moze_intel.projecte.gameObjs.items.itemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemMatterBlock extends ItemBlock
{
	public ItemMatterBlock(Block block) 
	{
		super(block);
		this.setMaxDamage(0);
		this.hasSubtypes = true;
	}
	
	@Override
    public String getUnlocalizedName(ItemStack stack)
    {
		if (stack.getItemDamage() == 0)
			return "tile.dm_block";
		else return "tile.rm_block";
    }
	
	@Override
    public int getMetadata(int meta)
    {
        return meta;
    }
}
