package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class InterdictionTile extends TileEntity implements IUpdatePlayerListBox
{
	private AxisAlignedBB effectBounds = null;
	
	public void update()
	{
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();

		if (effectBounds == null)
		{
			effectBounds = new AxisAlignedBB(xCoord - 8, yCoord - 8, zCoord - 8, xCoord + 8, yCoord + 8, zCoord + 8);
		}
		WorldHelper.repelEntitiesInAABBFromPoint(worldObj, effectBounds, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, false);
	}
}
