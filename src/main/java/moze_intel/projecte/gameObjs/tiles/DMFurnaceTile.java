package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.Nonnull;

public class DMFurnaceTile extends RMFurnaceTile
{
	public DMFurnaceTile()
	{
		super(ObjHandler.DM_FURNACE_TILE, 10, 3);
	}
	
	@Override
	protected int getInvSize()
	{
		return 9;
	}

	@Override
	protected float getOreDoubleChance() {
		return 0.5F;
	}

	@Override
	public int getCookProgressScaled(int value)
	{
		return furnaceCookTime * value / ticksBeforeSmelt;
	}

	@Nonnull
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new DMFurnaceContainer(playerInventory, this);
	}
}