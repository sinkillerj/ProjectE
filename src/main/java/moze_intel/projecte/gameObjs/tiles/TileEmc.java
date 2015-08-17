package moze_intel.projecte.gameObjs.tiles;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.api.tile.TileEmcBase;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

public abstract class TileEmc extends TileEmcBase
{
	public TileEmc()
	{
		setMaximumEMC(Constants.TILE_MAX_EMC);
	}
	
	public TileEmc(int maxAmount)
	{
		setMaximumEMC(maxAmount);
	}
	
	public boolean hasMaxedEmc()
	{
		return getStoredEmc() >= getMaximumEmc();
	}

	/**
	 * The amount provided will be divided and evenly distributed as best as possible between adjacent IEMCAcceptors
	 * Remainder or rejected EMC is added back to this provider
	 *
	 * @param emc The maximum combined emc to send to others
	 */
	public void sendToAllAcceptors(double emc)
	{
		if (!(this instanceof IEmcProvider))
		{
			// todo move this method somewhere
			throw new UnsupportedOperationException("sending without being a provider");
		}


		Map<ForgeDirection, TileEntity> tiles = Maps.filterValues(WorldHelper.getAdjacentTileEntitiesMapped(worldObj, this), Predicates.instanceOf(IEmcAcceptor.class));

		double emcPer = emc / tiles.size();
		for (Map.Entry<ForgeDirection, TileEntity> entry : tiles.entrySet())
		{
			if (this instanceof RelayMK1Tile && entry.getValue() instanceof RelayMK1Tile)
			{
				continue;
			}
			double provide = ((IEmcProvider) this).provideEMC(entry.getKey().getOpposite(), emcPer);
			double remain = provide - ((IEmcAcceptor) entry.getValue()).acceptEMC(entry.getKey(), provide);
			this.addEMC(remain);
		}
	}
}
