package moze_intel.projecte.gameObjs.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class CovalenceDust extends ItemPE
{
	private final String[] names = new String[] {"low", "medium", "high"};
	
	public CovalenceDust()
	{
		this.setUnlocalizedName("covalence_dust");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Nonnull
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
	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs cTab, List<ItemStack> list)
	{
		for (int i = 0; i < 3; ++i)
			list.add(new ItemStack(item, 1, i));
	}
}
