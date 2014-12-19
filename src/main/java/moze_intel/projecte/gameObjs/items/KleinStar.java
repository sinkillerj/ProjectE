package moze_intel.projecte.gameObjs.items;

import java.util.List;

import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KleinStar extends ItemPE
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public KleinStar()
	{
		this.setUnlocalizedName("klein_star");
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		double starEmc = this.getEmc(stack);
		
		if (starEmc == 0)
		{
			return 1.0D;
		}
		
		return 1.0D - starEmc / (double)Utils.getKleinStarMaxEmc(stack);
	}

	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		/*if (!world.isRemote)
		{
			this.setEmc(stack, Utils.GetKleinStarMaxEmc(stack));
		}*/
		
		return stack;
	}
	
	/*@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		if (!world.isRemote)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
	}*/
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			if (stack.getItemDamage() == 5)
			{
				player.addStat(AchievementHandler.KLEIN_MASTER, 1);
			}
			else
			{
				player.addStat(AchievementHandler.KLEIN_BASIC, 1);
			}
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{	
		return super.getUnlocalizedName()+ "_"+(stack.getItemDamage() + 1);
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 6; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)
	{
		return icons[MathHelper.clamp_int(par1, 0, 6)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		icons = new IIcon[6];
		
		for (int i = 0; i < 6; i++)
		{
			icons[i] = register.registerIcon(this.getTexture("stars", "klein_star_"+(i + 1)));
		}
	}
}
