package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.relay.SlotRelayInput;
import moze_intel.projecte.gameObjs.container.slots.relay.SlotRelayKlein;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RelayMK3Container extends Container
{
	private RelayMK3Tile tile;
	
	public RelayMK3Container(InventoryPlayer invPlayer, RelayMK3Tile relay)
	{
		this.tile = relay;
		tile.openInventory();
		
		//Burn slot
		this.addSlotToContainer(new SlotRelayInput(tile, 0, 104, 58));
		 
		//Inventory Buffer
		for (int i = 0; i <= 3; i++) 
			for (int j = 0; j <= 4; j++)
				this.addSlotToContainer(new SlotRelayInput(tile, i * 5 + j + 1, 28 + i * 18, 18 + j * 18));

		//Klein star charge
		this.addSlotToContainer(new SlotRelayKlein(tile, 21, 164, 58));
			
		//Main player inventory
		for (int i = 0; i < 3; i++) 
			for (int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 26 + j * 18, 113 + i * 18));

		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 26 + i * 18, 171));
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tile.closeInventory();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack())
		{
			return null;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex < 22)
		{
			if (!this.mergeItemStack(stack, 22, this.inventorySlots.size(), true))
				return null;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 21, false))
		{
			return null;
		}
		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}

		slot.onPickupFromSlot(player, newStack);
		return newStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5) <= 64.0;
	}
}
