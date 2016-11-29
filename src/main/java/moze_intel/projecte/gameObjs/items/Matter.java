package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.utils.AchievementHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

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

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs cTab, List<ItemStack> list)
	{
		for (int i = 0; i < 2; i++)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}
