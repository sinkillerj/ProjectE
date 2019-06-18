package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

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

	@Nonnull
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerIn)
	{
		return new CollectorMK2Container(windowId, playerInventory, this);
	}
}
