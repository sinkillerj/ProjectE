package moze_intel.projecte.api.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Base class for the reference implementations TileEmcProvider, TileEmcAcceptor, and TileEmcHandler
 * Usually you want to use one of three derived reference implementations
 * Extend this if you want fine-grained control over all aspects of how your tile provides or accepts EMC
 *
 * @author williewillus
 */
public class TileEmcBase extends TileEntity implements IEmcStorage
{
	protected double maximumEMC;
	protected double currentEMC = 0;

	protected TileEmcBase()
	{
		setMaximumEMC(Double.MAX_VALUE);
	}

	public final void setMaximumEMC(double max)
	{
		maximumEMC = max;
		if (currentEMC > maximumEMC)
		{
			currentEMC = maximumEMC;
		}
	}

	@Override
	public double getStoredEmc()
	{
		return currentEMC;
	}

	@Override
	public double getMaximumEmc()
	{
		return maximumEMC;
	}

	/**
	 * Add EMC directly into the internal buffer. Use for internal implementation of your tile
	 */
	protected void addEMC(double toAdd)
	{
		currentEMC += toAdd;
		if (currentEMC > maximumEMC)
		{
			currentEMC = maximumEMC;
		}
	}

	/**
	 * Removes EMC directly into the internal buffer. Use for internal implementation of your tile
	 */
	protected void removeEMC(double toRemove)
	{
		currentEMC -= toRemove;
		if (currentEMC < 0)
		{
			currentEMC = 0;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		if (currentEMC > maximumEMC)
		{
			currentEMC = maximumEMC;
		}
		tag.setDouble("EMC", currentEMC);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		double set = tag.getDouble("EMC");
		if (set > maximumEMC)
		{
			set = maximumEMC;
		}
		currentEMC = set;
	}
}
