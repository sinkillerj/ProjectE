package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.MozeCore;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransmutationTablet extends ItemBase
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
			player.openGui(MozeCore.instance, Constants.TRANSMUTE_TABLET_GUI, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("transmute_tablet"));
	}
}
