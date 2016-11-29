package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class KleinStar extends ItemPE implements IItemEmc
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
		double starEmc = getEmc(stack);
		
		if (starEmc == 0)
		{
			return 1.0D;
		}
		
		return 1.0D - starEmc / (double) EMCHelper.getKleinStarMaxEmc(stack);
	}

	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote && PECore.DEV_ENVIRONMENT)
		{
			setEmc(stack, EMCHelper.getKleinStarMaxEmc(stack));
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
	
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (stack.getItemDamage() > 5)
		{
			return "pe.debug.metainvalid";
		}

		return super.getUnlocalizedName()+ "_" + (stack.getItemDamage() + 1);
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs cTab, List<ItemStack> list)
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

	// -- IItemEmc -- //

	@Override
	public double addEmc(@Nonnull ItemStack stack, double toAdd)
	{
		double add = Math.min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd);
		ItemPE.addEmcToStack(stack, add);
		return add;
	}

	@Override
	public double extractEmc(@Nonnull ItemStack stack, double toRemove)
	{
		double sub = Math.min(getStoredEmc(stack), toRemove);
		ItemPE.removeEmc(stack, sub);
		return sub;
	}

	@Override
	public double getStoredEmc(@Nonnull ItemStack stack)
	{
		return ItemPE.getEmc(stack);
	}

	@Override
	public double getMaximumEmc(@Nonnull ItemStack stack)
	{
		return EMCHelper.getKleinStarMaxEmc(stack);
	}
}
