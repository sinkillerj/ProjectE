package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotConsume;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotInput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotLock;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotUnlearn;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class TransmutationContainer extends Container
{
	public final TransmutationInventory transmutationInventory;
	private final int blocked;

	public static TransmutationContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf)
	{
		Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		return new TransmutationContainer(windowId, invPlayer, hand);
	}

	public TransmutationContainer(int windowId, PlayerInventory invPlayer, Hand hand)
	{
		super(ObjHandler.TRANSMUTATION_CONTAINER, windowId);
		this.transmutationInventory = new TransmutationInventory(invPlayer.player);
		
		// Transmutation Inventory
		this.addSlot(new SlotInput(transmutationInventory, 0, 43, 23));
		this.addSlot(new SlotInput(transmutationInventory, 1, 34, 41));
		this.addSlot(new SlotInput(transmutationInventory, 2, 52, 41));
		this.addSlot(new SlotInput(transmutationInventory, 3, 16, 50));
		this.addSlot(new SlotInput(transmutationInventory, 4, 70, 50));
		this.addSlot(new SlotInput(transmutationInventory, 5, 34, 59));
		this.addSlot(new SlotInput(transmutationInventory, 6, 52, 59));
		this.addSlot(new SlotInput(transmutationInventory, 7, 43, 77));
		this.addSlot(new SlotLock(transmutationInventory, 8, 158, 50));
		this.addSlot(new SlotConsume(transmutationInventory, 9, 107, 97));
		this.addSlot(new SlotUnlearn(transmutationInventory, 10, 89, 97));
		this.addSlot(new SlotOutput(transmutationInventory, 11, 158, 9));
		this.addSlot(new SlotOutput(transmutationInventory, 12, 176, 13));
		this.addSlot(new SlotOutput(transmutationInventory, 13, 193, 30));
		this.addSlot(new SlotOutput(transmutationInventory, 14, 199, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 15, 193, 70));
		this.addSlot(new SlotOutput(transmutationInventory, 16, 176, 87));
		this.addSlot(new SlotOutput(transmutationInventory, 17, 158, 91));
		this.addSlot(new SlotOutput(transmutationInventory, 18, 140, 87));
		this.addSlot(new SlotOutput(transmutationInventory, 19, 123, 70));
		this.addSlot(new SlotOutput(transmutationInventory, 20, 116, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 21, 123, 30));
		this.addSlot(new SlotOutput(transmutationInventory, 22, 140, 13));
		this.addSlot(new SlotOutput(transmutationInventory, 23, 158, 31));
		this.addSlot(new SlotOutput(transmutationInventory, 24, 177, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 25, 158, 69));
		this.addSlot(new SlotOutput(transmutationInventory, 26, 139, 50));

		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 117 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 35 + i * 18, 175));

		blocked = hand == Hand.MAIN_HAND ? (inventorySlots.size() - 1) - (8 - invPlayer.currentItem) : -1;
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity var1)
	{
		return true;
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
		
		if (slotIndex <= 7) //Input Slots
		{
			return ItemStack.EMPTY;
		}
		else if (slotIndex >= 11 && slotIndex <= 26) // Output Slots
		{	
			long emc = EMCHelper.getEmcValue(newStack);
			
			int stackSize = 0;

			IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElseThrow(NullPointerException::new);

			while (transmutationInventory.getAvailableEMC() >= emc && stackSize < newStack.getMaxStackSize() && ItemHelper.hasSpace(player.inventory.mainInventory, newStack))
			{
				transmutationInventory.removeEmc(emc);
				ItemHandlerHelper.insertItemStacked(inv, ItemHelper.getNormalizedStack(stack), false);
				stackSize++;
			}
			
			transmutationInventory.updateClientTargets();
		}
		else if (slotIndex > 26)
		{
			long emc = EMCHelper.getEmcSellValue(stack);
			
			if (emc == 0 && stack.getItem() != ObjHandler.tome)
			{
				return ItemStack.EMPTY;
			}
			
			while(!transmutationInventory.hasMaxedEmc() && stack.getCount() > 0)
			{
				transmutationInventory.addEmc(emc);
				stack.shrink(1);
			}
			
			transmutationInventory.handleKnowledge(newStack);

			if (stack.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
		}
		
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, @Nonnull ClickType flag, PlayerEntity player)
	{
		if (player.getEntityWorld().isRemote && transmutationInventory.getHandlerForSlot(slot) == transmutationInventory.outputs)
		{
			PacketHandler.sendToServer(new SearchUpdatePKT(transmutationInventory.getIndexFromSlot(slot), getSlot(slot).getStack()));
		}

		if (slot == blocked)
		{
			return ItemStack.EMPTY;
		}

		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return !(slot instanceof SlotConsume || slot instanceof SlotUnlearn || slot instanceof SlotInput || slot instanceof SlotLock || slot instanceof SlotOutput);
	}
}
