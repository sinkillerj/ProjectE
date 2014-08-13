package moze_intel.gameObjs.items.rings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.gameObjs.entity.HomingArrow;
import moze_intel.gameObjs.items.ItemBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArchangelSmite extends ItemBase
{
	public ArchangelSmite()
	{
		this.setUnlocalizedName("archangel_smite");
		this.setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		HomingArrow arrow = new HomingArrow(world, player, 2.0F);
		if (!world.isRemote)
		{
			world.spawnEntityInWorld(arrow);
		}
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("rings", "archangel_smite"));
	}
}
