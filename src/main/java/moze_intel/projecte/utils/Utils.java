package moze_intel.projecte.utils;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SetFlyPKT;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.OreDictionary;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public final class Utils 
{	
	private static List<Class> peacefuls = new ArrayList<Class>();
	private static List<Class> mobs = new ArrayList<Class>();
	
	public static void init()
	{
		WorldTransmutations.init();
		loadEntityLists();
	}
	
	public static boolean doesItemHaveEmc(ItemStack stack)
	{
		if (stack == null) 
		{
			return false;
		}
		
		SimpleStack iStack = new SimpleStack(stack);

		if (!iStack.isValid())
		{
			return false;
		}

		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0)
		{
			iStack.damage = 0;
		}

		return EMCMapper.mapContains(iStack);
	}

	public static boolean doesItemHaveEmc(Item item)
	{
		if (item == null)
		{
			return false;
		}

		return doesItemHaveEmc(new ItemStack(item));
	}

	public static boolean doesBlockHaveEmc(Block block)
	{
		if (block == null)
		{
			return false;
		}

		return doesItemHaveEmc(new ItemStack(block));
	}

	public static int getEmcValue(Item item)
	{
		SimpleStack stack = new SimpleStack(new ItemStack(item));

		if (stack.isValid() && EMCMapper.mapContains(stack))
		{
			return EMCMapper.getEmcValue(stack);
		}

		return 0;
	}

	public static int getEmcValue(Block Block)
	{
		SimpleStack stack = new SimpleStack(new ItemStack(Block));

		if (stack.isValid() && EMCMapper.mapContains(stack))
		{
			return EMCMapper.getEmcValue(stack);
		}

		return 0;
	}

	public static int getEmcValue(ItemStack stack)
	{
		if (stack == null) 
		{
			return 0;
		}
		
		SimpleStack iStack = new SimpleStack(stack);

		if (!iStack.isValid())
		{
			return 0;
		}

		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0)
		{
			iStack.damage = 0;
			
			if (EMCMapper.mapContains(iStack))
			{
				int emc = EMCMapper.getEmcValue(iStack);
				
				int relDamage = (stack.getMaxDamage() - stack.getItemDamage());

				if (relDamage <= 0)
				{
					//Impossible?
					return 0;
				}

				long result = emc * relDamage;

				if (result <= 0)
				{
					//Congratulations, big number is big.
					return emc;
				}

				result /= stack.getMaxDamage();
				result += getEnchantEmcBonus(stack);

				if (result > Integer.MAX_VALUE)
				{
					return emc;
				}

				if (result <= 0)
				{
					return 1;
				}

				return (int) result;
			}
		}
		else
		{
			if (EMCMapper.mapContains(iStack))
			{
				return EMCMapper.getEmcValue(iStack) + getEnchantEmcBonus(stack);
			}
		}
			
		return 0;
	}
	
	public static int getEnchantEmcBonus(ItemStack stack)
	{
		int result = 0;
		
		Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
		
		if (!enchants.isEmpty())
		{
			for (Entry<Integer, Integer> entry : enchants.entrySet())
			{
				Enchantment ench = Enchantment.enchantmentsList[entry.getKey()];
				
				if (ench.getWeight() == 0)
				{
					continue;
				}
				
				result += Constants.ENCH_EMC_BONUS / ench.getWeight() * entry.getValue();
			}
		}
		
		return result;
	}
	
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return ItemStack.areItemStacksEqual(getNormalizedStack(stack1), getNormalizedStack(stack2));
	}
	
	public static boolean areItemStacksEqualIgnoreNBT(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.getItem() != stack2.getItem())
		{
			return false;
		}
		

		if (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}

		return stack1.getItemDamage() == stack2.getItemDamage();
	}
	
	
	public static boolean basicAreStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return (stack1.getItem() == stack2.getItem()) && (stack1.getItemDamage() == stack2.getItemDamage());
	}
	
	public static boolean ContainsItemStack(List<ItemStack> list, ItemStack toSearch)
	{
		Iterator<ItemStack> iter = list.iterator();

		while (iter.hasNext())
		{
			ItemStack stack = iter.next();

			if (stack == null)
			{
				continue;
			}

			if (stack.getItem().equals(toSearch.getItem()))
			{
				if( !stack.getHasSubtypes() || stack.getItemDamage() == toSearch.getItemDamage())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean containsItemStack(ItemStack[] stacks, ItemStack toSearch)
	{
		for (ItemStack stack : stacks)
		{
			if (stack == null)
			{
				continue;
			}

			if (stack.getItem() == toSearch.getItem())
			{
				if (!stack.getHasSubtypes() || stack.getItemDamage() == toSearch.getItemDamage())
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean invContainsItem(IInventory inv, ItemStack toSearch)
	{
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			
			if (stack != null && basicAreStacksEqual(stack, toSearch))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean invContainsItem(ItemStack inv[], ItemStack toSearch)
	{
		for (ItemStack stack : inv)
		{
			if (stack != null && basicAreStacksEqual(stack, toSearch))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean invContainsItem(ItemStack inv[], Item toSearch)
	{
		for (ItemStack stack : inv)
		{
			if (stack != null && stack.getItem() == toSearch)
			{
				return true;
			}
		}
		return false;
	}
	
	public static int getKleinStarMaxEmc(ItemStack stack)
	{
		return Constants.MAX_KLEIN_EMC[stack.getItemDamage()];
	}
	
	public static boolean hasSpace(IInventory inv, ItemStack stack)
	{
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);
			
			if (invStack == null) 
			{
				return true;
			}
			
			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hasSpace(ItemStack[] inv, ItemStack stack)
	{
		for (ItemStack invStack : inv)
		{
			if (invStack == null) 
			{
				return true;
			}
			
			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 *	Returns an itemstack if the stack passed could not entirely fit in the inventory, otherwise returns null. 
	 */
	public static ItemStack pushStackInInv(IInventory inv, ItemStack stack)
	{
		int limit;
		
		if (inv instanceof InventoryPlayer)
		{
			limit = 36;
		}
		else
		{
			limit = inv.getSizeInventory();
		}
		
		for (int i = 0; i < limit; i++)
		{
			ItemStack invStack = inv.getStackInSlot(i);
			
			if (invStack == null)
			{
				inv.setInventorySlotContents(i, stack);
				return null;
			}
			
			if (Utils.areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				int remaining = invStack.getMaxStackSize() - invStack.stackSize;
				
				if (remaining >= stack.stackSize)
				{
					invStack.stackSize += stack.stackSize;
					inv.setInventorySlotContents(i, invStack);
					return null;
				}
				
				invStack.stackSize += remaining;
				inv.setInventorySlotContents(i, invStack);
				stack.stackSize -= remaining;
			}
		}
		
		return stack.copy();
	}
	
	/**
	 *	Returns an itemstack if the stack passed could not entirely fit in the inventory, otherwise returns null. 
	 */
	public static ItemStack pushStackInInv(ItemStack[] inv, ItemStack stack)
	{
		for (int i = 0; i < inv.length; i++)
		{
			ItemStack invStack = inv[i];
			
			if (invStack == null)
			{
				inv[i] = stack;
				return null;
			}
			
			if (Utils.areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				int remaining = invStack.getMaxStackSize() - invStack.stackSize;
				
				if (remaining >= stack.stackSize)
				{
					invStack.stackSize += stack.stackSize;
					inv[i] = invStack;
					return null;
				}
				
				invStack.stackSize += remaining;
				inv[i] = invStack;
				stack.stackSize -= remaining;
			}
		}
		
		return stack.copy();
	}
	
	public static void spawnEntityItem(World world, ItemStack stack, int x, int y, int z)
	{
		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		EntityItem entityitem;
		
		for (float f2 = world.rand.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world.spawnEntityInWorld(entityitem))
		{
			int j1 = world.rand.nextInt(21) + 10;

			if (j1 > stack.stackSize)
				j1 = stack.stackSize;

			stack.stackSize -= j1;
			entityitem = new EntityItem(world, (double)((float) x + f), (double)((float) y + f1), (double)((float) z + f2), new ItemStack(stack.getItem(), j1, stack.getItemDamage()));
			float f3 = 0.05F;
			entityitem.motionX = (double)((float) world.rand.nextGaussian() * f3);
			entityitem.motionY = (double)((float) world.rand.nextGaussian() * f3 + 0.2F);
			entityitem.motionZ = (double)((float) world.rand.nextGaussian() * f3);

			if (stack.hasTagCompound())
			{
				entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
			}
		}	
		
	}
	
	public static void setPlayerFireImmunity(EntityPlayer player, boolean flag)
	{
		Class c = Entity.class;
		Field field = c.getDeclaredFields()[52];
		field.setAccessible(true);

		try 
		{
			field.setBoolean(player, flag);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void setPlayerWalkSpeed(EntityPlayer player, float value)
	{
		Class c = PlayerCapabilities.class;
		Field field = c.getDeclaredFields()[6];
		field.setAccessible(true);

		try 
		{
			field.setFloat(player.capabilities, value);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static Entity getRandomEntity(World world, Entity toRandomize)
	{
		Class entClass = toRandomize.getClass();
		
		if (peacefuls.contains(entClass))
		{
			return getNewEntityInstance((Class) getRandomListEntry(peacefuls, entClass), world);
		}
		else if (mobs.contains(entClass))
		{
			return getNewEntityInstance((Class) getRandomListEntry(mobs, entClass), world);
		}
		else if (world.rand.nextInt(2) == 0)
		{
			return new EntitySlime(world);
		}
		else 
		{
			return new EntitySheep(world);
		}
	}
	
	public static Object getRandomListEntry(List<?> list, Object toExclude)
	{
		Object obj;
		
		do
		{
			int random = randomIntInRange(list.size() - 1, 0);
			obj = list.get(random);
		}
		while(obj.equals(toExclude));
		
		return obj;
	}
	
	public static Entity getNewEntityInstance(Class c, World world)
	{
		try 
		{
			Constructor constr = c.getConstructor(World.class);
			Entity ent = (Entity) constr.newInstance(world);
			
			if (ent instanceof EntitySkeleton)
			{
				if (world.rand.nextInt(2) == 0)
				{
					((EntitySkeleton) ent).setSkeletonType(1);
					ent.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
				}
				else 
				{
					ent.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
				}
			}
			else if (ent instanceof EntityPigZombie)
			{
				ent.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
			}
			
			return ent;
		}
		catch (Exception e)
		{
			PELogger.logFatal("Could not create new entity instance for: "+c.getCanonicalName());
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static double consumePlayerFuel(EntityPlayer player, double minFuel)
	{
		if (player.capabilities.isCreativeMode)
		{
			return minFuel;
		}

		IInventory inv = player.inventory;
		LinkedHashMap<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
		boolean metRequirement = false;
		int decrement = 0;
		int emcConsumed = 0;
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			
			if (stack == null)
			{
				continue;
			}
			else if (stack.getItem() == ObjHandler.kleinStars)
			{
				if (ItemPE.getEmc(stack) >= minFuel)
				{
					ItemPE.removeEmc(stack, minFuel);
					player.inventoryContainer.detectAndSendChanges();
					return minFuel;
				}
			}
			else if (!metRequirement)
			{
				if(FuelMapper.isStackFuel(stack))
				{
					int emc = Utils.getEmcValue(stack);
					int toRemove = ((int) Math.ceil((minFuel - emcConsumed) / (float) emc));
					
					if (stack.stackSize >= toRemove)
					{
						map.put(i, toRemove);
						emcConsumed += emc * toRemove;
						metRequirement = true;
					}
					else
					{
						map.put(i, stack.stackSize);
						emcConsumed += emc * stack.stackSize;
						
						if (emcConsumed >= minFuel)
						{
							metRequirement = true;
						}
					}
		
				}
			}
		}
		
		if (metRequirement)
		{
			for (Entry<Integer, Integer> entry : map.entrySet())
			{
				inv.decrStackSize(entry.getKey(), entry.getValue());
			}
			
			player.inventoryContainer.detectAndSendChanges();
			return emcConsumed;
		}

		return -1;
	}
	
	/**
	 * Returns an ItemStack with stacksize 1.
	 */
	public static ItemStack getNormalizedStack(ItemStack stack)
	{
		ItemStack result = stack.copy();
		result.stackSize = 1;
		return result;
	}
	
	public static List<TileEntity> getTileEntitiesWithinAABB(World world, AxisAlignedBB bBox)
	{
		List<TileEntity> list = new ArrayList<TileEntity>();

		for (int i = (int) bBox.minX; i <= bBox.maxX; i++)
			for (int j = (int) bBox.minY; j <= bBox.maxY; j++)
				for (int k = (int) bBox.minZ; k <= bBox.maxZ; k++)
				{
					TileEntity tile = world.getTileEntity(i, j, k);
					if (tile != null)
					{
						list.add(tile);
					}
				}

		return list;
	}
	
	public static void setPlayerFlight(EntityPlayerMP player, boolean state)
	{
		PacketHandler.sendTo(new SetFlyPKT(state), player);
		player.capabilities.allowFlying = state;
		
		if (!state)
		{
			player.capabilities.isFlying = false;
		}
	}
	
	public static String getOreDictionaryName(ItemStack stack)
	{
		int[] oreIds = OreDictionary.getOreIDs(stack);
		
		if (oreIds.length == 0)
		{
			return "Unknown";
		}
		
		return OreDictionary.getOreName(oreIds[0]);
	}
	
	/**
	 * Get a List of itemstacks from an OD name.<br>
	 * It also makes sure that no items with damage 32767 are included, to prevent errors. 
	 */
	public static List<ItemStack> getODItems(String oreName)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();
		
		for (ItemStack stack : OreDictionary.getOres(oreName))
		{
			if (stack == null)
			{
				continue;
			}
			
			if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
			{
				List<ItemStack> list = new ArrayList<ItemStack>();
				
				ItemStack copy = stack.copy();
				copy.setItemDamage(0);
				
				list.add(copy.copy());
				
				String startName = copy.getUnlocalizedName();
				
				for (int i = 1; i <= 128; i++)
				{
					try
					{
						copy.setItemDamage(i);
					
						if (copy.getUnlocalizedName() == null || copy.getUnlocalizedName().equals(startName))
						{
							result.addAll(list);
							break;
						}
					}
					catch (Exception e)
					{
						PELogger.logFatal("Couldn't retrieve OD items for: " + oreName);
						PELogger.logFatal("Caused by: " + e.toString());

						result.addAll(list);
						break;
					}
					
					list.add(copy.copy());
					
					if (i == 128)
					{
						copy.setItemDamage(0);
						result.add(copy);
					}
				}
			}
			else
			{
				result.add(stack.copy());
			}
		}
		
		return result;
	}
	
	public static void harvestVein(World world, EntityPlayer player, ItemStack stack, Coordinates coords, Block target, List<ItemStack> currentDrops, int numMined)
	{
		if (numMined >= Constants.MAX_VEIN_SIZE)
		{
			return;
		}
		
		CoordinateBox b = new CoordinateBox(coords.x - 1, coords.y - 1, coords.z - 1, coords.x + 1, coords.y + 1, coords.z + 1);
		
		for (int x = (int) b.minX; x <= b.maxX; x++)
			for (int y = (int) b.minY; y <= b.maxY; y++)
				for (int z = (int) b.minZ; z <= b.maxZ; z++)
				{
					Block block = world.getBlock(x, y, z);
					
					if (block == target)
					{
						currentDrops.addAll(Utils.getBlockDrops(world, player, block, stack, x, y, z));
						world.setBlockToAir(x, y, z);
						numMined++;
						harvestVein(world, player, stack, new Coordinates(x, y, z), target, currentDrops, numMined);
					}
				}
	}
	
	public static boolean isOre(Block block)
	{
		if (block.equals(Blocks.lit_redstone_ore))
		{
			return true;
		}
		
		return getOreDictionaryName(new ItemStack(block)).startsWith("ore");
	}
	
	public static ItemStack getStackFromInv(IInventory inv, ItemStack stack)
	{
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack s = inv.getStackInSlot(i);
			
			if (s == null)
			{
				continue;
			}
			
			if (basicAreStacksEqual(stack, s))
			{
				return s;
			}
		}
		
		return null;
	}
	
	public static ItemStack getStackFromInv(ItemStack[] inv, ItemStack stack)
	{
		for (ItemStack s : inv)
		{
			if (s == null)
			{
				continue;
			}
			
			if (basicAreStacksEqual(stack, s))
			{
				return s;
			}
		}
		
		return null;
	}

	public static void repellEntities(Entity player)
	{
		AxisAlignedBB bBox = AxisAlignedBB.getBoundingBox(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5);
		List<Entity> list = player.worldObj.getEntitiesWithinAABB(Entity.class, bBox);
		
		for (Entity ent : list)
		{
			if (ent instanceof EntityPlayer)
			{
				continue;
			}
			
			Vec3 p = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
			Vec3 t = Vec3.createVectorHelper(ent.posX, ent.posY, ent.posZ);
			double distance = p.distanceTo(t) + 0.1D;

			Vec3 r = Vec3.createVectorHelper(t.xCoord - p.xCoord, t.yCoord - p.yCoord, t.zCoord - p.zCoord);

			ent.motionX += r.xCoord / 1.5D / distance;
			ent.motionY += r.yCoord / 1.5D / distance;
			ent.motionZ += r.zCoord / 1.5D / distance;
		}
	}

	public static ArrayList<ItemStack> getBlockDrops(World world, EntityPlayer player, Block block, ItemStack stack, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0 && block.canSilkHarvest(world, player, x, y, z, meta))
		{
			ArrayList<ItemStack> list = new ArrayList<ItemStack>();
			list.add(new ItemStack(block, 1, meta));
			return list;
		}
		
		return block.getDrops(world, x, y, z, meta, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
	}
	
	/**
	 *	@throws NullPointerException 
	 */
	public static ItemStack getStackFromString(String internal, int metaData)
	{
		Item item = (Item) Item.itemRegistry.getObject(internal);
		
		if (item == null)
		{
			return null;
		}
		
		return new ItemStack(item, 1, metaData);
	}

	public static boolean canFillTank(IFluidHandler tank, Fluid fluid, int side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(side);

		if (tank.canFill(dir, fluid))
		{
			boolean canFill = false;

			for (FluidTankInfo tankInfo : tank.getTankInfo(dir))
			{
				if (tankInfo.fluid == null)
				{
					canFill = true;
					break;
				}

				if (tankInfo.fluid.getFluid() == fluid && tankInfo.fluid.amount < tankInfo.capacity)
				{
					canFill = true;
					break;
				}
			}

			return canFill;
		}

		return false;
	}

	public static void fillTank(IFluidHandler tank, Fluid fluid, int side, int quantity)
	{
		tank.fill(ForgeDirection.getOrientation(side), new FluidStack(fluid, quantity), true);
	}

	public static void closeStream(Closeable c)
	{
		if (c != null)
		{
			try
			{
				c.close();
			}
			catch (IOException e)
			{
				PELogger.logFatal("IO Error: couldn't close stream!");
				e.printStackTrace();
			}
		}
	}

	public static int randomIntInRange(int max, int min)
	{
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	private static void loadEntityLists()
	{
		//Peacefuls
		peacefuls.add(EntityCow.class);
		peacefuls.add(EntityMooshroom.class);
		peacefuls.add(EntitySheep.class);
		peacefuls.add(EntityPig.class);
		peacefuls.add(EntityChicken.class);
		peacefuls.add(EntityBat.class);
		peacefuls.add(EntityOcelot.class);
		peacefuls.add(EntityVillager.class);
		peacefuls.add(EntitySquid.class);
		peacefuls.add(EntityHorse.class);
		
		//Mobs
		mobs.add(EntityZombie.class);
		mobs.add(EntitySkeleton.class);
		mobs.add(EntitySpider.class);
		mobs.add(EntityCaveSpider.class);
		mobs.add(EntityCreeper.class);
		mobs.add(EntityEnderman.class);
		mobs.add(EntitySilverfish.class);
		mobs.add(EntityGhast.class);
		mobs.add(EntityPigZombie.class);
		mobs.add(EntitySlime.class);
		mobs.add(EntityWitch.class);
		mobs.add(EntityBlaze.class);
	}
}
