package moze_intel.projecte.gameObjs.items.itemEntities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.client.renderer.texture.IIconRegister;

public class RandomizerProjectile extends ItemPE
{
	public RandomizerProjectile()
	{
		this.setCreativeTab(null);
		this.setUnlocalizedName("randomizer");
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("entities", "randomizer"));
	}
}
