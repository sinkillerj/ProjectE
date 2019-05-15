package moze_intel.projecte.gameObjs.container.inventory;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.UpdateGemModePKT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class EternalDensityInventory implements IItemHandlerModifiable
{
	private final IItemHandlerModifiable inventory = new ItemStackHandler(9);
	private boolean isInWhitelist;
	public final ItemStack invItem;

	public EternalDensityInventory(ItemStack stack, EntityPlayer player)
	{
		this.invItem = stack;
        readFromNBT(stack.getOrCreateTag());
	}

	@Override
	public int getSlots()
	{
		return inventory.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return inventory.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		ItemStack ret = inventory.insertItem(slot, stack, simulate);
		writeBack();
		return ret;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack ret = inventory.extractItem(slot, amount, simulate);
		writeBack();
		return ret;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return inventory.isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack)
	{
		inventory.setStackInSlot(slot, stack);
		writeBack();
	}

	private void writeBack()
	{
		for (int i = 0; i < inventory.getSlots(); ++i)
		{
			if (inventory.getStackInSlot(i).isEmpty())
			{
				inventory.setStackInSlot(i, ItemStack.EMPTY);
			}
		}

		writeToNBT(invItem.getTag());
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		isInWhitelist = nbt.getBoolean("Whitelist");
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, nbt.getList("Items", Constants.NBT.TAG_COMPOUND));
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.putBoolean("Whitelist", isInWhitelist);
		nbt.put("Items", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null));
	}
	
	public void changeMode()
	{
		isInWhitelist = !isInWhitelist;
		writeBack();
		
		PacketHandler.sendToServer(new UpdateGemModePKT(isInWhitelist));
	}
	
	public boolean isWhitelistMode()
	{
		return isInWhitelist;
	}
}
