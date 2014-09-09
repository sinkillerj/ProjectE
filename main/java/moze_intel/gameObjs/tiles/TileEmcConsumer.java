package moze_intel.gameObjs.tiles;

import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEmcConsumer extends TileEntity
{
	private int emc;
	private final int MAX_AMOUNT = Constants.TILE_MAX_EMC;
	
	public void addEmc(int amount)
	{
		emc += amount;
		
		if (emc > MAX_AMOUNT || emc < 0)
			emc = MAX_AMOUNT;
	}
	
	public void addEmc(ItemStack stack)
	{
		addEmc(Utils.getEmcValue(stack) * stack.stackSize);
	}
	
	public void removeEmc(int amount)
	{
		emc -= amount;
		if (emc < 0)
			emc = 0;
	}
	
	public void removeItemRelativeEmc(ItemStack stack)
	{
		removeEmc(Utils.getEmcValue(stack));
	}
	
	public int getStoredEMC()
	{
		return emc;
	}
	
	public boolean hasMaxedEmc()
	{
		return emc == MAX_AMOUNT;
	}
	
	public void setEmcValue(int value)
	{
		emc = value;
	}
}
