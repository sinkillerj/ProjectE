package moze_intel.gameObjs.tiles;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DMFurnaceTile extends RMFurnaceTile implements IInventory
{
	public DMFurnaceTile()
	{
		this.inventory = new ItemStack[19];
		this.ticksBeforeSmelt = 10;
		this.efficiencyBonus = 3;
		this.outputSlot = 10;
		this.inputStorage = new int[] {2, 9};
		this.outputStorage = new int[] {11, 18};
	}
	
	@Override
	public int getSizeInventory() 
	{
		return 19;
	}
	
	@SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int value)
    {
    	return furnaceCookTime * value / ticksBeforeSmelt;
    }
}