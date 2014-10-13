package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.TTableSyncPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEmc extends TileEntity
{
	private double emc;
	private final int maxAmount;
	public boolean isRequestingEmc = false;
	
	public TileEmc()
	{
		maxAmount = Constants.TILE_MAX_EMC;
	}
	
	public TileEmc(int maxAmount)
	{
		this.maxAmount = maxAmount;
	}
	
	public void addEmc(double amount)
	{
		emc += amount;
		
		if (emc > maxAmount || emc < 0)
		{
			emc = maxAmount;
		}
	}
	
	public void addEmcWithPKT(double amount)
	{
		addEmc(amount);
		
		sendUpdatePKT();
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
	
	public void removeEmcWithPKT(double amount)
	{
		removeEmc(amount);
		
		sendUpdatePKT();
	}
	
	public void removeItemRelativeEmc(ItemStack stack)
	{
		removeEmc(Utils.getEmcValue(stack));
	}
	
	public void removeItemRelativeEmcWithPKT(ItemStack stack)
	{
		removeItemRelativeEmc(stack);
		
		sendUpdatePKT();
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
		return emc >= maxAmount;
	}
	
	public void setEmcValue(double value)
	{
		emc = value;
	}
	
	public void setEmcValueWithPKT(double value)
	{
		setEmcValue(value);
		
		sendUpdatePKT();
	}
	
	public void sendUpdatePKT()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			PacketHandler.sendToAll(new TTableSyncPKT(emc, this.xCoord, this.yCoord, this.zCoord));
		}
	}
}
