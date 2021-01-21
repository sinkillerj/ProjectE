package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.NetherRootsBlock;
import net.minecraft.block.NetherSproutsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.NetherrackBlock;
import net.minecraft.block.NyliumBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Helper class for anything that touches a World. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper {

	private static final Predicate<Entity> SWRG_REPEL_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_SWRG);
	private static final Predicate<Entity> INTERDICTION_REPEL_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_INTERDICTION);
	//Note: We don't need to check if the projectile entity is on the ground here or not, as if it is we would not get past validRepelEntity
	private static final Predicate<Entity> INTERDICTION_REPEL_HOSTILE_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_INTERDICTION) &&
																							(entity instanceof IMob || entity instanceof ProjectileEntity);

	public static void createLootDrop(List<ItemStack> drops, World world, BlockPos pos) {
		createLootDrop(drops, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void createLootDrop(List<ItemStack> drops, World world, double x, double y, double z) {
		if (!drops.isEmpty()) {
			ItemHelper.compactItemListNoStacksize(drops);
			for (ItemStack drop : drops) {
				world.addEntity(new ItemEntity(world, x, y, z, drop));
			}
		}
	}

	/**
	 * Equivalent of World.newExplosion
	 */
	public static void createNovaExplosion(World world, Entity exploder, double x, double y, double z, float power) {
		NovaExplosion explosion = new NovaExplosion(world, exploder, x, y, z, power, true, Explosion.Mode.BREAK);
		if (!MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion))) {
			explosion.doExplosionA();
			explosion.doExplosionB(true);
		}
	}

	public static void drainFluid(World world, BlockPos pos, BlockState state, Fluid toMatch) {
		Block block = state.getBlock();
		if (block instanceof IFluidBlock && ((IFluidBlock) block).getFluid().isEquivalentTo(toMatch)) {
			//If it is a fluid block drain it (may be the case for some custom block?)
			// We double check though the fluid block represents a given one though, in case there is some weird thing
			// going on and we are a bucket pickup handler for the actual water and fluid state
			((IFluidBlock) block).drain(world, pos, FluidAction.EXECUTE);
		} else if (block instanceof IBucketPickupHandler) {
			//If it is a bucket pickup handler (so may be a fluid logged block) "pick it up"
			// This includes normal fluid blocks
			((IBucketPickupHandler) block).pickupFluid(world, pos, state);
		}
	}

	public static void dropInventory(IItemHandler inv, World world, BlockPos pos) {
		if (inv == null) {
			return;
		}
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ());
				ent.setItem(stack);
				world.addEntity(ent);
			}
		}
	}

	public static void extinguishNearby(World world, PlayerEntity player) {
		BlockPos.getAllInBox(player.getPosition().add(-1, -1, -1), player.getPosition().add(1, 1, 1)).forEach(pos -> {
			pos = pos.toImmutable();
			if (world.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				world.removeBlock(pos, false);
			}
		});
	}

	public static void freezeInBoundingBox(World world, AxisAlignedBB box, PlayerEntity player, boolean random) {
		for (BlockPos pos : getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.toImmutable();
			if (b == Blocks.WATER && (!random || world.rand.nextInt(128) == 0)) {
				if (player != null) {
					PlayerHelper.checkedReplaceBlock((ServerPlayerEntity) player, pos, Blocks.ICE.getDefaultState());
				} else {
					world.setBlockState(pos, Blocks.ICE.getDefaultState());
				}
			} else if (Block.doesSideFillSquare(state.getCollisionShape(world, pos.down()), Direction.UP)) {
				BlockPos up = pos.up();
				BlockState stateUp = world.getBlockState(up);
				BlockState newState = null;

				if (stateUp.getBlock().isAir(stateUp, world, up) && (!random || world.rand.nextInt(128) == 0)) {
					newState = Blocks.SNOW.getDefaultState();
				} else if (stateUp.getBlock() == Blocks.SNOW && stateUp.get(SnowBlock.LAYERS) < 8 && world.rand.nextInt(512) == 0) {
					newState = stateUp.with(SnowBlock.LAYERS, stateUp.get(SnowBlock.LAYERS) + 1);
				}
				if (newState != null) {
					if (player != null) {
						PlayerHelper.checkedReplaceBlock((ServerPlayerEntity) player, up, newState);
					} else {
						world.setBlockState(up, newState);
					}
				}
			}
		}
	}

	/**
	 * Checks if a block is a {@link ILiquidContainer} that supports a specific fluid type.
	 */
	public static boolean isLiquidContainerForFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getBlock() instanceof ILiquidContainer && ((ILiquidContainer) state.getBlock()).canContainFluid(world, pos, state, fluid);
	}

	/**
	 * Attempts to place a fluid in a specific spot if the spot is a {@link ILiquidContainer} that supports the fluid otherwise try to place it in the block that is on
	 * the given side of the clicked block.
	 */
	public static void placeFluid(@Nullable ServerPlayerEntity player, World world, BlockPos pos, Direction sideHit, FlowingFluid fluid, boolean checkWaterVaporize) {
		if (isLiquidContainerForFluid(world, pos, world.getBlockState(pos), fluid)) {
			//If the spot can be logged with our fluid then try using the position directly
			placeFluid(player, world, pos, fluid, checkWaterVaporize);
		} else {
			//Otherwise offset it because we clicked against the block
			placeFluid(player, world, pos.offset(sideHit), fluid, checkWaterVaporize);
		}
	}

	/**
	 * Attempts to place a fluid in a specific spot, if the spot is a {@link ILiquidContainer} that supports the fluid, insert it instead.
	 *
	 * @apiNote Call this from the server side
	 */
	public static void placeFluid(@Nullable ServerPlayerEntity player, World world, BlockPos pos, FlowingFluid fluid, boolean checkWaterVaporize) {
		BlockState blockState = world.getBlockState(pos);
		if (checkWaterVaporize && world.getDimensionType().isUltrawarm() && fluid.isIn(FluidTags.WATER)) {
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			for (int l = 0; l < 8; ++l) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
			}
		} else if (isLiquidContainerForFluid(world, pos, blockState, fluid)) {
			((ILiquidContainer) blockState.getBlock()).receiveFluid(world, pos, blockState, fluid.getStillFluidState(false));
		} else {
			Material material = blockState.getMaterial();
			if ((!material.isSolid() || material.isReplaceable()) && !material.isLiquid()) {
				world.destroyBlock(pos, true);
			}
			if (player == null) {
				world.setBlockState(pos, fluid.getDefaultState().getBlockState());
			} else {
				PlayerHelper.checkedPlaceBlock(player, pos, fluid.getDefaultState().getBlockState());
			}
		}
	}

	/**
	 * Gets an ItemHandler of a specific tile from the given side. Falls back to using wrappers if the tile is an instance of an ISidedInventory/IInventory.
	 */
	@Nullable
	public static IItemHandler getItemHandler(@Nonnull TileEntity tile, @Nullable Direction direction) {
		Optional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve();
		if (capability.isPresent()) {
			return capability.get();
		} else if (tile instanceof ISidedInventory) {
			return new SidedInvWrapper((ISidedInventory) tile, direction);
		} else if (tile instanceof IInventory) {
			return new InvWrapper((IInventory) tile);
		}
		return null;
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AxisAlignedBB getBroadDeepBox(BlockPos pos, Direction direction, int offset) {
		switch (direction) {
			case EAST:
				return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX(), pos.getY() + offset, pos.getZ() + offset);
			case WEST:
				return new AxisAlignedBB(pos.getX(), pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case UP:
				return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
			case DOWN:
				return new AxisAlignedBB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case SOUTH:
				return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ());
			case NORTH:
				return new AxisAlignedBB(pos.getX() - offset, pos.getY() - offset, pos.getZ(), pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			default:
				return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AxisAlignedBB getDeepBox(BlockPos pos, Direction direction, int depth) {
		switch (direction) {
			case EAST:
				return new AxisAlignedBB(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			case WEST:
				return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
			case UP:
				return new AxisAlignedBB(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			case DOWN:
				return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
			case SOUTH:
				return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
			case NORTH:
				return new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
			default:
				return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Returns in AABB that is always a single block deep but is size x size orthogonal to the side hit
	 */
	public static AxisAlignedBB getBroadBox(BlockPos pos, Direction direction, int size) {
		switch (direction) {
			case EAST:
			case WEST:
				return new AxisAlignedBB(pos.getX(), pos.getY() - size, pos.getZ() - size, pos.getX(), pos.getY() + size, pos.getZ() + size);
			case UP:
			case DOWN:
				return new AxisAlignedBB(pos.getX() - size, pos.getY(), pos.getZ() - size, pos.getX() + size, pos.getY(), pos.getZ() + size);
			case SOUTH:
			case NORTH:
				return new AxisAlignedBB(pos.getX() - size, pos.getY() - size, pos.getZ(), pos.getX() + size, pos.getY() + size, pos.getZ());
			default:
				return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box. Y level remains constant. As such, a direction hit is unneeded.
	 */
	public static AxisAlignedBB getFlatYBox(BlockPos pos, int offset) {
		return new AxisAlignedBB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
	}

	/**
	 * Wrapper around BlockPos.getAllInBox() with an AABB Note that this is inclusive of all positions in the AABB!
	 */
	public static Iterable<BlockPos> getPositionsFromBox(AxisAlignedBB box) {
		return getPositionsFromBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
	}

	/**
	 * Wrapper around BlockPos.getAllInBox()
	 */
	public static Iterable<BlockPos> getPositionsFromBox(BlockPos corner1, BlockPos corner2) {
		return () -> BlockPos.getAllInBox(corner1, corner2).iterator();
	}


	public static List<TileEntity> getTileEntitiesWithinAABB(World world, AxisAlignedBB bBox) {
		List<TileEntity> list = new ArrayList<>();
		for (BlockPos pos : getPositionsFromBox(bBox)) {
			TileEntity tile = getTileEntity(world, pos);
			if (tile != null) {
				list.add(tile);
			}
		}
		return list;
	}

	/**
	 * Gravitates an entity, vanilla xp orb style, towards a position Code adapted from EntityXPOrb and OpenBlocks Vacuum Hopper, mostly the former
	 */
	public static void gravitateEntityTowards(Entity ent, double x, double y, double z) {
		double dX = x - ent.getPosX();
		double dY = y - ent.getPosY();
		double dZ = z - ent.getPosZ();
		double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

		double vel = 1.0 - dist / 15.0;
		if (vel > 0.0D) {
			vel *= vel;
			ent.setMotion(ent.getMotion().add(dX / dist * vel * 0.1, dY / dist * vel * 0.2, dZ / dist * vel * 0.1));
		}
	}

	public static void growNearbyRandomly(boolean harvest, World world, BlockPos pos, PlayerEntity player) {
		if (!(world instanceof ServerWorld)) {
			return;
		}
		int chance = harvest ? 16 : 32;
		for (BlockPos currentPos : getPositionsFromBox(pos.add(-5, -3, -5), pos.add(5, 3, 5))) {
			currentPos = currentPos.toImmutable();
			BlockState state = world.getBlockState(currentPos);
			Block crop = state.getBlock();

			// Vines, leaves, tallgrass, deadbush, doubleplants
			// Note: Nether roots and sprouts are like these but don't actually implement IForgeShearable
			if (crop instanceof IForgeShearable || crop instanceof NetherRootsBlock || crop instanceof NetherSproutsBlock) {
				if (harvest) {
					harvestBlock(world, currentPos, (ServerPlayerEntity) player);
				}
			}
			// Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
			// Mushroom, potato, sapling, stems, tallgrass
			else if (crop instanceof IGrowable) {
				IGrowable growable = (IGrowable) crop;
				if (!growable.canGrow(world, currentPos, state, false)) {
					if (harvest && !crop.isIn(PETags.Blocks.BLACKLIST_HARVEST)) {
						harvestBlock(world, currentPos, (ServerPlayerEntity) player);
					}
				} else if (ProjectEConfig.server.items.harvBandGrass.get() || !isGrassLikeBlock(crop)) {
					if (world.rand.nextInt(chance) == 0) {
						growable.grow((ServerWorld) world, world.rand, currentPos, state);
					}
				}
			}
			// All modded
			// Cactus, Reeds, Netherwart, Flower
			else if (crop instanceof IPlantable) {
				if (world.rand.nextInt(chance / 4) == 0) {
					for (int i = 0; i < (harvest ? 8 : 4); i++) {
						state.randomTick((ServerWorld) world, currentPos, world.rand);
					}
				}
				if (harvest) {
					if (crop instanceof FlowerBlock || crop instanceof DoublePlantBlock) {
						//Handle double plant blocks that were not already handled due to being shearable
						harvestBlock(world, currentPos, (ServerPlayerEntity) player);
					} else if (crop == Blocks.SUGAR_CANE || crop == Blocks.CACTUS) {
						if (world.getBlockState(currentPos.up()).getBlock() == crop && world.getBlockState(currentPos.up(2)).getBlock() == crop) {
							for (int i = crop == Blocks.SUGAR_CANE ? 1 : 0; i < 3; i++) {
								harvestBlock(world, currentPos.up(i), (ServerPlayerEntity) player);
							}
						}
					} else if (crop == Blocks.NETHER_WART) {
						if (state.get(NetherWartBlock.AGE) == 3) {
							harvestBlock(world, currentPos, (ServerPlayerEntity) player);
						}
					}
				}
			}
		}
	}

	private static boolean isGrassLikeBlock(Block crop) {
		//Note: We count netherrack like a grass like block as it propagates growing to neighboring nylium blocks
		// and its can grow methods behave like one
		return crop instanceof GrassBlock || crop instanceof NyliumBlock || crop instanceof NetherrackBlock;
	}

	/**
	 * Breaks and "harvests" a block if the player has permission to break it or there is no player
	 */
	private static void harvestBlock(World world, BlockPos pos, @Nullable ServerPlayerEntity player) {
		if (player == null || PlayerHelper.hasBreakPermission(player, pos)) {
			world.destroyBlock(pos, true, player);
		}
	}

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static int harvestVein(World world, PlayerEntity player, ItemStack stack, BlockPos pos, Block target, List<ItemStack> currentDrops, int numMined) {
		if (numMined >= Constants.MAX_VEIN_SIZE) {
			return numMined;
		}
		AxisAlignedBB b = new AxisAlignedBB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		for (BlockPos currentPos : getPositionsFromBox(b)) {
			BlockState currentState = world.getBlockState(currentPos);
			if (currentState.getBlock() == target) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				currentPos = currentPos.toImmutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, currentPos)) {
					numMined++;
					currentDrops.addAll(Block.getDrops(currentState, (ServerWorld) world, currentPos, getTileEntity(world, currentPos), player, stack));
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

	public static void igniteNearby(World world, PlayerEntity player) {
		for (BlockPos pos : BlockPos.getAllInBoxMutable(player.getPosition().add(-8, -5, -8), player.getPosition().add(8, 5, 8))) {
			if (world.rand.nextInt(128) == 0 && world.isAirBlock(pos)) {
				PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) player, pos.toImmutable(), Blocks.FIRE.getDefaultState());
			}
		}
	}

	private static boolean validRepelEntity(Entity entity, ITag<EntityType<?>> blacklistTag) {
		if (!entity.isSpectator() && !entity.getType().isContained(blacklistTag)) {
			if (entity instanceof ProjectileEntity) {
				//Accept any projectile's that are not in the ground, but fail for ones that are in the ground
				return !entity.isOnGround();
			}
			return entity instanceof MobEntity;
		}
		return false;
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given point
	 */
	public static void repelEntitiesInterdiction(World world, AxisAlignedBB effectBounds, double x, double y, double z) {
		Vector3d vec = new Vector3d(x, y, z);
		Predicate<Entity> repelPredicate = ProjectEConfig.server.effects.interdictionMode.get() ? INTERDICTION_REPEL_HOSTILE_PREDICATE : INTERDICTION_REPEL_PREDICATE;
		for (Entity ent : world.getEntitiesWithinAABB(Entity.class, effectBounds, repelPredicate)) {
			repelEntity(vec, ent);
		}
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given player, if the player is not the thrower of the projectile
	 */
	public static void repelEntitiesSWRG(World world, AxisAlignedBB effectBounds, PlayerEntity player) {
		Vector3d playerVec = player.getPositionVec();
		for (Entity ent : world.getEntitiesWithinAABB(Entity.class, effectBounds, SWRG_REPEL_PREDICATE)) {
			if (ent instanceof ProjectileEntity) {
				Entity owner = ((ProjectileEntity) ent).func_234616_v_();
				//Note: Eventually we would like to remove the check for if the world is remote and the thrower is null, but
				// it is needed to make sure it renders properly for when a player throws an ender pearl, or other throwable
				// as the client doesn't know the owner of things like ender pearls and thus renders it improperly
				if (world.isRemote() && owner == null || owner != null && player.getUniqueID().equals(owner.getUniqueID())) {
					continue;
				}
			}
			repelEntity(playerVec, ent);
		}
	}

	private static void repelEntity(Vector3d vec, Entity entity) {
		Vector3d t = new Vector3d(entity.getPosX(), entity.getPosY(), entity.getPosZ());
		Vector3d r = new Vector3d(t.x - vec.x, t.y - vec.y, t.z - vec.z);
		double distance = vec.distanceTo(t) + 0.1;
		entity.setMotion(entity.getMotion().add(r.scale(1 / 1.5 * 1 / distance)));
	}

	@Nonnull
	public static ActionResultType igniteBlock(ItemUseContext ctx) {
		//TODO: Allow this to light fires and stuff as well?
		PlayerEntity player = ctx.getPlayer();
		if (player == null) {
			return ActionResultType.FAIL;
		}
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		Direction side = ctx.getFace();
		BlockState state = world.getBlockState(pos);
		if (AbstractFireBlock.canLightBlock(world, pos, side)) {
			if (!world.isRemote && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				world.setBlockState(pos, AbstractFireBlock.getFireForPlacement(world, pos));
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		} else if (CampfireBlock.canBeLit(state)) {
			if (!world.isRemote && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				world.setBlockState(pos, state.with(BlockStateProperties.LIT, true));
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		} else if (state.isFlammable(world, pos, side)) {
			if (!world.isRemote && PlayerHelper.hasBreakPermission((ServerPlayerEntity) player, pos)) {
				// Ignite the block
				state.catchFire(world, pos, side, player);
				if (state.getBlock() instanceof TNTBlock) {
					world.removeBlock(pos, false);
				}
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		} else {
			return ActionResultType.PASS;
		}
		return ActionResultType.SUCCESS;
	}

	/**
	 * Checks if a position is in bounds of the world, and is loaded
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @return True if the position is loaded or the given world is of a superclass of IWorldReader that does not have a concept of being loaded.
	 *
	 * @implNote From Mekanism
	 */
	public static boolean isBlockLoaded(@Nullable IBlockReader world, @Nonnull BlockPos pos) {
		if (world == null || !World.isValid(pos)) {
			return false;
		} else if (world instanceof IWorldReader) {
			//Note: We don't bother checking if it is a world and then isBlockPresent because
			// all that does is also validate the y value is in bounds, and we already check to make
			// sure the position is valid both in the y and xz directions
			return ((IWorldReader) world).isBlockLoaded(pos);
		}
		return true;
	}

	/**
	 * Gets a tile entity if the location is loaded
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @return tile entity if found, null if either not found or not loaded
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static TileEntity getTileEntity(@Nullable IBlockReader world, @Nonnull BlockPos pos) {
		if (!isBlockLoaded(world, pos)) {
			//If the world is null or its a world reader and the block is not loaded, return null
			return null;
		}
		return world.getTileEntity(pos);
	}

	/**
	 * Gets a tile entity if the location is loaded
	 *
	 * @param clazz Class type of the TileEntity we expect to be in the position
	 * @param world world
	 * @param pos   position
	 *
	 * @return tile entity if found, null if either not found, not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <T extends TileEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable IBlockReader world, @Nonnull BlockPos pos) {
		return getTileEntity(clazz, world, pos, false);
	}

	/**
	 * Gets a tile entity if the location is loaded
	 *
	 * @param clazz        Class type of the TileEntity we expect to be in the position
	 * @param world        world
	 * @param pos          position
	 * @param logWrongType Whether or not an error should be logged if a tile of a different type is found at the position
	 *
	 * @return tile entity if found, null if either not found or not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <T extends TileEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable IBlockReader world, @Nonnull BlockPos pos, boolean logWrongType) {
		TileEntity tile = getTileEntity(world, pos);
		if (tile == null) {
			return null;
		}
		if (clazz.isInstance(tile)) {
			return clazz.cast(tile);
		} else if (logWrongType) {
			PECore.LOGGER.warn("Unexpected TileEntity class at {}, expected {}, but found: {}", pos, clazz, tile.getClass());
		}
		return null;
	}
}