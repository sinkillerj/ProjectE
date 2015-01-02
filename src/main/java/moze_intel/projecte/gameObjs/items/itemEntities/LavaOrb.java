package moze_intel.projecte.gameObjs.items.itemEntities;

import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LavaOrb extends ItemPE
{
	public LavaOrb()
	{
		this.setUnlocalizedName("lava_orb");
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("entities", "lava_orb"));
	}
}
