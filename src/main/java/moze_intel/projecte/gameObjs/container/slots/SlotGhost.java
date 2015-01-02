package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotGhost extends Slot
{
	public SlotGhost(IInventory inv, int slotIndex, int xPos, int yPost) 
	{
		super(inv, slotIndex, xPos, yPost);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if (stack != null && Utils.doesItemHaveEmc(stack))
		{
			this.putStack(Utils.getNormalizedStack(stack));
		}
		
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}
	
	@Override
	public int getSlotStackLimit() 
	{
		return 1;
	}
}
