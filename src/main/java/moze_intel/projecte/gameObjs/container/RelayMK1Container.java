package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class RelayMK1Container extends LongContainer
{
	public final RelayMK1Tile tile;
	public double kleinChargeProgress = 0;
	public double inputBurnProgress = 0;
	public long emc = 0;

	public static RelayMK1Container fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		return new RelayMK1Container(windowId, invPlayer, (RelayMK1Tile) GuiHandler.getTeFromBuf(buf));
	}

	public RelayMK1Container(int windowId, PlayerInventory invPlayer, RelayMK1Tile relay)
	{
		this(ObjHandler.RELAY_MK1_CONTAINER, windowId, invPlayer, relay);
	}

	protected RelayMK1Container(ContainerType<?> type, int windowId, PlayerInventory invPlayer, RelayMK1Tile relay)
	{
		super(type, windowId);
		this.tile = relay;
		initSlots(invPlayer);
	}

	void initSlots(PlayerInventory invPlayer)
	{
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Klein Star charge slot
		this.addSlot(new ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV));

		int counter = input.getSlots() - 1;
		//Main Relay inventory
		for (int i = 0; i <= 1; i++)
			for (int j = 0; j <= 2; j++)
				this.addSlot(new ValidatedSlot(input, counter--, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV));

		//Burning slot
		this.addSlot(new ValidatedSlot(output, 0, 127, 43, SlotPredicates.IITEMEMC));

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 95 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 153));
	}

	@Override
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
		PacketHandler.sendProgressBarUpdateLong(listener, this, 0, (long) tile.getStoredEmc());
		PacketHandler.sendProgressBarUpdateInt(listener, this, 1, (int) (tile.getItemChargeProportion() * 8000));
		PacketHandler.sendProgressBarUpdateInt(listener, this, 2, (int) (tile.getInputBurnProportion() * 8000));
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		if (emc != ((long) tile.getStoredEmc()))
		{
			for (IContainerListener icrafting : this.listeners)
			{
				PacketHandler.sendProgressBarUpdateLong(icrafting, this, 0, ((long) tile.getStoredEmc()));
			}

			emc = ((long) tile.getStoredEmc());
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
	@OnlyIn(Dist.CLIENT)
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
	@OnlyIn(Dist.CLIENT)
	public void updateProgressBarLong(int id, long data)
	{
		switch (id)
		{
			case 0: emc = data; break;
			default: updateProgressBar(id, (int) data);
		}
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack())
		{
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex < 8)
		{
			if (!this.mergeItemStack(stack, 8, this.inventorySlots.size(), true))
				return ItemStack.EMPTY;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 7, false))
		{
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty())
		{
			slot.putStack(ItemStack.EMPTY);
		}
		else
		{
			slot.onSlotChanged();
		}

		return slot.onTake(player, newStack);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.relay
			&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}
