package sinkillerj.projecte.gameObjs.items;

import sinkillerj.projecte.api.IProjectileShooter;
import sinkillerj.projecte.gameObjs.entity.EntityLensProjectile;
import sinkillerj.projecte.network.PacketHandler;
import sinkillerj.projecte.network.packets.SwingItemPKT;
import sinkillerj.projecte.utils.Constants;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HyperkineticLens extends ItemCharge implements IProjectileShooter
{
	public HyperkineticLens() 
	{
		super("hyperkinetic_lens", (byte) 4);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) return stack;
		
		if (shootProjectile(player, stack))
		{
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
		
		return stack;
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		World world = player.worldObj;
		int requiredEmc = Constants.EXPLOSIVE_LENS_COST[this.getCharge(stack)];
		
		if (!this.consumeFuel(player, stack, requiredEmc, true))
		{
			return false;
		}
		
		world.spawnEntityInWorld(new EntityLensProjectile(world, player, this.getCharge(stack)));
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("hyper_lens"));
	}
}
