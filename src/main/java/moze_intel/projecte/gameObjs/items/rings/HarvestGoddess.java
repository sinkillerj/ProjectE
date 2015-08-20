package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class HarvestGoddess extends RingToggle implements IPedestalItem
{
	public HarvestGoddess()
	{
		super("harvest_god");
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
			double storedEmc = getEmc(stack);
			
			if (storedEmc == 0 && !consumeFuel(player, stack, 64, true))
			{
				stack.setItemDamage(0);
			}
			else
			{
				WorldHelper.growNearbyRandomly(true, world, player.posX, player.posY, player.posZ, player);
				removeEmc(stack, 0.32F);
			}
		}
		else
		{
			WorldHelper.growNearbyRandomly(false, world, player.posX, player.posY, player.posZ, player);
		}
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if (world.isRemote || !player.canPlayerEdit(x, y, z, par7, stack))
		{
			return false;
		}
		
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
		
		return plantSeeds(world, player, x, y, z);
	}
	
	private boolean useBoneMeal(World world, int xCoord, int yCoord, int zCoord)
	{
		boolean result = false;
		
		for (int x = xCoord - 15; x <= xCoord + 15; x++)
			for (int z = zCoord - 15; z <= zCoord + 15; z++)
			{
				Block crop = world.getBlock(x, yCoord, z);
				
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
	
	private boolean plantSeeds(World world, EntityPlayer player, int xCoord, int yCoord, int zCoord)
	{
		boolean result = false;
		
		List<StackWithSlot> seeds = getAllSeeds(player.inventory.mainInventory);
		
		if (seeds.isEmpty())
		{
			return false;
		}
		
		for (int x = xCoord - 8; x <= xCoord + 8; x++)
			for (int z = zCoord - 8; z <= zCoord + 8; z++)
			{
				Block block = player.worldObj.getBlock(x, yCoord, z);
				
				if (block == null || block == Blocks.air) 
				{
					continue;
				}
				
				for (int i = 0; i < seeds.size(); i++)
				{
					StackWithSlot s = seeds.get(i);
					IPlantable plant;
					
					if (s.stack.getItem() instanceof IPlantable)
					{
						plant = (IPlantable) s.stack.getItem();
					}
					else
					{
						plant = (IPlantable) Block.getBlockFromItem(s.stack.getItem());
					}
					
					if (block.canSustainPlant(world, x, yCoord, z, ForgeDirection.UP, plant) && world.isAirBlock(x, yCoord + 1, z))
					{
						world.setBlock(x, yCoord + 1, z, plant.getPlant(world, x, yCoord + 1, z));
						player.inventory.decrStackSize(s.slot, 1);
						player.inventoryContainer.detectAndSendChanges();
						
						s.stack.stackSize--;
						
						if (s.stack.stackSize <= 0)
						{
							seeds.remove(i);
						}
						
						if (!result)
						{
							result = true;
						}
					}
				}
			}
		
		return result;
	}
	
	private List<StackWithSlot> getAllSeeds(ItemStack[] inv) 
	{
		List<StackWithSlot> result = Lists.newArrayList();
		
		for (int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];
			
			if (stack != null)
			{
				if (stack.getItem() instanceof IPlantable)
				{
					result.add(new StackWithSlot(stack, i));
					continue;
				}
				
				Block block = Block.getBlockFromItem(stack.getItem());
				
				if (block != null && block instanceof IPlantable)
				{
					result.add(new StackWithSlot(stack, i));
				}
			}
		}
		
		return result;
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
			if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, true))
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
	public void updateInPedestal(World world, int x, int y, int z)
	{
		if (!world.isRemote && ProjectEConfig.harvestPedCooldown != -1)
		{
			DMPedestalTile tile = (DMPedestalTile) world.getTileEntity(x, y, z);
			if (tile.getActivityCooldown() == 0)
			{
				WorldHelper.growNearbyRandomly(true, world, x, y, z, null);
				tile.setActivityCooldown(ProjectEConfig.harvestPedCooldown);
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
		if (ProjectEConfig.harvestPedCooldown != -1)
		{
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.harvestgod.pedestal1"));
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.harvestgod.pedestal2"));
			list.add(EnumChatFormatting.BLUE + String.format(
					StatCollector.translateToLocal("pe.harvestgod.pedestal3"), MathUtils.tickToSecFormatted(ProjectEConfig.harvestPedCooldown)));
		}
		return list;
	}

	private class StackWithSlot
	{
		public final int slot;
		public final ItemStack stack;
		
		public StackWithSlot(ItemStack stack, int slot) 
		{
			this.stack = stack.copy();
			this.slot = slot;
		}
	}
}
