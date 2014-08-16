package moze_intel.gameObjs.container.slots;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.ItemBase;
import moze_intel.gameObjs.tiles.TransmuteTile;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTableInput extends Slot
{
	private TransmuteTile tile;
	
	public SlotTableInput(TransmuteTile tile, int par2, int par3, int par4) 
	{
		super(tile, par2, par3, par4);
		this.tile = tile;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !tile.hasMaxedEmc() && Utils.doesItemHaveEmc(stack);
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		super.putStack(stack);
		
		if (stack.getItem().equals(ObjHandler.kleinStars))
		{
			int remainingEmc = Utils.getKleinStarMaxEmc(stack) - (int) Math.ceil(ItemBase.getEmc(stack));
			
			if (tile.getStoredEMC() >= remainingEmc)
			{
				ItemBase.addEmc(stack, remainingEmc);
				tile.removeEmcWithPKT(remainingEmc);
			}
			else
			{
				ItemBase.addEmc(stack, tile.getStoredEMC());
				tile.setEmcValueWithPKT(0);
			}
		}
		else
		{
			tile.addEmcWithPKT(Utils.getEmcValue(stack));
		}
		
		tile.handleKnowledge(stack.copy());
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack stack)
	{
		super.onPickupFromSlot(par1EntityPlayer, stack);
		
		if (!stack.getItem().equals(ObjHandler.kleinStars))
		{
			tile.removeEmcWithPKT(Utils.getEmcValue(stack));
			tile.updateOutputs();
		}
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
