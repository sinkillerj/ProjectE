package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

public class RelayMK3Tile extends RelayMK1Tile
{
	public RelayMK3Tile()
	{
		super(ObjHandler.RELAY_MK3_TILE, 21, Constants.RELAY_MK3_MAX, Constants.RELAY_MK3_OUTPUT);
	}

	@Nonnull
	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerIn)
	{
		return new RelayMK3Container(playerInventory, this);
	}
}
