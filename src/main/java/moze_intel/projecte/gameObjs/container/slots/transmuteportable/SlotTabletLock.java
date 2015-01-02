package moze_intel.projecte.gameObjs.container.slots.transmuteportable;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTabletLock extends Slot
{
	private TransmuteTabletInventory table;
	
	public SlotTabletLock(TransmuteTabletInventory table, int par2, int par3, int par4) 
	{
		super(table, par2, par3, par4);
		this.table = table;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return Utils.doesItemHaveEmc(stack);
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		super.putStack(stack);
		
		if (stack.getItem() == ObjHandler.kleinStars)
		{
			int remainEmc = Constants.TILE_MAX_EMC - (int) Math.ceil(table.emc);
			
			if (ItemPE.getEmc(stack) >= remainEmc)
			{
				table.addEmc(remainEmc);
				ItemPE.removeEmc(stack, remainEmc);
			}
			else
			{
				table.addEmc(ItemPE.getEmc(stack));
				ItemPE.setEmc(stack, 0);
			}
			
			table.handleKnowledge(stack.copy());
			return;
		}
		
		if (stack.getItem() != ObjHandler.tome)
		{
			table.handleKnowledge(stack.copy());
		}
		else
		{
			table.updateOutputs();
		}
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
	{
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
		
		table.updateOutputs();
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
