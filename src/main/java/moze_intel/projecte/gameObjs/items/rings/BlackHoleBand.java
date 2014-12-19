package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class BlackHoleBand extends RingToggle implements IBauble
{
	public BlackHoleBand()
	{
		super("black_hole");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			changeMode(player, stack);
		}
		
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || stack.getItemDamage() != 1 || !(entity instanceof EntityPlayer)) 
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		AxisAlignedBB bBox = player.boundingBox.expand(7, 7, 7);
		List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
		
		for (EntityItem item : itemList)
		{
			if (Utils.hasSpace(player.inventory.mainInventory, item.getEntityItem())) 
			{
				item.delayBeforeCanPickup = 0;
				double d1 = (player.posX - item.posX);
				double d2 = (player.posY + (double)player.getEyeHeight() - item.posY);
				double d3 = (player.posZ - item.posZ);
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

				item.motionX += d1 / d4 * 0.1D;
				item.motionY += d2 / d4 * 0.1D;
				item.motionZ += d3 / d4 * 0.1D;
				
				item.moveEntity(item.motionX, item.motionY, item.motionZ);
			}
		}
		
		List<EntityLootBall> ballList = world.getEntitiesWithinAABB(EntityLootBall.class, bBox);
		
		for (EntityLootBall ball : ballList)
		{
			double d1 = (player.posX - ball.posX);
			double d2 = (player.posY + (double)player.getEyeHeight() - ball.posY);
			double d3 = (player.posZ - ball.posZ);
			double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

			ball.motionX += d1 / d4 * 0.1D;
			ball.motionY += d2 / d4 * 0.1D;
			ball.motionZ += d3 / d4 * 0.1D;
			
			ball.moveEntity(ball.motionX, ball.motionY, ball.motionZ);
		}
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}
}
