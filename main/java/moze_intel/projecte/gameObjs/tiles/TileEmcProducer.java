package moze_intel.projecte.gameObjs.tiles;

import net.minecraft.tileentity.TileEntity;

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
			if (areBlocksRequestingEmc[i])
			{
				result++;
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
			
			int x = xCoord;
			int y = yCoord;
			int z = zCoord;
			
			switch (i)
			{
				case 0:
					x -= 1;
					break;
				case 1:
					x += 1;
					break;
				case 2:
					y -= 1;
					break;
				case 3:
					y += 1;
					break;
				case 4:
					z -= 1;
					break;
				case 5:
					z += 1;
					break;
			}
			
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			
			if (tile instanceof TileEmc)
				((TileEmc) worldObj.getTileEntity(x, y, z)).addEmc(emc);
			else
			{
				((TileEmcConsumerDirection) worldObj.getTileEntity(x, y, z)).addEmc(emc);
			}
		}
	}
	
	public void checkSurroundingBlocks(boolean isFromRelay)
	{
		TileEmcProducer tile = (TileEmcProducer) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		for (int i = 0; i < 6; i++)
		{
			int x = xCoord;
			int y = yCoord;
			int z = zCoord;
			
			switch (i)
			{
				case 0:
					x -= 1;
					break;
				case 1:
					x += 1;
					break;
				case 2:
					y -= 1;
					break;
				case 3:
					y += 1;
					break;
				case 4:
					z -= 1;
					break;
				case 5:
					z += 1;
					break;
			}
			
			TileEntity closeTileEnt = worldObj.getTileEntity(x, y, z);
			
			if (closeTileEnt == null)
				tile.areBlocksRequestingEmc[i] = false;
			else if (isFromRelay && closeTileEnt instanceof RelayMK1Tile)
				tile.areBlocksRequestingEmc[i] = false;
			else
			{
				if (closeTileEnt instanceof TileEmc)
				{
					if (((TileEmc) closeTileEnt).hasMaxedEmc())
						tile.areBlocksRequestingEmc[i] = false;
					else
						tile.areBlocksRequestingEmc[i] = ((TileEmc) closeTileEnt).isRequestingEmc;
				}
				else if (closeTileEnt instanceof TileEmcConsumerDirection)
				{
					if (((TileEmcConsumerDirection) closeTileEnt).hasMaxedEmc())
						tile.areBlocksRequestingEmc[i] = false;
					else
						tile.areBlocksRequestingEmc[i] = ((TileEmcConsumerDirection) closeTileEnt).isRequestingEmc;
				}
				else tile.areBlocksRequestingEmc[i] = false;
			}
		}
	}
	
	public void sendRelayBonus()
	{
		for (int i = 0; i < 6; i++)
		{
			if (!areBlocksRequestingEmc[i]) continue;
			
			int x = xCoord;
			int y = yCoord;
			int z = zCoord;
			
			switch (i)
			{
				case 0:
					x -= 1;
					break;
				case 1:
					x += 1;
					break;
				case 2:
					y -= 1;
					break;
				case 3:
					y += 1;
					break;
				case 4:
					z -= 1;
					break;
				case 5:
					z += 1;
					break;
			}
			
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			if (tile instanceof RelayMK3Tile)
				((RelayMK3Tile) tile).addEmc(0.5F); //10 EMC/s -> 0.5 EMC/tick 
			else if (tile instanceof RelayMK2Tile)
				((RelayMK2Tile) tile).addEmc(0.15F); //3 EMC/s -> 3 EMC/tick
			else if (tile instanceof RelayMK1Tile)
				((RelayMK1Tile) tile).addEmc(0.05F); //1 EMC/s -> 0.05 EMC/tick
		}
	}
}
