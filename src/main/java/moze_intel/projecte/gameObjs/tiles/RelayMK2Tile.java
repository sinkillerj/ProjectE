package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

public class RelayMK2Tile extends RelayMK1Tile
{
	public RelayMK2Tile()
	{
		super(ObjHandler.RELAY_MK2_TILE, 13, Constants.RELAY_MK2_MAX, Constants.RELAY_MK2_OUTPUT);
	}

	@Nonnull
	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerIn)
	{
		return new RelayMK2Container(playerInventory, this);
	}
}
