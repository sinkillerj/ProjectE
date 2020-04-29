package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class InterdictionTile extends TileEntity implements ITickableTileEntity {

	public InterdictionTile() {
		super(ObjHandler.INTERDICTION_TORCH_TILE);
	}

	@Override
	public void tick() {
		WorldHelper.repelEntitiesInterdiction(world, new AxisAlignedBB(pos.add(-8, -8, -8), pos.add(8, 8, 8)), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}
}