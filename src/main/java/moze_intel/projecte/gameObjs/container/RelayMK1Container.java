package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.relay.SlotRelayInput;
import moze_intel.projecte.gameObjs.container.slots.relay.SlotRelayKlein;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RelayMK1Container extends Container 
{
	private RelayMK1Tile tile;
	
	public RelayMK1Container(InventoryPlayer invPlayer, RelayMK1Tile relay)
	{
		this.tile = relay;
		tile.openInventory();
		
		//Klein Star charge slot
		this.addSlotToContainer(new SlotRelayInput(tile, 0, 67, 43));
		
		//Main Relay inventory
		for (int i = 0; i <= 1; i++) 
			for (int j = 0; j <= 2; j++) 
				this.addSlotToContainer(new SlotRelayInput(tile, i * 3 + j + 1, 27 + i * 18, 17 + j * 18));
		
		//Burning slot
		this.addSlotToContainer(new SlotRelayKlein(tile, 7, 127, 43));
		
		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 95 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 153));
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tile.closeInventory();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
	}
}
