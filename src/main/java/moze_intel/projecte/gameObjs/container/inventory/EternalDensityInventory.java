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

public class EternalDensityInventory implements IItemHandlerModifiable
{
	private final IItemHandlerModifiable inventory = new ItemStackHandler(9) {
		@Override
		protected int getStackLimit(int slot, ItemStack stack) { return 1; }
	};
	private boolean isInWhitelist;
	public final ItemStack invItem;

	public EternalDensityInventory(ItemStack stack, EntityPlayer player)
	{
		this.invItem = stack;
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		readFromNBT(stack.getTagCompound());
	}

	@Override
	public int getSlots()
	{
		return inventory.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		ItemStack ret = inventory.insertItem(slot, stack, simulate);
		writeBack();
		return ret;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack ret = inventory.extractItem(slot, amount, simulate);
		writeBack();
		return ret;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		inventory.setStackInSlot(slot, stack);
		writeBack();
	}

	private void writeBack()
	{
		for (int i = 0; i < inventory.getSlots(); ++i)
		{
			if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).stackSize == 0)
			{
				inventory.setStackInSlot(i, null);
			}
		}

		writeToNBT(invItem.getTagCompound());
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		isInWhitelist = nbt.getBoolean("Whitelist");
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND));
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("Whitelist", isInWhitelist);
		nbt.setTag("Items", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null));
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

	public int findFirstEmptySlot()
	{
		for (int i = 0; i < inventory.getSlots(); i++)
		{
			if (inventory.getStackInSlot(i) == null)
			{
				return i;
			}
		}
		return -1;
	}
}
