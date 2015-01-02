package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.utils.Utils;
import net.minecraft.item.ItemStack;

public class CondenserMK2Tile extends CondenserTile
{
	private static final int INPUT_SLOTS[] = {1, 42};
	private static final int OUTPUT_SLOTS[] = {43, 84};

	public CondenserMK2Tile()
	{
		this.inventory = new ItemStack[85];
		this.loadChecks = false;
	}

	@Override
	protected void condense()
	{
		for (int i = INPUT_SLOTS[0]; i <= INPUT_SLOTS[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null || isStackEqualToLock(stack))
			{
				continue;
			}

			this.addEmc(Utils.getEmcValue(stack) * stack.stackSize);
			inventory[i] = null;
			break;
		}

		while (this.hasSpace() && this.getStoredEmc() >= requiredEmc)
		{
			pushStack();
			this.removeEmc(requiredEmc);
		}
	}

	@Override
	protected boolean hasSpace()
	{
		for (int i = OUTPUT_SLOTS[0]; i <= OUTPUT_SLOTS[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null)
			{
				return true;
			}

			if (isStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize())
			{
				return true;
			}
		}

		return false;
	}

	@Override
	protected int getSlotForStack()
	{
		for (int i = OUTPUT_SLOTS[0]; i <= OUTPUT_SLOTS[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null)
			{
				return i;
			}

			if (isStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize())
			{
				return i;
			}
		}

		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		if (slot == 0 || slot >= OUTPUT_SLOTS[0])
		{
			return false;
		}

		return !isStackEqualToLock(stack) && Utils.doesItemHaveEmc(stack);
	}
}
