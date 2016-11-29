package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TransmutationTablet extends ItemPE
{
	public TransmutationTablet()
	{
		this.setUnlocalizedName("transmutation_tablet");
		this.setMaxStackSize(1);
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote)
		{
			player.openGui(PECore.instance, Constants.TRANSMUTATION_GUI, world, hand == EnumHand.MAIN_HAND ? 0 : 1, -1, -1);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.PORTABLE_TRANSMUTATION, 1);
		}
	}
}
