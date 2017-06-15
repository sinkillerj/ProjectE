package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class Matter extends ItemPE 
{
	private final String[] names = new String[] {"dark", "red"};
	
	public Matter()
	{
		this.setUnlocalizedName("matter");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{	
		return super.getUnlocalizedName() + "_" + names[stack.getItemDamage()];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		if (isInCreativeTab(cTab))
		{
			for (int i = 0; i < 2; i++)
			{
				list.add(new ItemStack(this, 1, i));
			}
		}
	}
}
