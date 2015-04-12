package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
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
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class HarvestGoddess extends RingToggle implements IPedestalItem
{
	private int harvestCooldown;

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
		
		if (stack.getMetadata() != 0)
		{
			double storedEmc = this.getEmc(stack);
			
			if (storedEmc == 0 && !this.consumeFuel(player, stack, 64, true))
			{
				stack.setMetadata(0);
			}
			else
			{
				growNearbyRandomly(true, world, player);
				this.removeEmc(stack, 0.32F);
			}
		}
		else
		{
			growNearbyRandomly(false, world, player);
		}
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if (world.isRemote || !player.canPlayerEdit(x, y, z, par7, stack))
		{
			return false;
		}
		
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
					
					if (growable.shouldFertilize(world, world.rand, x, yCoord, z))
					{
						if (!result)
						{
							result = true;
						}
						
						growable.fertilize(world, world.rand, x, yCoord, z);
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
			
			if (stack != null && stack.getItem() == item && stack.getMetadata() == meta)
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
			
			if (stack != null && stack.stackSize >= minAmount && stack.getItem() == item && stack.getMetadata() == meta)
			{
				obj[0] = i;
				obj[1] = stack;
				return obj;
			}
		}
		
		return null;
	}

	private void growNearbyRandomly(boolean harvest, World world, double xCoord, double yCoord, double zCoord)
	{
		int chance = harvest ? 16 : 32;

		for (int x = (int) (xCoord - 5); x <= xCoord + 5; x++)
			for (int y = (int) (yCoord - 3); y <= yCoord + 3; y++)
				for (int z = (int) (zCoord - 5); z <= zCoord + 5; z++)
				{
					Block crop = world.getBlock(x, y, z);

					if (crop instanceof BlockGrass)
					{
						continue;
					}

					if (crop instanceof IShearable)
					{
						if (harvest)
						{
							world.breakBlock(x, y, z, true);
						}
					}
					else if (crop instanceof IGrowable)
					{
						IGrowable growable = (IGrowable) crop;

						if(harvest && !growable.canFertilize(world, x, y, z, false))
						{
							world.breakBlock(x, y, z, true);
						}
						else if (world.rand.nextInt(chance) == 0)
						{
							growable.fertilize(world, world.rand, x, y, z);
						}
					}
					else if (crop instanceof IPlantable)
					{
						if (world.rand.nextInt(chance / 4) == 0)
						{
							for (int i = 0; i < (harvest ? 8 : 4); i++)
							{
								crop.updateTick(world, x, y, z, world.rand);
							}
						}

						if (harvest)
						{
							if (crop == Blocks.reeds || crop == Blocks.cactus)
							{
								boolean shouldHarvest = true;

								for (int i = 1; i < 3; i++)
								{
									if (world.getBlock(x, y + i, z) != crop)
									{
										shouldHarvest = false;
										break;
									}
								}

								if (shouldHarvest)
								{
									for (int i = crop == Blocks.reeds ? 1 : 0; i < 3; i++)
									{
										world.breakBlock(x, y + i, z, true);
									}
								}
							}
						}
					}
				}
	}

	private void growNearbyRandomly(boolean harvest, World world, Entity player)
	{
		growNearbyRandomly(harvest, world, player.posX, player.posY, player.posZ);
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (stack.getMetadata() == 0)
		{
			if (this.getEmc(stack) == 0 && !this.consumeFuel(player, stack, 64, true))
			{
				//NOOP (used to be sounds)
			}
			else
			{
				stack.setMetadata(1);
			}
		}
		else
		{
			stack.setMetadata(0);
		}
	}

	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		if (!world.isRemote && ProjectEConfig.harvestPedCooldown != -1)
		{
			if (harvestCooldown == 0)
			{
				growNearbyRandomly(true, world, x, y, z);
				harvestCooldown = ProjectEConfig.harvestPedCooldown;
			}
			else
			{
				harvestCooldown--;
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
