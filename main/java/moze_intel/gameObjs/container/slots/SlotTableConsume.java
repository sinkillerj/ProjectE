package moze_intel.gameObjs.container.slots;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.ItemBase;
import moze_intel.gameObjs.tiles.TransmuteTile;
import moze_intel.utils.Utils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTableConsume extends Slot
{
	private TransmuteTile tile;
	
	public SlotTableConsume(TransmuteTile tile, int par2, int par3, int par4) 
	{
		super(tile, par2, par3, par4);
		this.tile = tile;
	}
	
	@Override
	public void putStack(ItemStack stack)
    {
		if (stack == null)
		{
			return;
		}
		
		ItemStack cache = stack.copy();
		
		if (stack.getItem().equals(ObjHandler.kleinStars))
		{
			int remainEmc = tile.getMaxEmc() - (int) Math.ceil(tile.getStoredEMC());
			
			if (ItemBase.getEmc(stack) >= remainEmc)
			{
				tile.addEmc(remainEmc);
				ItemBase.removeEmc(stack, remainEmc);
			}
			else
			{
				tile.addEmc(ItemBase.getEmc(stack));
				ItemBase.setEmc(stack, 0);
			}
			
			super.putStack(stack);
	        tile.handleKnowledge(cache);
	        return;
		}
		
		int toAdd = 0;
		
		while (!tile.hasMaxedEmc() && stack.stackSize > 0)
		{
			toAdd += Utils.getEmcValue(stack);
			stack.stackSize--;
		}
		
		tile.addEmc(toAdd);
        this.onSlotChanged();
        tile.handleKnowledge(cache);
    }
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !tile.hasMaxedEmc() && Utils.doesItemHaveEmc(stack);
	}
}
