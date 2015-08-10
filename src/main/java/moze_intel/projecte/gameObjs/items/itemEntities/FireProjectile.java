package moze_intel.projecte.gameObjs.items.itemEntities;

import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.client.renderer.texture.IIconRegister;

public class FireProjectile extends ItemPE
{
	public FireProjectile()
	{
		setCreativeTab(null);
		setUnlocalizedName("fire_projectile");
		setMaxStackSize(1);
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		itemIcon = register.registerIcon(getTexture("entities", "fireball"));
	}
}
