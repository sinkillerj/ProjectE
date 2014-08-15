package moze_intel.gameObjs.tiles;

import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
import net.minecraft.item.ItemStack;

public class TileEmcConsumerDirection extends TileEntityDirection
{
	private double emc;
	private final int maxAmount;
	public boolean isRequestingEmc = false;
	
	public TileEmcConsumerDirection()
	{
		maxAmount = Constants.TILE_MAX_EMC;
	}
	
	public TileEmcConsumerDirection(int maxAmount)
	{
		this.maxAmount = maxAmount;
	}
	
	public void addEmc(double amount)
	{
		emc += amount;
		
		if (emc > maxAmount)
		{
			emc = maxAmount;
		}
	}
	public void addEmc(ItemStack stack)
	{
		addEmc(Utils.getEmcValue(stack) * stack.stackSize);
	}
	
	public void removeEmc(double amount)
	{
		emc -= amount;
		
		if (emc < 0)
		{
			emc = 0;
		}
	}
	
	public void removeItemRelativeEmc(ItemStack stack)
	{
		removeEmc(Utils.getEmcValue(stack));
	}
	
	public double getStoredEMC()
	{
		return emc;
	}
	
	public int getMaxEmc()
	{
		return maxAmount;
	}
	
	public boolean hasMaxedEmc()
	{
		return emc == maxAmount;
	}
	
	public void setEmcValue(double value)
	{
		emc = value;
	}
}
