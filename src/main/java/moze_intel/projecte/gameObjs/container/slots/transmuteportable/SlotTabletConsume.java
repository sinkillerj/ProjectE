package moze_intel.projecte.gameObjs.container.slots.transmuteportable;

import cpw.mods.fml.common.FMLCommonHandler;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.utils.Utils;
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
