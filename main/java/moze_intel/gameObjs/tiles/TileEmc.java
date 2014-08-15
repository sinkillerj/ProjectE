package moze_intel.gameObjs.tiles;

import moze_intel.MozeCore;
import moze_intel.network.packets.TTableSyncPKT;
import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
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
		
		if (emc > maxAmount)
		{
			emc = maxAmount;
		}
		
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
		
		sendUpdatePKT();
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
		
		sendUpdatePKT();
	}
	
	public void setEmcWithoutPKT(double value)
	{
		emc = value;
	}
	
	public void sendUpdatePKT()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			MozeCore.pktHandler.sendToAll(new TTableSyncPKT(emc, this.xCoord, this.yCoord, this.zCoord));
		}
	}
}
