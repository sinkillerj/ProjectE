package moze_intel.projecte.gameObjs.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CovalenceDust extends ItemPE
{
	private final String[] names = new String[] {"low", "medium", "high"};
	
	public CovalenceDust()
	{
		this.setTranslationKey("covalence_dust");
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
}
