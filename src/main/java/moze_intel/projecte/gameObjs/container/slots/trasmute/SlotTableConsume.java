package moze_intel.projecte.gameObjs.container.slots.trasmute;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.utils.Utils;
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
		
		double toAdd = 0;
		
		while (!tile.hasMaxedEmc() && stack.stackSize > 0)
		{
			toAdd += Utils.getEmcValue(stack);
			stack.stackSize--;
		}
		
		if (cache.getItem() == ObjHandler.kleinStars)
		{
			toAdd += KleinStar.getEmc(cache);
		}
		
		tile.addEmcWithPKT(toAdd);
		this.onSlotChanged();
		tile.handleKnowledge(cache);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !tile.hasMaxedEmc() && Utils.doesItemHaveEmc(stack);
	}
}
