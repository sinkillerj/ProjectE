package moze_intel.projecte.gameObjs.container;

import com.google.common.base.Predicates;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.container.slots.condenser.SlotCondenserMK2Lock;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class CondenserMK2Container extends Container
{
	public final CondenserMK2Tile tile;

	public CondenserMK2Container(InventoryPlayer invPlayer, CondenserMK2Tile condenser)
	{
		tile = condenser;
		tile.numPlayersUsing++;

		//Item Lock Slot
		this.addSlotToContainer(new SlotCondenserMK2Lock(this, 0, 12, 6));

		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Condenser Inventory
		//Inputs
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 6; j++)
				this.addSlotToContainer(new ValidatedSlot(input, j + i * 6, 12 + j * 18, 26 + i * 18, SlotPredicates.HAS_EMC));

		//Outputs
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 6; j++)
				this.addSlotToContainer(new ValidatedSlot(output, j + i * 6, 138 + j * 18, 26 + i * 18, Predicates.<ItemStack>alwaysFalse()));

		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 48 + i * 18, 212));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		if (slotIndex == 0)
		{
			return null;
		}

		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack())
		{
			return null;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex <= 84)
		{
			if (!this.mergeItemStack(stack, 85, 120, false))
			{
				return null;
			}
		}
		else if (!EMCHelper.doesItemHaveEmc(stack) || !this.mergeItemStack(stack, 1, 42, false))
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

		slot.onPickupFromSlot(player, stack);
		return newStack;
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player)
	{
		return player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tile.numPlayersUsing--;
	}

	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player)
	{
		if (slot == 0 && tile.getLock().getStackInSlot(0) != null)
		{
			if (!player.worldObj.isRemote)
			{
				tile.getLock().setStackInSlot(0, null);
				tile.checkLockAndUpdate();
				this.detectAndSendChanges();
			}

			return null;
		} else return super.slotClick(slot, button, flag, player);
	}
}
