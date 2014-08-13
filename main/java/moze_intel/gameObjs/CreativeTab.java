package moze_intel.gameObjs;

import moze_intel.MozeCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs
{
	public CreativeTab()
	{
		super(MozeCore.MODID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() 
	{
		return ObjHandler.philosStone;
	}
}
