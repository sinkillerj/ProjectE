package sinkillerj.projecte.gameObjs.container;

import sinkillerj.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import sinkillerj.projecte.gameObjs.container.slots.mercurial.SlotMercurialKlein;
import sinkillerj.projecte.gameObjs.container.slots.mercurial.SlotMercurialTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MercurialEyeContainer extends Container
{
	private MercurialEyeInventory inventory;
	
	public MercurialEyeContainer(InventoryPlayer invPlayer, MercurialEyeInventory mercEyeInv)
	{
		inventory = mercEyeInv;
		
		//Klein Star
		this.addSlotToContainer(new SlotMercurialKlein(inventory, 0, 50, 26));
		
		//Target
		this.addSlotToContainer(new SlotMercurialTarget(inventory, 1, 104, 26));
		
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 6 + j * 18, 56 + i * 18));
		
		//Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 6 + i * 18, 114));
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
	
	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) 
		{
			return null;
		}
		
		return super.slotClick(slot, button, flag, player);
	}
}
