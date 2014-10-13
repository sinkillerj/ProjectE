package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EvertideAmulet extends ItemBase implements IProjectileShooter, IBauble
{
	public EvertideAmulet()
	{
		this.setUnlocalizedName("evertide_amulet");
		this.setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (shootProjectile(player, stack))
			{
				PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			}
		}
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
	{
		if (invSlot > 8 || !(entity instanceof EntityPlayer)) 
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;

		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		
		if ((world.getBlock(x, y - 1, z) == Blocks.water || world.getBlock(x, y - 1, z) == Blocks.flowing_water) && world.getBlock(x, y, z) == Blocks.air)
		{
			if (!player.isSneaking())
			{
				player.motionY = 0.0D;
				player.fallDistance = 0.0F;
				player.onGround = true;
			}
				
			if (!world.isRemote && player.capabilities.getWalkSpeed() < 0.25F)
			{
				Utils.setPlayerWalkSpeed(player, 0.25F);
			}
		}
		else if (!world.isRemote)
		{
			if (player.isInWater())
			{
				player.setAir(300);
			}
				
			if (player.capabilities.getWalkSpeed() != Constants.PLAYER_WALK_SPEED)
			{
				Utils.setPlayerWalkSpeed(player, Constants.PLAYER_WALK_SPEED);
			}
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		World world = player.worldObj;
		if (!world.provider.isHellWorld)
		{
			world.spawnEntityInWorld(new EntityWaterProjectile(world, player));
			world.playSoundAtEntity(player, "projecte:waterball", 0.6F, 1.0F);
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("rings", "evertide_amulet"));//"ee2:rings/evertide_amulet");
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public void onWornTick(ItemStack stack, World world, Entity entity, EntityLivingBase player) {}
}
