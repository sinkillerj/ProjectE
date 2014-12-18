package sinkillerj.projecte.gameObjs.container.slots.transmuteportable;

import cpw.mods.fml.common.FMLCommonHandler;
import sinkillerj.projecte.gameObjs.ObjHandler;
import sinkillerj.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import sinkillerj.projecte.gameObjs.items.ItemPE;
import sinkillerj.projecte.gameObjs.items.KleinStar;
import sinkillerj.projecte.gameObjs.tiles.TransmuteTile;
import sinkillerj.projecte.utils.Utils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTabletConsume extends Slot
{
	private TransmuteTabletInventory table;
	
	public SlotTabletConsume(TransmuteTabletInventory table, int par2, int par3, int par4) 
	{
		super(table, par2, par3, par4);
		this.table = table;
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
		
		while (!table.hasMaxedEmc() && stack.stackSize > 0)
		{
			toAdd += Utils.getEmcValue(stack);
			stack.stackSize--;
		}
		
		if (cache.getItem() == ObjHandler.kleinStars)
		{
			toAdd += KleinStar.getEmc(cache);
		}
		
		table.addEmc(toAdd);
        this.onSlotChanged();
        table.handleKnowledge(cache);
    }
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !table.hasMaxedEmc() && Utils.doesItemHaveEmc(stack);
	}
}
