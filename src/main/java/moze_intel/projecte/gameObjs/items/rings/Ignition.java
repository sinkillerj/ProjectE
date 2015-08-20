package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class Ignition extends RingToggle implements IBauble, IPedestalItem, IFireProtector, IProjectileShooter
{
	public Ignition()
	{
		super("ignition");
		this.setNoRepair();
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int inventorySlot, boolean par5) 
	{
		if (world.isRemote || inventorySlot > 8 || !(entity instanceof EntityPlayer)) return;
		
		super.onUpdate(stack, world, entity, inventorySlot, par5);
		EntityPlayerMP player = (EntityPlayerMP)entity;

		if (stack.getItemDamage() != 0)
		{
			if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
			{
				stack.setItemDamage(0);
			}
			else 
			{
				WorldHelper.igniteNearby(world, player);
				removeEmc(stack, 0.32F);
			}
		}
		else 
		{
			WorldHelper.extinguishNearby(world, player);
		}
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		
		if (stack.getItemDamage() == 0)
		{
			if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
			{
				//NOOP (used to be sounds)
			}
			else
			{
				stack.setItemDamage(1);
			}
		}
		else
		{
			stack.setItemDamage(0);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, false);
			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				if (world.getBlock(mop.blockX, mop.blockY, mop.blockZ) instanceof BlockTNT
						&& PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), mop.blockX, mop.blockY, mop.blockZ))
				{
					// Ignite TNT or derivatives
					((BlockTNT) world.getBlock(mop.blockX, mop.blockY, mop.blockZ)).func_150114_a(world, mop.blockX, mop.blockY, mop.blockZ, 1, player);
					world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
				}
			}
			world.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 1.0F);
		}
		return stack;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
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

	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		if (!world.isRemote && ProjectEConfig.ignitePedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
			if (tile.getActivityCooldown() == 0)
			{
				List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, tile.getEffectBounds());
				for (EntityLiving living : list)
				{
					living.attackEntityFrom(DamageSource.inFire, 3.0F);
					living.setFire(8);
				}

				tile.setActivityCooldown(ProjectEConfig.ignitePedCooldown);
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.ignitePedCooldown != -1)
		{
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.ignition.pedestal1"));
			list.add(EnumChatFormatting.BLUE + String.format(
					StatCollector.translateToLocal("pe.ignition.pedestal2"), MathUtils.tickToSecFormatted(ProjectEConfig.ignitePedCooldown)));
		}
		return list;
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack)
	{
		World world = player.worldObj;
		
		if(world.isRemote) return false;
		
		EntityFireProjectile fire = new EntityFireProjectile(world, player);
		world.spawnEntityInWorld(fire);
		
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}
}
