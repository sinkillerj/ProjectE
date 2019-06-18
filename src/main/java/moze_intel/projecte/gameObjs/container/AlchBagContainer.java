package moze_intel.projecte.gameObjs.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

// todo 1.13 @ChestContainer(isLargeChest = true, rowSize = 13)
public class AlchBagContainer extends Container
{
	public final Hand hand;
	private final int blocked;
	private final boolean immutable;

	public AlchBagContainer(PlayerInventory invPlayer, Hand hand, IItemHandlerModifiable invBag)
	{
		this(invPlayer, hand, invBag, false);
	}
	
	public AlchBagContainer(PlayerInventory invPlayer, Hand hand, IItemHandlerModifiable invBag, boolean immutable)
	{
		this.hand = hand;
		this.immutable = immutable;

		//Bag Inventory
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 13; j++)
				this.addSlot(new SlotItemHandler(invBag, j + i * 13, 12 + j * 18, 5 + i * 18));
				
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 152 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 48 + i * 18, 210));

		blocked = hand == Hand.MAIN_HAND ? (inventorySlots.size() - 1) - (8 - invPlayer.currentItem) : -1;
	}
	
	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return true;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
	{
		if (immutable)
		{
			return ItemStack.EMPTY;
		}

		Slot slot = this.getSlot(slotIndex);
		
		if (!slot.getHasStack())
		{
			return ItemStack.EMPTY;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex < 104)
		{
			if (!this.mergeItemStack(stack, 104, this.inventorySlots.size(), true))
				return ItemStack.EMPTY;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 104, false))
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

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, PlayerEntity player)
	{
		if (slot == blocked || immutable)
		{
			return ItemStack.EMPTY;
		}
		
		return super.slotClick(slot, button, flag, player);
	}
}
