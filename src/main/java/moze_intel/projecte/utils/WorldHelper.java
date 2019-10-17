package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
	private static final List<Class<? extends MobEntity>> peacefuls = Lists.newArrayList(
			SheepEntity.class, PigEntity.class, CowEntity.class,
			MooshroomEntity.class, ChickenEntity.class, BatEntity.class,
			VillagerEntity.class, SquidEntity.class, OcelotEntity.class,
			WolfEntity.class, HorseEntity.class, RabbitEntity.class,
			DonkeyEntity.class, MuleEntity.class, PolarBearEntity.class,
			LlamaEntity.class, ParrotEntity.class
	);

	private static final List<Class<? extends MobEntity>> mobs = Lists.newArrayList(
			ZombieEntity.class, SkeletonEntity.class, CreeperEntity.class,
			SpiderEntity.class, EndermanEntity.class, SilverfishEntity.class,
			ZombiePigmanEntity.class, GhastEntity.class, BlazeEntity.class,
			SlimeEntity.class, WitchEntity.class, RabbitEntity.class, EndermiteEntity.class,
			StrayEntity.class, WitherSkeletonEntity.class, SkeletonHorseEntity.class, ZombieHorseEntity.class,
			ZombieVillagerEntity.class, HuskEntity.class, GuardianEntity.class,
			EvokerEntity.class, VexEntity.class, VindicatorEntity.class, ShulkerEntity.class
	);

	private static Set<EntityType<?>> interdictionBlacklist = Collections.emptySet();

	private static Set<EntityType<?>> swrgBlacklist = Collections.emptySet();

	public static void setInterdictionBlacklist(Set<EntityType<?>> types)
	{
		interdictionBlacklist = ImmutableSet.copyOf(types);
	}

	public static void setSwrgBlacklist(Set<EntityType<?>> types)
	{
		swrgBlacklist = ImmutableSet.copyOf(types);
	}

	public static boolean addPeaceful(Class<? extends MobEntity> clazz)
	{
		if (!peacefuls.contains(clazz))
		{
			peacefuls.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean removePeaceful(Class<? extends MobEntity> clazz)
	{
		return peacefuls.remove(clazz);
	}

	public static void clearPeacefuls()
	{
		peacefuls.clear();
	}

	public static boolean addMob(Class<? extends MobEntity> clazz)
	{
		if (!mobs.contains(clazz))
		{
			mobs.add(clazz);
			return true;
		}
		return false;
	}

	public static boolean removeMob(Class<? extends MobEntity> clazz)
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
			ItemEntity ent = new ItemEntity(world, x, y, z);
			ent.setItem(drop);
			world.addEntity(ent);
		}
	}

	/**
	 * Equivalent of World.newExplosion
	 */
	public static void createNovaExplosion(World world, Entity exploder, double x, double y, double z, float power)
	{
		NovaExplosion explosion = new NovaExplosion(world, exploder, x, y, z, power, true, Explosion.Mode.BREAK);
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
				ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ());
				ent.setItem(stack);
				world.addEntity(ent);
			}
		}
	}

	public static void extinguishNearby(World world, PlayerEntity player)
	{
		BlockPos.getAllInBox(new BlockPos(player).add(-1, -1, -1), new BlockPos(player).add(1, 1, 1)).forEach(pos ->
		{
			if (world.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos))
			{
				world.removeBlock(pos, false);
			}
		});
	}
	
	public static void freezeInBoundingBox(World world, AxisAlignedBB box, PlayerEntity player, boolean random)
	{
		for (BlockPos pos : getPositionsFromBox(box))
		{
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();

			if (b == Blocks.WATER && (!random || world.rand.nextInt(128) == 0))
			{
				if (player != null)
				{
					PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) player), pos, Blocks.ICE.getDefaultState());
				}
				else
				{
					world.setBlockState(pos, Blocks.ICE.getDefaultState());
				}
			}
			else if (Block.doesSideFillSquare(state.getCollisionShape(world, pos.down()), Direction.UP))
			{
				BlockPos up = pos.up();
				BlockState stateUp = world.getBlockState(up);
				BlockState newState = null;

				if (stateUp.getBlock().isAir(stateUp, world, up) && (!random || world.rand.nextInt(128) == 0))
				{
					newState = Blocks.SNOW.getDefaultState();
				} else if (stateUp.getBlock() == Blocks.SNOW && stateUp.get(SnowBlock.LAYERS) < 8
							&& world.rand.nextInt(512) == 0)
				{
					newState = stateUp.with(SnowBlock.LAYERS, stateUp.get(SnowBlock.LAYERS) + 1);
				}

				if (newState != null)
				{
					if (player != null)
					{
						PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) player), up, newState);
					}
					else
					{
						world.setBlockState(up, newState);
					}
				}
			}
		}
	}
	
	public static Map<Direction, TileEntity> getAdjacentTileEntitiesMapped(final World world, final TileEntity tile)
	{
		Map<Direction, TileEntity> ret = new EnumMap<>(Direction.class);

		for (Direction dir : Direction.values()) {
			TileEntity candidate = world.getTileEntity(tile.getPos().offset(dir));
			if (candidate != null) {
				ret.put(dir, candidate);
			}
		}

		return ret;
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AxisAlignedBB getBroadDeepBox(BlockPos pos, Direction direction, int offset)
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
	public static AxisAlignedBB getDeepBox(BlockPos pos, Direction direction, int depth)
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
		return () -> BlockPos.getAllInBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ)).iterator();
	}

	public static MobEntity getRandomEntity(World world, MobEntity toRandomize)
	{
		Class<? extends MobEntity> entClass = toRandomize.getClass();

		if (peacefuls.contains(entClass))
		{
			return getNewEntityInstance(CollectionHelper.getRandomListEntry(peacefuls, entClass), world);
		}
		else if (mobs.contains(entClass))
		{
			MobEntity ent = getNewEntityInstance(CollectionHelper.getRandomListEntry(mobs, entClass), world);
			if (ent instanceof RabbitEntity)
			{
				((RabbitEntity) ent).setRabbitType(99);
			}
			return ent;
		}
		else if (world.rand.nextInt(2) == 0)
		{
			return getNewEntityInstance(SlimeEntity.class, world);
		}
		else
		{
			return getNewEntityInstance(SheepEntity.class, world);
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
			ent.setMotion(ent.getMotion().add(dX / dist * vel * 0.1, dY / dist * vel * 0.2, dZ / dist * vel * 0.1));
		}
	}

	public static void growNearbyRandomly(boolean harvest, World world, BlockPos pos, PlayerEntity player)
	{
		int chance = harvest ? 16 : 32;

		BlockPos.getAllInBox(pos.add(-5, -3, -5), pos.add(5, 3, 5)).forEach(currentPos ->
		{
			BlockState state = world.getBlockState(currentPos);
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
							&& (player == null || PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), currentPos)))
					{
						world.destroyBlock(currentPos, true);
					}
				}
				else if (world.rand.nextInt(chance) == 0)
				{
					if (ProjectEConfig.items.harvBandGrass.get() || !crop.getTranslationKey().toLowerCase(Locale.ROOT).contains("grass"))
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
						state.randomTick(world, currentPos, world.rand);
					}
				}

				if (harvest)
				{
					if (crop instanceof FlowerBlock)
					{
						if (player == null || PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), currentPos))
						{
							world.destroyBlock(currentPos, true);
						}
					}
					if (crop == Blocks.SUGAR_CANE || crop == Blocks.CACTUS)
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
							for (int i = crop == Blocks.SUGAR_CANE ? 1 : 0; i < 3; i++)
							{
								if (player != null && PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), currentPos.up(i)))
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
						int age = state.get(NetherWartBlock.AGE);
						if (age == 3)
						{
							if (player == null || PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, currentPos))
							{
								world.destroyBlock(currentPos, true);
							}
						}
					}
				}

			}
		});
	}

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static int harvestVein(World world, PlayerEntity player, ItemStack stack, BlockPos pos, Block target, List<ItemStack> currentDrops, int numMined)
	{
		if (numMined >= Constants.MAX_VEIN_SIZE)
		{
			return numMined;
		}

		AxisAlignedBB b = new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

		for (BlockPos currentPos : getPositionsFromBox(b))
		{
			BlockState currentState = world.getBlockState(currentPos);

			if (currentState.getBlock() == target)
			{
				numMined++;
				if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), currentPos))
				{
					currentDrops.addAll(Block.getDrops(currentState, (ServerWorld) world, currentPos, world.getTileEntity(currentPos), player, stack));
					world.removeBlock(currentPos, false);
					numMined = harvestVein(world, player, stack, currentPos, target, currentDrops, numMined);
					if (numMined >= Constants.MAX_VEIN_SIZE) {
						break;
					}
				}
			}
		}
		return numMined;
	}
	
	public static void igniteNearby(World world, PlayerEntity player)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(new BlockPos(player).add(-8, -5, -8), new BlockPos(player).add(8, 5, 8)))
		{
			if (world.rand.nextInt(128) == 0 && world.isAirBlock(pos))
			{
				PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) player), pos.toImmutable(), Blocks.FIRE.getDefaultState());
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
			if ((isSWRG && !swrgBlacklist.contains(ent.getType()))
					|| (!isSWRG && !interdictionBlacklist.contains(ent.getType()))) {
				if ((ent instanceof MobEntity) || (ent instanceof IProjectile))
				{
					if (!isSWRG && ProjectEConfig.effects.interdictionMode.get() && !(ent instanceof IMob || ent instanceof IProjectile))
					{
						continue;
					}
					else
					{
						if (ent instanceof AbstractArrowEntity && ((AbstractArrowEntity) ent).onGround)
						{
							continue;
						}
						Vec3d p = new Vec3d(x, y, z);
						Vec3d t = new Vec3d(ent.posX, ent.posY, ent.posZ);
						double distance = p.distanceTo(t) + 0.1D;

						Vec3d r = new Vec3d(t.x - p.x, t.y - p.y, t.z - p.z);

						ent.setMotion(ent.getMotion().add(r.scale(1/1.5D * 1/distance)));
					}
				}
			}
		}
	}
}
