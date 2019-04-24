package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class KleinStar extends ItemPE implements IItemEmc
{
	public KleinStar()
	{
		this.setTranslationKey("klein_star");
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
		long starEmc = getEmc(stack);
		
		if (starEmc == 0)
		{
			return 1.0D;
		}
		
		return 1.0D - starEmc / (double) EMCHelper.getKleinStarMaxEmc(stack);
	}

	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote && PECore.DEV_ENVIRONMENT)
		{
			setEmc(stack, EMCHelper.getKleinStarMaxEmc(stack));
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		
		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}
	
	@Nonnull
	@Override
	public String getTranslationKey(ItemStack stack)
	{
		if (stack.getItemDamage() > 5)
		{
			return "pe.debug.metainvalid";
		}

		return super.getTranslationKey()+ "_" + (stack.getItemDamage() + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		if (isInCreativeTab(cTab))
		{
			for (int i = 0; i < 6; ++i)
			{
				list.add(new ItemStack(this, 1, i));
			}
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
	public long addEmc(@Nonnull ItemStack stack, long toAdd)
	{
		long add = Math.min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd);
		ItemPE.addEmcToStack(stack, add);
		return add;
	}

	@Override
	public long extractEmc(@Nonnull ItemStack stack, long toRemove)
	{
		long sub = Math.min(getStoredEmc(stack), toRemove);
		ItemPE.removeEmc(stack, sub);
		return sub;
	}

	@Override
	public long getStoredEmc(@Nonnull ItemStack stack)
	{
		return ItemPE.getEmc(stack);
	}

	@Override
	public long getMaximumEmc(@Nonnull ItemStack stack)
	{
		return EMCHelper.getKleinStarMaxEmc(stack);
	}
}
