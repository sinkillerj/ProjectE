package moze_intel.projecte.gameObjs.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PhilosStoneContainer extends Container
{
	private final CraftingInventory craftMatrix = new CraftingInventory(this, 3, 3);
	private final CraftResultInventory craftResult = new CraftResultInventory();
	private final World worldObj;
	private final PlayerEntity player;
	
	public PhilosStoneContainer(PlayerInventory invPlayer)
	{
		this.player = invPlayer.player;
		this.worldObj = player.getEntityWorld();

		//CraftingResult
		this.addSlot(new CraftingResultSlot(invPlayer.player, this.craftMatrix, this.craftResult, 0, 124, 35));
		
		//Crafting grid
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				this.addSlot(new Slot(craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));

		//Player inv
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
		
		this.onCraftMatrixChanged(craftMatrix);
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inv)
	{
		this.slotChangedCraftingGrid(player.world, this.player, this.craftMatrix, this.craftResult);
	}
	
	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		super.onContainerClosed(player);

		if (!this.worldObj.isRemote)
		{
			this.clearContainer(player, this.worldObj, this.craftMatrix);
		}
	}
	
	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return true;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index == 0)
			{
				if (!this.mergeItemStack(itemstack1, 10, 46, true))
				{
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (index >= 10 && index < 37)
			{
				if (!this.mergeItemStack(itemstack1, 37, 46, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (index >= 37 && index < 46)
			{
				if (!this.mergeItemStack(itemstack1, 10, 37, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 10, 46, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			itemstack = slot.onTake(player, itemstack1);
		}
		
		return itemstack;
	}
	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot)
	{
		return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
	}
}
