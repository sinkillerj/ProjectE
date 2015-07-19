package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.api.tooltip.keybinds.ITTCharge;
import moze_intel.projecte.api.tooltip.keybinds.ITTProjectile;
import moze_intel.projecte.api.tooltip.keybinds.ITTRightClick;
import moze_intel.projecte.api.tooltip.special.ITTConsumesEMC;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HyperkineticLens extends ItemCharge implements ITTProjectile, ITTConsumesEMC, ITTCharge, ITTRightClick
{
	public HyperkineticLens() 
	{
		super("hyperkinetic_lens", (byte)3);
		this.setNoRepair();
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) return stack;
		
		if (shootProjectile(player, stack))
		{
			PlayerHelper.swingItem(player);
		}
		
		return stack;
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
		this.itemIcon = register.registerIcon(this.getTexture("hyper_lens"));
	}

	@Override
	public String getTooltipLocalisationPrefix()
	{
		return "pe.hyperkineticlens";
	}
}
