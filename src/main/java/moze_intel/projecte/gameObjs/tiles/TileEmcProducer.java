package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.ITileEmc;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public abstract class TileEmcProducer extends TileEmc
{
	public boolean[] areBlocksRequestingEmc = new boolean[6];
	
	public TileEmcProducer() {}
	
	public TileEmcProducer(int maxEmc)
	{
		super(maxEmc);
	}
	
	public int getNumRequesting()
	{
		int result = 0;
		
		for (EnumFacing e : EnumFacing.VALUES)
		{
			if (areBlocksRequestingEmc[e.getIndex()])
			{
				result++;
			}
		}
		
		return result;
	}
	
	/**
	 * The amount of emc must be previously devided by the amount of tiles requesting.
	 */
	public void sendEmcToRequesting(double emc)
	{
		for (EnumFacing dir : EnumFacing.VALUES)
		{
			if (!areBlocksRequestingEmc[dir.getIndex()])
			{
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(pos.offset(dir));
			
			if (tile instanceof ITileEmc)
			{
				((ITileEmc) tile).addEmc(emc);
			}
		}
	}
	
	public void checkSurroundingBlocks(boolean isFromRelay)
	{
		TileEmcProducer tile = (TileEmcProducer) worldObj.getTileEntity(pos);
		
		for (EnumFacing dir : EnumFacing.VALUES)
		{
			TileEntity closeTileEnt = worldObj.getTileEntity(pos.offset(dir));
			
			if (closeTileEnt == null)
			{
				tile.areBlocksRequestingEmc[dir.getIndex()] = false;
			}
			else if (isFromRelay && closeTileEnt instanceof RelayMK1Tile)
			{
				tile.areBlocksRequestingEmc[dir.getIndex()] = false;
			}
			else if (closeTileEnt instanceof ITileEmc)
			{
				tile.areBlocksRequestingEmc[dir.getIndex()] = ((ITileEmc) closeTileEnt).isRequestingEmc();
			}
		}
	}
	
	public void sendRelayBonus()
	{
		for (EnumFacing dir : EnumFacing.VALUES)
		{
			if (!areBlocksRequestingEmc[dir.getIndex()])
			{
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(pos.offset(dir));
			if (tile instanceof RelayMK3Tile)
			{
				((RelayMK3Tile) tile).addEmc(0.5F); //10 EMC/s -> 0.5 EMC/tick
			}
			else if (tile instanceof RelayMK2Tile)
			{
				((RelayMK2Tile) tile).addEmc(0.15F); //3 EMC/s -> 3 EMC/tick
			}
			else if (tile instanceof RelayMK1Tile)
			{
				((RelayMK1Tile) tile).addEmc(0.05F); //1 EMC/s -> 0.05 EMC/tick
			}
		}
	}
}
