package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;

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
		
		if (stack.getItemDamage() != 0)
		{
			double storedEmc = getEmc(stack);
			
			if (storedEmc == 0 && !consumeFuel(player, stack, 64, true))
			{
				stack.setItemDamage(0);
			}
			else
			{
				growNearbyRandomly(true, world, player);
				removeEmc(stack, 0.32F);
			}
		}
		else
		{
			growNearbyRandomly(false, world, player);
		}
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float par8, float par9, float par10)
	{
		if (world.isRemote || !player.canPlayerEdit(pos, facing, stack))
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

			if (boneMeal != null && useBoneMeal(world, pos))
			{
				player.inventory.decrStackSize((Integer) obj[0], 4);
				player.inventoryContainer.detectAndSendChanges();
				return true;
			}
			
			return false;
		}
		
		return plantSeeds(world, player, pos);
	}
	
	private boolean useBoneMeal(World world, BlockPos pos)
	{
		boolean result = false;
		
		for (int x = pos.getX() - 15; x <= pos.getX() + 15; x++)
			for (int z = pos.getZ() - 15; z <= pos.getZ() + 15; z++)
			{
				BlockPos currentPos = new BlockPos(x, pos.getY(), z);
				IBlockState state = world.getBlockState(currentPos);
				Block crop = state.getBlock();
				
				if (crop instanceof IGrowable)
				{
					IGrowable growable = (IGrowable) crop;
					
					if (growable.canUseBonemeal(world, world.rand, currentPos, state))
					{
						if (!result)
						{
							result = true;
						}
						
						growable.grow(world, world.rand, currentPos, state);
					}
				}
			}
		
		return result;
	}
	
	private boolean plantSeeds(World world, EntityPlayer player, BlockPos pos)
	{
		boolean result = false;
		
		List<StackWithSlot> seeds = getAllSeeds(player.inventory.mainInventory);
		
		if (seeds.isEmpty())
		{
			return false;
		}
		
		for (int x = pos.getX() - 8; x <= pos.getX() + 8; x++)
			for (int z = pos.getZ() - 8; z <= pos.getZ() + 8; z++)
			{
				BlockPos currentPos = new BlockPos(x, pos.getY(), z);
				IBlockState state = world.getBlockState(currentPos);
				
				if (world.isAirBlock(currentPos))
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
					
					if (state.getBlock().canSustainPlant(world, currentPos, EnumFacing.UP, plant) && world.isAirBlock(currentPos.up()))
					{
						world.setBlockState(currentPos.up(), plant.getPlant(world, currentPos.up()));
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

	private void growNearbyRandomly(boolean harvest, World world, BlockPos pos)
	{
		int chance = harvest ? 16 : 32;

		for (int x = pos.getX() - 5; x <= pos.getX() + 5; x++)
			for (int y = pos.getY() - 3; y <= pos.getY() + 3; y++)
				for (int z = pos.getZ() - 5; z <= pos.getZ() + 5; z++)
				{
					BlockPos currentPos = new BlockPos(x, y, z);
					IBlockState state = world.getBlockState(currentPos);
					Block crop = state.getBlock();

					// Vines, leaves, tallgrass, deadbush, doubleplants
					if (crop instanceof IShearable)
					{
						if (harvest)
						{
							world.destroyBlock(currentPos, true);
						}
					}
					// Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
					// Mushroom, potato, sapling, stems, tallgrass
					else if (crop instanceof IGrowable)
					{
						IGrowable growable = (IGrowable) crop;
						if(harvest && !growable.canGrow(world, currentPos, state, false))
						{
							world.destroyBlock(currentPos, true);
						}
						else if (world.rand.nextInt(chance) == 0)
						{
							if (ProjectEConfig.harvBandGrass || !crop.getUnlocalizedName().toLowerCase().contains("grass"))
							{
								growable.grow(world, world.rand, currentPos, state);
							}
						}
					}
					// All modded
					// Cactus, Reeds, Netherwart, Flower
					else if (crop instanceof IPlantable)
					{
						if (world.rand.nextInt(chance / 4) == 0)
						{
							for (int i = 0; i < (harvest ? 8 : 4); i++)
							{
								crop.updateTick(world, currentPos, state, world.rand);
							}
						}

						if (harvest)
						{
							if (crop instanceof BlockFlower)
							{
								world.destroyBlock(currentPos, true);
							}
							if (crop == Blocks.reeds || crop == Blocks.cactus)
							{
								boolean shouldHarvest = true;

								for (int i = 1; i < 3; i++)
								{
									if (world.getBlockState(currentPos.up()).getBlock() != crop)
									{
										shouldHarvest = false;
										break;
									}
								}

								if (shouldHarvest)
								{
									for (int i = crop == Blocks.reeds ? 1 : 0; i < 3; i++)
									{
										world.destroyBlock(pos.up(i), true);
									}
								}
							}
							if (crop == Blocks.nether_wart)
							{
								IBlockState wart = ((IPlantable) crop).getPlant(world, pos);
								if (((Integer) wart.getValue(BlockNetherWart.AGE)) == 3)
								{
									world.destroyBlock(currentPos, true);
								}
							}
						}
					}
				}
	}

	private void growNearbyRandomly(boolean harvest, World world, Entity player)
	{
		growNearbyRandomly(harvest, world, new BlockPos(player));
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
	public void updateInPedestal(World world, BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.harvestPedCooldown != -1)
		{
			if (harvestCooldown == 0)
			{
				growNearbyRandomly(true, world, pos);
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
