package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.tile.ITileEmc;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEmcProducer extends TileEmc
{
	public boolean[] areBlocksRequestingEmc = new boolean[6];
	
	public TileEmcProducer()
	{
		super();
	}
	
	public TileEmcProducer(int maxEmc)
	{
		super(maxEmc);
	}
	
	public int getNumRequesting()
	{
		int result = 0;
		
		for (int i = 0; i < 6; i++)
		{
			if (areBlocksRequestingEmc[i])
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
		for (int i = 0; i < 6; i++)
		{
			if (!areBlocksRequestingEmc[i]) 
			{
				continue;
			}
			
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			
			if (tile instanceof ITileEmc)
			{
				((ITileEmc) tile).addEmc(emc);
			}
		}
	}
	
	public void checkSurroundingBlocks(boolean isFromRelay)
	{
		TileEmcProducer tile = (TileEmcProducer) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		
		for (int i = 0; i < 6; i++)
		{
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			
			TileEntity closeTileEnt = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			
			if (closeTileEnt == null)
			{
				tile.areBlocksRequestingEmc[i] = false;
			}
			else if (isFromRelay && closeTileEnt instanceof RelayMK1Tile)
			{
				tile.areBlocksRequestingEmc[i] = false;
			}
			else if (closeTileEnt instanceof ITileEmc)
			{
				tile.areBlocksRequestingEmc[i] = ((ITileEmc) closeTileEnt).isRequestingEmc();
			}
		}
	}
	
	public void sendRelayBonus()
	{
		for (int i = 0; i < 6; i++)
		{
			if (!areBlocksRequestingEmc[i]) 
			{
				continue;
			}
			
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			
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
