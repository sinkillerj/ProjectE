package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.utils.Constants;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class AlchemicalFuel extends ItemPE
{
	private final String[] names = new String[] {"alchemical_coal", "mobius", "aeternalis"};
	
	public AlchemicalFuel()
	{
		this.setTranslationKey("fuel");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Nonnull
	@Override
	public String getTranslationKey(ItemStack stack)
	{	
		if (stack.getItemDamage() > 2)
		{
			return "pe.debug.metainvalid";
		}

		return super.getTranslationKey()+ "_" + names[MathHelper.clamp(stack.getItemDamage(), 0, 2)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		if (isInCreativeTab(cTab))
		{
			for (int i = 0; i < 3; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getItemBurnTime(ItemStack stack)
	{
		switch (stack.getItemDamage())
		{
			case 0:
				return Constants.ALCH_BURN_TIME;
			case 1:
				return Constants.MOBIUS_BURN_TIME;
			case 2:
				return Constants.AETERNALIS_BURN_TIME;
			default: return -1;
		}
	}
}
