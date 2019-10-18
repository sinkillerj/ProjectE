package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class AlchChestContainer extends Container
{
	private final AlchChestTile tile;
	
	public AlchChestContainer(int windowId, PlayerInventory invPlayer, AlchChestTile tile)
	{
		super(ObjHandler.ALCH_CHEST_CONTAINER, windowId);
		this.tile = tile;
		tile.numPlayersUsing++;

		IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
		//Chest Inventory
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 13; j++)
				this.addSlot(new SlotItemHandler(inv, j + i * 13, 12 + j * 18, 5 + i * 18));
		
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 152 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 48 + i * 18, 210));
	}

	public static AlchChestContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		return new AlchChestContainer(windowId, invPlayer,
				(AlchChestTile) GuiHandler.getTeFromBuf(buf));
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.alchChest
				&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);
		
		if (slot == null || !slot.getHasStack()) 
		{
			return ItemStack.EMPTY;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex < 104)
		{
			if (!this.mergeItemStack(stack, 104, this.inventorySlots.size(), false))
			{
				return ItemStack.EMPTY;
			}
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
		
		return newStack;
	}
	
	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		super.onContainerClosed(player);
		tile.numPlayersUsing--;
	}
}
