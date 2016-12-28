package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CreativeTab extends CreativeTabs
{
	public CreativeTab()
	{
		super(PECore.MODID);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem()
	{
		return new ItemStack(ObjHandler.philosStone);
	}
}
