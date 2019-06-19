package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class RelayMK2Container extends RelayMK1Container
{
	public static RelayMK2Container fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		return new RelayMK2Container(windowId, invPlayer, (RelayMK2Tile) GuiHandler.getTeFromBuf(buf));
	}

	public RelayMK2Container(int windowId, PlayerInventory invPlayer, RelayMK2Tile relay)
	{
		super(ObjHandler.RELAY_MK2_CONTAINER, windowId, invPlayer, relay);
	}

	@Override
	void initSlots(PlayerInventory invPlayer)
	{
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Burn slot
		this.addSlot(new ValidatedSlot(input, 0, 84, 44, SlotPredicates.RELAY_INV));

		int counter = input.getSlots() - 1;
		//Inventory buffer
		for (int i = 0; i <= 2; i++)
			for (int j = 0; j <= 3; j++)
				this.addSlot(new ValidatedSlot(input, counter--, 26 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV));

		//Klein star slot
		this.addSlot(new ValidatedSlot(output, 0, 144, 44, SlotPredicates.IITEMEMC));

		//Main player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 16 + j * 18, 101 + i * 18));

		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 16 + i * 18, 159));
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

		if (slotIndex < 14)
		{
			if (!this.mergeItemStack(stack, 14, this.inventorySlots.size(), true))
				return ItemStack.EMPTY;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 13, false))
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
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.relayMK2
				&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}
