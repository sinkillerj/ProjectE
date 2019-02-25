package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for anything that touches a World.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper
{
	private static final List<Class<? extends EntityLiving>> peacefuls = Lists.newArrayList(
			EntitySheep.class, EntityPig.class, EntityCow.class,
			EntityMooshroom.class, EntityChicken.class, EntityBat.class,
			EntityVillager.class, EntitySquid.class, EntityOcelot.class,
			EntityWolf.class, EntityHorse.class, EntityRabbit.class,
			EntityDonkey.class, EntityMule.class, EntityPolarBear.class,
			EntityLlama.class, EntityParrot.class
	);

	private static final List<Class<? extends EntityLiving>> mobs = Lists.newArrayList(
			EntityZombie.class, EntitySkeleton.class, EntityCreeper.class,
			EntitySpider.class, EntityEnderman.class, EntitySilverfish.class,
			EntityPigZombie.class, EntityGhast.class, EntityBlaze.class,
			EntitySlime.class, EntityWitch.class, EntityRabbit.class, EntityEndermite.class,
			EntityStray.class, EntityWitherSkeleton.class, EntitySkeletonHorse.class, EntityZombieHorse.class,
			EntityZombieVillager.class, EntityHusk.class, EntityGuardian.class,
			EntityEvoker.class, EntityVex.class, EntityVindicator.class, EntityShulker.class
	);

	private static final Set<Class<? extends Entity>> interdictionBlacklist = new HashSet<>();

	private static final Set<Class<? extends Entity>> swrgBlacklist = new HashSet<>();

	public static boolean blacklistInterdiction(Class<? extends Entity> clazz)
	{
		if (!interdictionBlacklist.contains(clazz))
		{
			interdictionBlacklist.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean blacklistSwrg(Class<? extends Entity> clazz)
	{
		if (!interdictionBlacklist.contains(clazz))
		{
			interdictionBlacklist.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean addPeaceful(Class<? extends EntityLiving> clazz)
	{
		if (!peacefuls.contains(clazz))
		{
			peacefuls.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean removePeaceful(Class<? extends EntityLiving> clazz)
	{
		return peacefuls.remove(clazz);
	}

	public static void clearPeacefuls()
	{
		peacefuls.clear();
	}

	public static boolean addMob(Class<? extends EntityLiving> clazz)
	{
		if (!mobs.contains(clazz))
		{
			mobs.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean removeMob(Class<? extends EntityLiving> clazz)
	{
		return mobs.remove(clazz);
	}

	public static void clearMobs()
	{
		mobs.clear();
	}

	public static void createLootDrop(List<ItemStack> drops, World world, BlockPos pos)
	{
		createLootDrop(drops, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void createLootDrop(List<ItemStack> drops, World world, double x, double y, double z)
	{
		ItemHelper.compactItemListNoStacksize(drops);

		for (ItemStack drop : drops)
		{
			EntityItem ent = new EntityItem(world, x, y, z);
			ent.setItem(drop);
			world.spawnEntity(ent);
		}
	}

	/**
	 * Equivalent of World.newExplosion
	 */
	public static void createNovaExplosion(World world, Entity exploder, double x, double y, double z, float power)
	{
		NovaExplosion explosion = new NovaExplosion(world, exploder, x, y, z, power, true, true);
		if (!MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion)))
		{
			explosion.doExplosionA();
			explosion.doExplosionB(true);
		}
	}

	public static void dropInventory(IItemHandler inv, World world, BlockPos pos)
	{
		if (inv == null)
			return;

		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);

			if (!stack.isEmpty())
			{
				EntityItem ent = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ());
				ent.setItem(stack);
				world.spawnEntity(ent);
			}
		}
	}

	public static void extinguishNearby(World world, EntityPlayer player)
	{
		for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(player).add(-1, -1, -1), new BlockPos(player).add(1, 1, 1)))
		{
			if (world.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos))
			{
				world.setBlockToAir(pos);
			}
		}
	}
	
	public static void freezeInBoundingBox(World world, AxisAlignedBB box, EntityPlayer player, boolean random)
	{
		for (BlockPos pos : getPositionsFromBox(box))
		{
			Block b = world.getBlockState(pos).getBlock();

			if ((b == Blocks.WATER || b == Blocks.FLOWING_WATER) && (!random || world.rand.nextInt(128) == 0))
			{
				if (player != null)
				{
					PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), pos, Blocks.ICE.getDefaultState());
				}
				else
				{
					world.setBlockState(pos, Blocks.ICE.getDefaultState());
				}
			}
			else if (b.isSideSolid(world.getBlockState(pos), world, pos, EnumFacing.UP))
			{
				BlockPos up = pos.up();
				IBlockState stateUp = world.getBlockState(up);
				IBlockState newState = null;

				if (stateUp.getBlock().isAir(stateUp, world, up) && (!random || world.rand.nextInt(128) == 0))
				{
					newState = Blocks.SNOW_LAYER.getDefaultState();
				} else if (stateUp.getBlock() == Blocks.SNOW_LAYER && stateUp.getValue(BlockSnow.LAYERS) < 8
							&& world.rand.nextInt(512) == 0)
				{
					newState = stateUp.withProperty(BlockSnow.LAYERS, stateUp.getValue(BlockSnow.LAYERS) + 1);
				}

				if (newState != null)
				{
					if (player != null)
					{
						PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), up, newState);
					}
					else
					{
						world.setBlockState(up, newState);
					}
				}
			}
		}
	}
	
	public static Map<EnumFacing, TileEntity> getAdjacentTileEntitiesMapped(final World world, final TileEntity tile)
	{
		Map<EnumFacing, TileEntity> ret = new EnumMap<>(EnumFacing.class);

		for (EnumFacing dir : EnumFacing.VALUES) {
			TileEntity candidate = world.getTileEntity(tile.getPos().offset(dir));
			if (candidate != null) {
				ret.put(dir, candidate);
			}
		}

		return ret;
	}

	public static List<ItemStack> getBlockDrops(World world, EntityPlayer player, IBlockState state, ItemStack stack, BlockPos pos)
	{
		if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0 && state.getBlock().canSilkHarvest(world, pos, state, player))
		{
			return Lists.newArrayList(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
		}

		return state.getBlock().getDrops(world, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AxisAlignedBB getBroadDeepBox(BlockPos pos, EnumFacing direction, int offset)
	{
		switch (direction)
		{
			case EAST: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX(), pos.getY() + offset, pos.getZ() + offset);
			case WEST: return new AxisAlignedBB(pos.getX(), pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case UP: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
			case DOWN: return new AxisAlignedBB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case SOUTH: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ());
			case NORTH: return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ(), pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AxisAlignedBB getDeepBox(BlockPos pos, EnumFacing direction, int depth)
	{
		switch (direction)
		{
			case EAST: return new AxisAlignedBB(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			case WEST: return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
			case UP: return new AxisAlignedBB(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			case DOWN: return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
			case SOUTH: return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
			case NORTH: return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
			default: return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box.
	 * Y level remains constant. As such, a direction hit is unneeded.
	 */
	public static AxisAlignedBB getFlatYBox(BlockPos pos, int offset)
	{
		return new AxisAlignedBB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
	}

	public static <T extends Entity> T getNewEntityInstance(Class<T> c, World world)
	{
		try
		{
			return c.getConstructor(World.class).newInstance(world);
		}
		catch (Exception e)
		{
			PECore.LOGGER.fatal("Could not create new entity instance for: {}", c.getCanonicalName());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Wrapper around BlockPos.getAllInBox() with an AABB
	 * Note that this is inclusive of all positions in the AABB!
	 */
	public static Iterable<BlockPos> getPositionsFromBox(AxisAlignedBB box)
	{
		return BlockPos.getAllInBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
	}

	public static EntityLiving getRandomEntity(World world, EntityLiving toRandomize)
	{
		Class<? extends EntityLiving> entClass = toRandomize.getClass();

		if (peacefuls.contains(entClass))
		{
			return getNewEntityInstance(CollectionHelper.getRandomListEntry(peacefuls, entClass), world);
		}
		else if (mobs.contains(entClass))
		{
			EntityLiving ent = getNewEntityInstance(CollectionHelper.getRandomListEntry(mobs, entClass), world);
			if (ent instanceof EntityRabbit)
			{
				((EntityRabbit) ent).setRabbitType(99);
			}
			return ent;
		}
		else if (world.rand.nextInt(2) == 0)
		{
			return getNewEntityInstance(EntitySlime.class, world);
		}
		else
		{
			return getNewEntityInstance(EntitySheep.class, world);
		}
	}

	public static List<TileEntity> getTileEntitiesWithinAABB(World world, AxisAlignedBB bBox)
	{
		List<TileEntity> list = new ArrayList<>();

		for (BlockPos pos : getPositionsFromBox(bBox))
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null)
			{
				list.add(tile);
			}
		}

		return list;
	}

	/**
	 * Gravitates an entity, vanilla xp orb style, towards a position
	 * Code adapted from EntityXPOrb and OpenBlocks Vacuum Hopper, mostly the former
	 */
	public static void gravitateEntityTowards(Entity ent, double x, double y, double z)
	{
		double dX = x - ent.posX;
		double dY = y - ent.posY;
		double dZ = z - ent.posZ;
		double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

		double vel = 1.0 - dist / 15.0;
		if (vel > 0.0D)
		{
			vel *= vel;
			ent.motionX += dX / dist * vel * 0.1;
			ent.motionY += dY / dist * vel * 0.2;
			ent.motionZ += dZ / dist * vel * 0.1;
		}
	}

	public static void growNearbyRandomly(boolean harvest, World world, BlockPos pos, EntityPlayer player)
	{
		int chance = harvest ? 16 : 32;

		for (BlockPos currentPos : BlockPos.getAllInBox(pos.add(-5, -3, -5), pos.add(5, 3, 5)))
		{
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
				IGrowable growable = ((IGrowable) crop);
				if (!growable.canGrow(world, currentPos, state, false))
				{
					if (harvest
							&& crop != Blocks.MELON_STEM && crop != Blocks.PUMPKIN_STEM
							&& (player == null || PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos)))
					{
						world.destroyBlock(currentPos, true);
					}
				}
				else if (world.rand.nextInt(chance) == 0)
				{
					if (ProjectEConfig.items.harvBandGrass || !crop.getTranslationKey().toLowerCase(Locale.ROOT).contains("grass"))
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
						if (player == null || PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos))
						{
							world.destroyBlock(currentPos, true);
						}
					}
					if (crop == Blocks.REEDS || crop == Blocks.CACTUS)
					{
						boolean shouldHarvest = true;

						for (int i = 1; i < 3; i++)
						{
							if (world.getBlockState(currentPos.up(i)).getBlock() != crop)
							{
								shouldHarvest = false;
								break;
							}
						}

						if (shouldHarvest)
						{
							for (int i = crop == Blocks.REEDS ? 1 : 0; i < 3; i++)
							{
								if (player != null && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos.up(i)))
								{
									world.destroyBlock(currentPos.up(i), true);
								} else if (player == null)
								{
									world.destroyBlock(currentPos.up(i), true);
								}
							}
						}
					}
					if (crop == Blocks.NETHER_WART)
					{
						int age = state.getValue(BlockNetherWart.AGE);
						if (age == 3)
						{
							if (player == null || player != null && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos))
							{
								world.destroyBlock(currentPos, true);
							}
						}
					}
				}

			}
		}
	}

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static int harvestVein(World world, EntityPlayer player, ItemStack stack, BlockPos pos, IBlockState target, List<ItemStack> currentDrops, int numMined)
	{
		if (numMined >= Constants.MAX_VEIN_SIZE)
		{
			return numMined;
		}

		AxisAlignedBB b = new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

		for (BlockPos currentPos : getPositionsFromBox(b))
		{
			IBlockState currentState = world.getBlockState(currentPos);
			Block block = currentState.getBlock();

			if (currentState == target || (target == Blocks.LIT_REDSTONE_ORE && block == Blocks.REDSTONE_ORE))
			{
				numMined++;
				if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos))
				{
					currentDrops.addAll(getBlockDrops(world, player, currentState, stack, currentPos));
					world.setBlockToAir(currentPos);
					numMined = harvestVein(world, player, stack, currentPos, target, currentDrops, numMined);
					if (numMined >= Constants.MAX_VEIN_SIZE) {
						break;
					}
				}
			}
		}
		return numMined;
	}
	
	public static void igniteNearby(World world, EntityPlayer player)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(new BlockPos(player).add(-8, -5, -8), new BlockPos(player).add(8, 5, 8)))
		{
			if (world.rand.nextInt(128) == 0 && world.isAirBlock(pos))
			{
				PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos.toImmutable(), Blocks.FIRE.getDefaultState());
			}
		}
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given point
	 */
	public static void repelEntitiesInAABBFromPoint(World world, AxisAlignedBB effectBounds, double x, double y, double z, boolean isSWRG)
	{
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, effectBounds);

		for (Entity ent : list)
		{
			if ((isSWRG && !swrgBlacklist.contains(ent.getClass()))
					|| (!isSWRG && !interdictionBlacklist.contains(ent.getClass()))) {
				if ((ent instanceof EntityLiving) || (ent instanceof IProjectile))
				{
					if (!isSWRG && ProjectEConfig.effects.interdictionMode && !(ent instanceof IMob || ent instanceof IProjectile))
					{
						continue;
					}
					else
					{
						if (ent instanceof EntityArrow && ((EntityArrow) ent).onGround)
						{
							continue;
						}
						Vec3d p = new Vec3d(x, y, z);
						Vec3d t = new Vec3d(ent.posX, ent.posY, ent.posZ);
						double distance = p.distanceTo(t) + 0.1D;

						Vec3d r = new Vec3d(t.x - p.x, t.y - p.y, t.z - p.z);

						ent.motionX += r.x / 1.5D / distance;
						ent.motionY += r.y / 1.5D / distance;
						ent.motionZ += r.z / 1.5D / distance;
					}
				}
			}
		}
	}
}
