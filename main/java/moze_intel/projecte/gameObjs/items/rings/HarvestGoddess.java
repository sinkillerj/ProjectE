package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class HarvestGoddess extends RingToggle
{
	public HarvestGoddess() 
	{
		super("harvest_god");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || par4 > 8 || !(entity instanceof EntityPlayer)) return;
		
		super.onUpdate(stack, world, entity, par4, par5);
		
		EntityPlayer player = (EntityPlayer) entity;
		
		if (stack.getItemDamage() != 0)
		{
			double storedEmc = this.getEmc(stack);
			
			if (storedEmc == 0 && !this.consumeFuel(player, stack, 64, true))
			{
				stack.setItemDamage(0);
			}
			else
			{
				Utils.growNearbyRandomly(true, world, player);
				this.removeEmc(stack, 0.32F);
			}
		}
		else
		{
			Utils.growNearbyRandomly(false, world, player);
		}
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if (world.isRemote || !player.canPlayerEdit(x, y, z, par7, stack))
			return false;
		
		Block block = world.getBlock(x, y, z);
		
		if (player.isSneaking())
		{
			Object[] obj = getStackFromInventory(player.inventory.mainInventory, Items.dye, 15, 4);

			if (obj == null) 
			{
				return false;
			}
			
			ItemStack boneMeal = (ItemStack) obj[1];

			if (boneMeal != null && useBoneMeal(world, x, y, z))
			{
				player.inventory.decrStackSize((Integer) obj[0], 4);
				player.inventoryContainer.detectAndSendChanges();
				return true;
			}
			
			return false;
		}
		
		if (block == Blocks.farmland)
		{
			plantSeeds(world, player, x, y, z);
			return true;
		}
		
		return false;
	}
	
	private boolean useBoneMeal(World world, int xCoord, int yCoord, int zCoord)
	{
		boolean result = false;
		
		for (int x = xCoord - 15; x <= xCoord + 15; x++)
			for (int z = zCoord - 15; z <= zCoord + 15; z++)
			{
				Block crop = world.getBlock(x, yCoord, z);
				
				if (crop == Blocks.double_plant) 
				{
					continue;
				}
				
				if (crop instanceof IGrowable)
				{
					IGrowable growable = (IGrowable) crop;
					
					if (growable.func_149852_a(world, world.rand, x, yCoord, z))
					{
						if (!result)
						{
							result = true;
						}
						
						growable.func_149853_b(world, world.rand, x, yCoord, z);
					}
				}
			}
		
		return result;
	}
	
	private void plantSeeds(World world, EntityPlayer player, int xCoord, int yCoord, int zCoord)
	{
		for (int x = xCoord - 8; x <= xCoord + 8; x++)
			for (int z = zCoord - 8; z <= zCoord + 8; z++)
			{
				Block block = player.worldObj.getBlock(x, yCoord, z);
				if (block == null || block == Blocks.air) continue;
				
				Object[] obj = getStackFromInventory(player.inventory.mainInventory, IPlantable.class);
				
				if (obj == null) 
				{
					return;
				}
				
				ItemStack stack = (ItemStack) obj[1];
				
				if (block.canSustainPlant(world, x, yCoord, z, ForgeDirection.UP, (IPlantable) stack.getItem()) && world.isAirBlock(x, yCoord + 1, z))
				{
					world.setBlock(x, yCoord + 1, z, ((IPlantable) stack.getItem()).getPlant(world, x, yCoord + 1, z));
					player.inventory.decrStackSize((Integer) obj[0], 1);
					player.inventoryContainer.detectAndSendChanges();
				}
			}
	}
	
	private Object[] getStackFromInventory(ItemStack[] inv, Class<?> type)
	{
		Object[] obj = new Object[2];
		for (int i = 0; i < inv.length;i++)
		{
			ItemStack stack = inv[i];
			if (stack != null && type.isInstance(stack.getItem()))
			{
				obj[0] = i;
				obj[1] = stack;
				return obj;
			}
		}
		return null;
	}
	
	private Object[] getStackFromInventory(ItemStack[] inv, Item item, int meta)
	{
		Object[] obj = new Object[2];
		for (int i = 0; i < inv.length;i++)
		{
			ItemStack stack = inv[i];
			if (stack != null && stack.getItem() == item && stack.getItemDamage() == meta)
			{
				obj[0] = i;
				obj[1] = stack;
				return obj;
			}
		}
		return null;
	}
	
	private Object[] getStackFromInventory(ItemStack[] inv, Item item, int meta, int minAmount)
	{
		Object[] obj = new Object[2];
		for (int i = 0; i < inv.length;i++)
		{
			ItemStack stack = inv[i];
			if (stack != null && stack.stackSize >= minAmount && stack.getItem() == item && stack.getItemDamage() == meta)
			{
				obj[0] = i;
				obj[1] = stack;
				return obj;
			}
		}
		return null;
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			if (this.getEmc(stack) == 0 && !this.consumeFuel(player, stack, 64, true))
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
}
