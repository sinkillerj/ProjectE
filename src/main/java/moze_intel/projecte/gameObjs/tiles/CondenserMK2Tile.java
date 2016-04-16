package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class CondenserMK2Tile extends CondenserTile
{
	private final CombinedInvWrapper joined = new CombinedInvWrapper(inputInventory, outputInventory);

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side == null)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(joined);
			}
			else if (side.getAxis().isHorizontal())
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputInventory);
			}
			else
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(outputInventory);
			}
		}

		return super.getCapability(cap, side);
	}

	@Override
	protected ItemStackHandler createInput()
	{
		return new StackHandler(42, true, false)
		{
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
			{
				if (!isStackEqualToLock(stack) && EMCHelper.doesItemHaveEmc(stack))
					return super.insertItem(slot, stack, simulate);
				else return stack;
			}
		};
	}

	@Override
	protected ItemStackHandler createOutput()
	{
		return new StackHandler(42, false, true);
	}

	@Override
	protected void condense()
	{
		while (this.hasSpace() && this.getStoredEmc() >= requiredEmc)
		{
			pushStack();
			this.removeEMC(requiredEmc);
		}

		if (this.hasSpace())
		{
			for (int i = 0; i < inputInventory.getSlots(); i++)
			{
				ItemStack stack = inputInventory.getStackInSlot(i);

				if (stack == null)
				{
					continue;
				}

				this.addEMC(EMCHelper.getEmcValue(stack) * stack.stackSize);
				inputInventory.setStackInSlot(i, null);
				break;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		outputInventory.deserializeNBT(nbt.getCompoundTag("Output"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setTag("Output", outputInventory.serializeNBT());
	}
}
