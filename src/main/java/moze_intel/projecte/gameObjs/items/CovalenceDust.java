package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import java.util.List;

public class CovalenceDust extends ItemPE
{
	private final String[] names = new String[] {"low", "medium", "high"};
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public CovalenceDust()
	{
		this.setUnlocalizedName("covalence_dust");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{	
		if (stack.getItemDamage() > 2)
		{
			return "pe.debug.metainvalid";
		}

		return super.getUnlocalizedName()+ "_" + names[MathHelper.clamp_int(stack.getItemDamage(), 0, 2)];
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 3; ++i)
			list.add(new ItemStack(item, 1, i));
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)
	{
		return icons[MathHelper.clamp_int(par1, 0, 2)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		icons = new IIcon[3];
		for (int i = 0; i < 3; i++)
			icons[i] = register.registerIcon(this.getTexture("covalence_dust", names[i]));
	}
}
