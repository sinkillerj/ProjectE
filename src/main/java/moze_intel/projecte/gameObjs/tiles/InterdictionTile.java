package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class InterdictionTile extends TileEntity implements ITickable
{
	@Override
	public void update()
	{
		WorldHelper.repelEntitiesInAABBFromPoint(world, new AxisAlignedBB(pos.add(-8, -8, -8), pos.add(8, 8, 8)), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, false);
	}
}
