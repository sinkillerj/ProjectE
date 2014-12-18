package sinkillerj.projecte.gameObjs.container.slots.trasmute;

import sinkillerj.projecte.gameObjs.ObjHandler;
import sinkillerj.projecte.gameObjs.items.ItemPE;
import sinkillerj.projecte.gameObjs.items.KleinStar;
import sinkillerj.projecte.gameObjs.tiles.TransmuteTile;
import sinkillerj.projecte.utils.Utils;
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
