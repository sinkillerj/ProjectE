package moze_intel.gameObjs.tiles;

import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEmcConsumer extends TileEntity
{
	private int emc;
	private final int maxAmount = Constants.tileEmcConsumerMaxEmc;
	
	public void AddEmc(int amount)
	{
		emc += amount;
		if (emc > maxAmount || emc < 0)
			emc = maxAmount;
	}
	
	public void AddEmc(ItemStack stack)
	{
		AddEmc(Utils.GetEmcValue(stack) * stack.stackSize);
	}
	
	public void RemoveEmc(int amount)
	{
		emc -= amount;
		if (emc < 0)
			emc = 0;
	}
	
	public void RemoveItemRelativeEmc(ItemStack stack)
	{
		RemoveEmc(Utils.GetEmcValue(stack));
	}
	
	public int GetStoredEMC()
	{
		return emc;
	}
	
	public boolean HasMaxedEmc()
	{
		return emc == maxAmount;
	}
	
	public void SetEmcValue(int value)
	{
		emc = value;
	}
}
