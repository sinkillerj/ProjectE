package moze_intel.projecte.gameObjs.items.itemBlocks;

import moze_intel.projecte.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemFuelBlock extends ItemBlock
{
	private final int burnTime;

	public ItemFuelBlock(Block block, Builder builder, int burnTime)
	{
		super(block, builder);
		this.burnTime = burnTime;
	}
	
	@Override
	public int getBurnTime(ItemStack stack)
	{
		return burnTime;
	}
}
