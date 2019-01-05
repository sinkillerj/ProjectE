package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class CondenserMK2Tile extends CondenserTile
{
	public CondenserMK2Tile()
	{
		super(ObjHandler.CONDENSER_MK2_TILE);
	}

	protected IItemHandler createAutomationInventory()
	{
		IItemHandlerModifiable automationInput = new WrappedItemHandler(getInput(), WrappedItemHandler.WriteMode.IN)
		{
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
			{
				return SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack)
						? super.insertItem(slot, stack, simulate)
						: stack;
			}
		};
		IItemHandlerModifiable automationOutput = new WrappedItemHandler(getOutput(), WrappedItemHandler.WriteMode.OUT);
		return new CombinedInvWrapper(automationInput, automationOutput);
	}

	@Override
	protected ItemStackHandler createInput()
	{
		return new StackHandler(42);
	}

	@Override
	protected ItemStackHandler createOutput()
	{
		return new StackHandler(42);
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
			for (int i = 0; i < getInput().getSlots(); i++)
			{
				ItemStack stack = getInput().getStackInSlot(i);

				if (stack.isEmpty())
				{
					continue;
				}

				this.addEMC(EMCHelper.getEmcSellValue(stack) * stack.getCount());
				getInput().setStackInSlot(i, ItemStack.EMPTY);
				break;
			}
		}
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		super.read(nbt);
		getOutput().deserializeNBT(nbt.getCompound("Output"));
	}

	@Nonnull
	@Override
	public NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt = super.write(nbt);
		nbt.put("Output", getOutput().serializeNBT());
		return nbt;
	}
}
