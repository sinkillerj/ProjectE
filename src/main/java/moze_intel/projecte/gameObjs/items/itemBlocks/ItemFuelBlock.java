package moze_intel.projecte.gameObjs.items.itemBlocks;

import moze_intel.projecte.gameObjs.EnumFuelType;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemFuelBlock extends ItemBlock
{
	private final EnumFuelType type;

	public ItemFuelBlock(Block block, Properties props, EnumFuelType type)
	{
		super(block, props);
		this.type = type;
	}
	
	@Override
	public int getBurnTime(ItemStack stack)
	{
		switch (type)
		{
			case ALCHEMICAL_COAL:
				return Constants.ALCH_BURN_TIME * 9;
			case MOBIUS_FUEL:
				return Constants.MOBIUS_BURN_TIME * 9;
			case AETERNALIS_FUEL:
				return Constants.AETERNALIS_BURN_TIME * 9;
			default: return -1;
		}
	}
}
