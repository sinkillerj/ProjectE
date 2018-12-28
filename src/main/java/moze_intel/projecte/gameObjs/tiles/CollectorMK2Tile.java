package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;

public class CollectorMK2Tile extends CollectorMK1Tile
{
	public CollectorMK2Tile()
	{
		super(ObjHandler.COLLECTOR_MK2_TILE, Constants.COLLECTOR_MK2_MAX, Constants.COLLECTOR_MK2_GEN);
	}

	@Override
	protected int getInvSize()
	{
		return 12;
	}
}
