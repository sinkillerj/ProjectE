package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ITickable;

public class InterdictionTile extends TileEntity implements ITickable
{
	private AxisAlignedBB effectBounds = null;

	@Override
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
