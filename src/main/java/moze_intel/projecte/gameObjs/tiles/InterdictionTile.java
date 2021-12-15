package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class InterdictionTile extends TileEntity implements ITickableTileEntity {

	public InterdictionTile() {
		super(PETileEntityTypes.INTERDICTION_TORCH.get());
	}

	@Override
	public void tick() {
		WorldHelper.repelEntitiesInterdiction(level, new AxisAlignedBB(worldPosition.offset(-8, -8, -8), worldPosition.offset(8, 8, 8)), worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
	}
}