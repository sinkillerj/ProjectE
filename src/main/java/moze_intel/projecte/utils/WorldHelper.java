package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Helper class for anything that touches a World.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper
{
	public static final ImmutableList<? extends Class<? extends EntityLiving>> peacefuls = ImmutableList.of(
			EntitySheep.class, EntityPig.class, EntityCow.class,
			EntityMooshroom.class, EntityChicken.class, EntityBat.class,
			EntityVillager.class, EntitySquid.class, EntityOcelot.class,
			EntityWolf.class, EntityHorse.class
	);
	public static final ImmutableList<? extends Class<? extends EntityLiving>> mobs = ImmutableList.of(
			EntityZombie.class, EntitySkeleton.class, EntityCreeper.class,
			EntitySpider.class, EntityEnderman.class, EntitySilverfish.class,
			EntityPigZombie.class, EntityGhast.class, EntityBlaze.class,
			EntitySlime.class, EntityWitch.class
	);

	public static Set<Class<? extends Entity>> interdictionBlacklist = Sets.newHashSet();

	public static boolean blacklistInterdiction(Class<? extends Entity> clazz)
	{
		if (!interdictionBlacklist.contains(clazz))
		{
			interdictionBlacklist.add(clazz);
			return true;
		}
		return false;
	}

	public static void createLootDrop(List<ItemStack> drops, World world, double x, double y, double z)
	{
		if (drops.isEmpty())
		{
			return;
		}
		world.spawnEntityInWorld(new EntityLootBall(world, drops, x, y, z));
	}

	public static List<TileEntity> getAdjacentTileEntities(World world, TileEntity tile)
	{
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;

		List<TileEntity> list = Lists.newArrayList();
		for (int i = 0; i <= 5; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity te = world.getTileEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
			if (te != null)
			{
				list.add(te);
			}
		}
		return list;
	}

	public static ArrayList<ItemStack> getBlockDrops(World world, EntityPlayer player, Block block, ItemStack stack, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0 && block.canSilkHarvest(world, player, x, y, z, meta))
		{
			ArrayList<ItemStack> list = Lists.newArrayList(new ItemStack(block, 1, meta));
			return list;
		}

		return block.getDrops(world, x, y, z, meta, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AxisAlignedBB getBroadDeepBox(Coordinates coords, ForgeDirection direction, int offset)
	{
		if (direction.offsetX > 0)
		{
			return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - offset, coords.z - offset, coords.x, coords.y + offset, coords.z + offset);
		}
		else if (direction.offsetX < 0)
		{
			return AxisAlignedBB.getBoundingBox(coords.x, coords.y - offset, coords.z - offset, coords.x + offset, coords.y + offset, coords.z + offset);
		}
		else if (direction.offsetY > 0)
		{
			return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - offset, coords.z - offset, coords.x + offset, coords.y, coords.z + offset);
		}
		else if (direction.offsetY < 0)
		{
			return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y, coords.z - offset, coords.x + offset, coords.y + offset, coords.z + offset);
		}
		else if (direction.offsetZ > 0)
		{
			return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - offset, coords.z - offset, coords.x + offset, coords.y + offset, coords.z);
		}
		else if (direction.offsetZ < 0)
		{
			return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - offset, coords.z, coords.x + offset, coords.y + offset, coords.z + offset);
		}
		return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AxisAlignedBB getDeepBox(Coordinates coords, ForgeDirection direction, int depth)
	{
		if (direction.offsetX != 0)
		{
			if (direction.offsetX > 0)
			{
				return AxisAlignedBB.getBoundingBox(coords.x - depth, coords.y - 1, coords.z - 1, coords.x, coords.y + 1, coords.z + 1);
			}
			else return AxisAlignedBB.getBoundingBox(coords.x, coords.y - 1, coords.z - 1, coords.x + depth, coords.y + 1, coords.z + 1);
		}
		else if (direction.offsetY != 0)
		{
			if (direction.offsetY > 0)
			{
				return AxisAlignedBB.getBoundingBox(coords.x - 1, coords.y - depth, coords.z - 1, coords.x + 1, coords.y, coords.z + 1);
			}
			else return AxisAlignedBB.getBoundingBox(coords.x - 1, coords.y, coords.z - 1, coords.x + 1, coords.y + depth, coords.z + 1);
		}
		else
		{
			if (direction.offsetZ > 0)
			{
				return AxisAlignedBB.getBoundingBox(coords.x - 1, coords.y - 1, coords.z - depth, coords.x + 1, coords.y + 1, coords.z);
			}
			else return AxisAlignedBB.getBoundingBox(coords.x - 1, coords.y - 1, coords.z, coords.x + 1, coords.y + 1, coords.z + depth);
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box.
	 * Y level remains constant. As such, a direction hit is unneeded.
	 */
	public static AxisAlignedBB getFlatYBox(Coordinates coords, int offset)
	{
		return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y, coords.z - offset, coords.x + offset, coords.y, coords.z + offset);
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

	public static Entity getRandomEntity(World world, Entity toRandomize)
	{
		Class entClass = toRandomize.getClass();

		if (peacefuls.contains(entClass))
		{
			return getNewEntityInstance((Class) CollectionHelper.getRandomListEntry(peacefuls, entClass), world);
		}
		else if (mobs.contains(entClass))
		{
			return getNewEntityInstance((Class) CollectionHelper.getRandomListEntry(mobs, entClass), world);
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

	public static List<TileEntity> getTileEntitiesWithinAABB(World world, AxisAlignedBB bBox)
	{
		List<TileEntity> list = Lists.newArrayList();

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

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static void harvestVein(World world, EntityPlayer player, ItemStack stack, Coordinates coords, Block target, List<ItemStack> currentDrops, int numMined)
	{
		if (numMined >= Constants.MAX_VEIN_SIZE)
		{
			return;
		}

		AxisAlignedBB b = AxisAlignedBB.getBoundingBox(coords.x - 1, coords.y - 1, coords.z - 1, coords.x + 1, coords.y + 1, coords.z + 1);

		for (int x = (int) b.minX; x <= b.maxX; x++)
			for (int y = (int) b.minY; y <= b.maxY; y++)
				for (int z = (int) b.minZ; z <= b.maxZ; z++)
				{
					Block block = world.getBlock(x, y, z);

					if (block == target)
					{
						currentDrops.addAll(getBlockDrops(world, player, block, stack, x, y, z));
						world.setBlockToAir(x, y, z);
						numMined++;
						harvestVein(world, player, stack, new Coordinates(x, y, z), target, currentDrops, numMined);
					}
				}
	}

	public static boolean isArrowInGround(EntityArrow arrow)
	{
		return ReflectionHelper.getArrowInGround(arrow);
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given point
	 * If isSWRG is true, then the blacklist is not checked.
	 */
	public static void repelEntitiesInAABBFromPoint(World world, AxisAlignedBB effectBounds, double x, double y, double z, boolean isSWRG)
	{
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, effectBounds);

		for (Entity ent : list)
		{
			if (isSWRG || !interdictionBlacklist.contains(ent.getClass())) {
				// SWRG repels all, only the torch respects blacklist
				if ((ent instanceof EntityLiving) || (ent instanceof IProjectile))
				{
					if (ProjectEConfig.interdictionMode && !(ent instanceof IMob || ent instanceof IProjectile))
					{
						continue;
					}
					else
					{
						if (ent instanceof EntityArrow && ((EntityArrow) ent).onGround)
						{
							continue;
						}
						Vec3 p = Vec3.createVectorHelper(x, y, z);
						Vec3 t = Vec3.createVectorHelper(ent.posX, ent.posY, ent.posZ);
						double distance = p.distanceTo(t) + 0.1D;

						Vec3 r = Vec3.createVectorHelper(t.xCoord - p.xCoord, t.yCoord - p.yCoord, t.zCoord - p.zCoord);

						ent.motionX += r.xCoord / 1.5D / distance;
						ent.motionY += r.yCoord / 1.5D / distance;
						ent.motionZ += r.zCoord / 1.5D / distance;
					}
				}
			}
		}
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
}
