package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.Constants;

public class RelayMK3Tile extends RelayMK1Tile
{
	public RelayMK3Tile()
	{
		super(20, Constants.RELAY_MK3_MAX, Constants.RELAY_MK3_OUTPUT);
	}

	@Override
	public String getInventoryName()
	{
		return "pe.relay.mk3";
	}
}
