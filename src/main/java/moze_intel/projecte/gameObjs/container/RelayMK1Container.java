package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class RelayMK1Container extends Container 
{
	final RelayMK1Tile tile;
	public double kleinChargeProgress = 0;
	public double inputBurnProgress = 0;
	public int emc = 0;
	
	public RelayMK1Container(InventoryPlayer invPlayer, RelayMK1Tile relay)
	{
		this.tile = relay;
		initSlots(invPlayer);
	}

	void initSlots(InventoryPlayer invPlayer)
	{
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Klein Star charge slot
		this.addSlotToContainer(new ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV));

		int counter = input.getSlots() - 1;
		//Main Relay inventory
		for (int i = 0; i <= 1; i++)
			for (int j = 0; j <= 2; j++)
				this.addSlotToContainer(new ValidatedSlot(input, counter--, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV));

		//Burning slot
		this.addSlotToContainer(new ValidatedSlot(output, 0, 127, 43, SlotPredicates.IITEMEMC));

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 95 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 153));
	}

	@Override
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
		PacketHandler.sendProgressBarUpdateInt(listener, this, 0, (int) tile.getStoredEmc());
		PacketHandler.sendProgressBarUpdateInt(listener, this, 1, (int) (tile.getItemChargeProportion() * 8000));
		PacketHandler.sendProgressBarUpdateInt(listener, this, 2, (int) (tile.getInputBurnProportion() * 8000));
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		if (emc != ((int) tile.getStoredEmc()))
		{
			for (IContainerListener icrafting : this.listeners)
			{
				PacketHandler.sendProgressBarUpdateInt(icrafting, this, 0, ((int) tile.getStoredEmc()));
			}

			emc = ((int) tile.getStoredEmc());
		}

		if (kleinChargeProgress != tile.getItemChargeProportion())
		{
			for (IContainerListener icrafting : this.listeners)
			{
				PacketHandler.sendProgressBarUpdateInt(icrafting, this, 1, (int) (tile.getItemChargeProportion() * 8000));
			}

			kleinChargeProgress = tile.getItemChargeProportion();
		}

		if (inputBurnProgress != tile.getInputBurnProportion())
		{
			for (IContainerListener icrafting : this.listeners)
			{
				PacketHandler.sendProgressBarUpdateInt(icrafting, this, 2, (int) (tile.getInputBurnProportion() * 8000));
			}

			inputBurnProgress = tile.getInputBurnProportion();
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		switch (id)
		{
			case 0: emc = data; break;
			case 1: kleinChargeProgress = data / 8000.0; break;
			case 2: inputBurnProgress = data / 8000.0; break;
		}
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

		if (slotIndex < 8)
		{
			if (!this.mergeItemStack(stack, 8, this.inventorySlots.size(), true))
				return null;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 7, false))
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
	public boolean canInteractWith(@Nonnull EntityPlayer player)
	{
		return player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}
