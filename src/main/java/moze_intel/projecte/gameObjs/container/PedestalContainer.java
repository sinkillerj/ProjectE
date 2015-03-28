package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PedestalContainer extends Container
{
	public PedestalContainer(InventoryPlayer inventory, DMPedestalTile tile)
	{
		//Pedestal Inventory
		this.addSlotToContainer(new Slot(tile, 0, 80, 20));

		int slotIndex = -1;
		//Player Hotbar
		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(inventory, ++slotIndex, 18 * i + 8, 112));
		}
		//Player Inventory
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(inventory, ++slotIndex, 18 * j + 8, 18 * i + 54));
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_)
	{
		return true;
	}
}
