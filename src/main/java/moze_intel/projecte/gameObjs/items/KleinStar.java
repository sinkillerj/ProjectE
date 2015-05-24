package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class KleinStar extends ItemPE
{
	public KleinStar()
	{
		this.setUnlocalizedName("klein_star");
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setNoRepair();
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
		
		return 1.0D - starEmc / (double) EMCHelper.getKleinStarMaxEmc(stack);
	}

	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote && PECore.DEV_ENVIRONMENT)
		{
			setEmc(stack, EMCHelper.getKleinStarMaxEmc(stack));
		}
		
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
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

	public enum EnumKleinTier
	{
		EIN("ein"),
		ZWEI("zwei"),
		DREI("drei"),
		VIER("vier"),
		SPHERE("sphere"),
		OMEGA("omega");

		public final String name;
		EnumKleinTier(String name)
		{
			this.name = name;
		}
	}
}
