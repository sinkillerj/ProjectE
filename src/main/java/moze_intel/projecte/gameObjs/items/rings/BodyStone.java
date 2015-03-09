package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.PlayerTimers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class BodyStone extends RingToggle implements IBauble, IPedestalItem
{
	private int healCooldown = 5;
	public BodyStone() 
	{
		super("body_stone");
		this.setNoRepair();
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		if (world.isRemote || par4 > 8 || !(entity instanceof EntityPlayer)) 
		{
			return;
		}
		
		super.onUpdate(stack, world, entity, par4, par5);
		
		EntityPlayer player = (EntityPlayer) entity;
		
		if (stack.getItemDamage() != 0)
		{
			double itemEmc = getEmc(stack);
			
			if (itemEmc < 64 && !consumeFuel(player, stack, 64, false))
			{
				stack.setItemDamage(0);
			}
			else
			{
				PlayerTimers.activateFeed(player);

				if (player.getFoodStats().needFood() && PlayerTimers.canFeed(player))
				{
					player.getFoodStats().addStats(2, 10);
					removeEmc(stack, 64);
				}
			}
		}
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			if (getEmc(stack) < 64 && !consumeFuel(player, stack, 64, false))
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
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
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
		if (!world.isRemote)
		{
			if (healCooldown == 0)
			{
				DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
				List<EntityPlayerMP> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, tile.getEffectBounds());

				for (EntityPlayerMP player : players)
				{
					player.getFoodStats().addStats(1, 1); // 1/2 shank every 0.25 sec = 2 shank every second
				}

				healCooldown = 5;
			}
			else
			{
				healCooldown--;
			}
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<String>();
		list.add("Restores hunger");
		list.add("2 shanks / sec");
		return list;
	}
}
