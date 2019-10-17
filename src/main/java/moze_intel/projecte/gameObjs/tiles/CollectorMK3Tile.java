package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

public class CollectorMK3Tile extends CollectorMK1Tile
{
	public CollectorMK3Tile()
	{
		super(ObjHandler.COLLECTOR_MK3_TILE, Constants.COLLECTOR_MK3_MAX, Constants.COLLECTOR_MK3_GEN);
	}

	@Override
	protected int getInvSize()
	{
		return 16;
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerIn)
	{
		return new CollectorMK3Container(windowId, playerInventory, this);
	}
}
