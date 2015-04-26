package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class Matter extends ItemPE 
{
	private final String[] names = new String[] {"dark", "red"};
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public Matter()
	{
		this.setUnlocalizedName("matter");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{	
		return super.getUnlocalizedName() + "_" + names[stack.getItemDamage()];
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			if (stack.getItemDamage() == 0)
			{
				player.addStat(AchievementHandler.DARK_MATTER, 1);
			}
			else
			{
				player.addStat(AchievementHandler.RED_MATTER, 1);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 2; i++)
		{
			list.add(new ItemStack(item, 1, i));
		}
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
		icons = new IIcon[2];
		
		for (int i = 0; i < 2; i++)
		{
			icons[i] = register.registerIcon(this.getTexture("matter", names[i]));
		}
	}
}
