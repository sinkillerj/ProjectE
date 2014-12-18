package sinkillerj.projecte.gameObjs.items.itemEntities;

import sinkillerj.projecte.gameObjs.items.ItemPE;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LootBallItem extends ItemPE
{
	public LootBallItem()
	{
		this.setUnlocalizedName("loot_ball");
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("entities", "loot_ball"));
	}
}
