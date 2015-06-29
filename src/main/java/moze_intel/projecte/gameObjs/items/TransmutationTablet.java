package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TransmutationTablet extends ItemPE
{
	public TransmutationTablet()
	{
		this.setUnlocalizedName("transmutation_tablet");
		this.setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			player.openGui(PECore.instance, Constants.TRANSMUTATION_GUI, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		
		return stack;
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("transmute_tablet"));
	}
}
