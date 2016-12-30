package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class EternalDensityContainer extends Container
{
	private final EternalDensityInventory inventory;
	
	public EternalDensityContainer(InventoryPlayer invPlayer, EternalDensityInventory gemInv)
	{
		inventory = gemInv;
		
		 for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
			{
				this.addSlotToContainer(new SlotGhost(gemInv, j + i * 3, 62 + j * 18, 26 + i * 18, SlotPredicates.HAS_EMC));
			}

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 93 + i * 18));
			}

		for (int i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 151));
		}

	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		Slot slot = getSlot(slotIndex);
		if (slotIndex > 8)
		{
			ItemStack toSet = slot.getStack().copy();
			toSet.setCount(1);
			ItemHandlerHelper.insertItem(inventory, toSet, false);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player)
	{
		return true;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot).getStack() == inventory.invItem)
		{
			return ItemStack.EMPTY;
		}
		
		if (slot >= 0 && slot < 9)
		{
			inventory.setStackInSlot(slot, ItemStack.EMPTY);
		}
		
		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return false;
	}
}
