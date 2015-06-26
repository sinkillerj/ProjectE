package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CataliticLens extends DestructionCatalyst implements IProjectileShooter
{
	public CataliticLens() 
	{
		super("catalitic_lens", (byte)7);
		this.setNoRepair();
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack)
	{
		World world = player.worldObj;
		int requiredEmc = Constants.EXPLOSIVE_LENS_COST[this.getCharge(stack)];
		
		if (!consumeFuel(player, stack, requiredEmc, true))
		{
			return false;
		}

		world.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 1.0F);
		world.spawnEntityInWorld(new EntityLensProjectile(world, player, this.getCharge(stack)));
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("catalitic_lens"));
	}
}
