package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableList;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for anything that touches a World.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper
{
	static ImmutableList<? extends Class<? extends EntityLiving>> peacefuls = ImmutableList.of(
			EntitySheep.class, EntityPig.class, EntityCow.class,
			EntityMooshroom.class, EntityChicken.class, EntityBat.class,
			EntityVillager.class, EntitySquid.class, EntityOcelot.class,
			EntityWolf.class, EntityHorse.class
	);
	static ImmutableList<? extends Class<? extends EntityLiving>> mobs = ImmutableList.of(
			EntityZombie.class, EntitySkeleton.class, EntityCreeper.class,
			EntitySpider.class, EntityEnderman.class, EntitySilverfish.class,
			EntityPigZombie.class, EntityGhast.class, EntityBlaze.class,
			EntitySlime.class, EntityWitch.class
	);

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

		List<TileEntity> list = new ArrayList<>();
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

	public static Entity getRandomEntity(World world, Entity toRandomize)
	{
		Class entClass = toRandomize.getClass();

		if (peacefuls.contains(entClass))
		{
			return Utils.getNewEntityInstance((Class) Utils.getRandomListEntry(peacefuls, entClass), world);
		}
		else if (mobs.contains(entClass))
		{
			return Utils.getNewEntityInstance((Class) Utils.getRandomListEntry(mobs, entClass), world);
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
						currentDrops.addAll(Utils.getBlockDrops(world, player, block, stack, x, y, z));
						world.setBlockToAir(x, y, z);
						numMined++;
						harvestVein(world, player, stack, new Coordinates(x, y, z), target, currentDrops, numMined);
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
