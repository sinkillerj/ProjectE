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
		maxAmount = Constants.tileEmcConsumerMaxEmc;
	}
	
	public TileEmcConsumerDirection(int maxAmount)
	{
		this.maxAmount = maxAmount;
	}
	
	public void AddEmc(double amount)
	{
		emc += amount;
		
		if (emc > maxAmount)
		{
			emc = maxAmount;
		}
	}
	public void AddEmc(ItemStack stack)
	{
		AddEmc(Utils.GetEmcValue(stack) * stack.stackSize);
	}
	
	public void RemoveEmc(double amount)
	{
		emc -= amount;
		
		if (emc < 0)
		{
			emc = 0;
		}
	}
	
	public void RemoveItemRelativeEmc(ItemStack stack)
	{
		RemoveEmc(Utils.GetEmcValue(stack));
	}
	
	public double GetStoredEMC()
	{
		return emc;
	}
	
	public int GetMaxEmc()
	{
		return maxAmount;
	}
	
	public boolean HasMaxedEmc()
	{
		return emc == maxAmount;
	}
	
	public void SetEmcValue(double value)
	{
		emc = value;
	}
}
