package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.Constants;

public class RelayMK2Tile extends RelayMK1Tile
{
	public RelayMK2Tile()
	{
		super(12, Constants.RELAY_MK2_MAX, Constants.RELAY_MK2_OUTPUT);
	}

	@Override
	public String getInventoryName()
	{
		return "pe.relay.mk2";
	}
}
