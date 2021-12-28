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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RootsBlock;
import net.minecraft.world.level.block.NetherSproutsBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.NetherrackBlock;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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
																							(entity instanceof Enemy || entity instanceof Projectile);

	public static void createLootDrop(List<ItemStack> drops, Level world, BlockPos pos) {
		createLootDrop(drops, world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void createLootDrop(List<ItemStack> drops, Level world, double x, double y, double z) {
		if (!drops.isEmpty()) {
			ItemHelper.compactItemListNoStacksize(drops);
			for (ItemStack drop : drops) {
				world.addFreshEntity(new ItemEntity(world, x, y, z, drop));
			}
		}
	}

	/**
	 * Equivalent of World.newExplosion
	 */
	public static void createNovaExplosion(Level world, Entity exploder, double x, double y, double z, float power) {
		NovaExplosion explosion = new NovaExplosion(world, exploder, x, y, z, power, true, Explosion.BlockInteraction.BREAK);
		if (!MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion))) {
			explosion.explode();
			explosion.finalizeExplosion(true);
		}
	}

	public static void drainFluid(Level world, BlockPos pos, BlockState state, Fluid toMatch) {
		Block block = state.getBlock();
		if (block instanceof IFluidBlock && ((IFluidBlock) block).getFluid().isSame(toMatch)) {
			//If it is a fluid block drain it (may be the case for some custom block?)
			// We double check though the fluid block represents a given one though, in case there is some weird thing
			// going on and we are a bucket pickup handler for the actual water and fluid state
			((IFluidBlock) block).drain(world, pos, FluidAction.EXECUTE);
		} else if (block instanceof BucketPickup) {
			//If it is a bucket pickup handler (so may be a fluid logged block) "pick it up"
			// This includes normal fluid blocks
			((BucketPickup) block).pickupBlock(world, pos, state);
		}
	}

	public static void dropInventory(IItemHandler inv, Level world, BlockPos pos) {
		if (inv == null) {
			return;
		}
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack));
			}
		}
	}

	public static void extinguishNearby(Level world, Player player) {
		BlockPos.betweenClosedStream(player.blockPosition().offset(-1, -1, -1), player.blockPosition().offset(1, 1, 1)).forEach(pos -> {
			pos = pos.immutable();
			if (world.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				world.removeBlock(pos, false);
			}
		});
	}

	public static void freezeInBoundingBox(Level world, AABB box, Player player, boolean random) {
		for (BlockPos pos : getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.immutable();
			if (b == Blocks.WATER && (!random || world.random.nextInt(128) == 0)) {
				if (player != null) {
					PlayerHelper.checkedReplaceBlock((ServerPlayer) player, pos, Blocks.ICE.defaultBlockState());
				} else {
					world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
				}
			} else if (Block.isFaceFull(state.getCollisionShape(world, pos.below()), Direction.UP)) {
				BlockPos up = pos.above();
				BlockState stateUp = world.getBlockState(up);
				BlockState newState = null;

				if (stateUp.isAir() && (!random || world.random.nextInt(128) == 0)) {
					newState = Blocks.SNOW.defaultBlockState();
				} else if (stateUp.getBlock() == Blocks.SNOW && stateUp.getValue(SnowLayerBlock.LAYERS) < 8 && world.random.nextInt(512) == 0) {
					newState = stateUp.setValue(SnowLayerBlock.LAYERS, stateUp.getValue(SnowLayerBlock.LAYERS) + 1);
				}
				if (newState != null) {
					if (player != null) {
						PlayerHelper.checkedReplaceBlock((ServerPlayer) player, up, newState);
					} else {
						world.setBlockAndUpdate(up, newState);
					}
				}
			}
		}
	}

	/**
	 * Checks if a block is a {@link LiquidBlockContainer} that supports a specific fluid type.
	 */
	public static boolean isLiquidContainerForFluid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) state.getBlock()).canPlaceLiquid(world, pos, state, fluid);
	}

	/**
	 * Attempts to place a fluid in a specific spot if the spot is a {@link LiquidBlockContainer} that supports the fluid otherwise try to place it in the block that is on
	 * the given side of the clicked block.
	 */
	public static void placeFluid(@Nullable ServerPlayer player, Level world, BlockPos pos, Direction sideHit, FlowingFluid fluid, boolean checkWaterVaporize) {
		if (isLiquidContainerForFluid(world, pos, world.getBlockState(pos), fluid)) {
			//If the spot can be logged with our fluid then try using the position directly
			placeFluid(player, world, pos, fluid, checkWaterVaporize);
		} else {
			//Otherwise offset it because we clicked against the block
			placeFluid(player, world, pos.relative(sideHit), fluid, checkWaterVaporize);
		}
	}

	/**
	 * Attempts to place a fluid in a specific spot, if the spot is a {@link LiquidBlockContainer} that supports the fluid, insert it instead.
	 *
	 * @apiNote Call this from the server side
	 */
	public static void placeFluid(@Nullable ServerPlayer player, Level world, BlockPos pos, FlowingFluid fluid, boolean checkWaterVaporize) {
		BlockState blockState = world.getBlockState(pos);
		if (checkWaterVaporize && world.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
			world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
			for (int l = 0; l < 8; ++l) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
			}
		} else if (isLiquidContainerForFluid(world, pos, blockState, fluid)) {
			((LiquidBlockContainer) blockState.getBlock()).placeLiquid(world, pos, blockState, fluid.getSource(false));
		} else {
			Material material = blockState.getMaterial();
			if ((!material.isSolid() || material.isReplaceable()) && !material.isLiquid()) {
				world.destroyBlock(pos, true);
			}
			if (player == null) {
				world.setBlockAndUpdate(pos, fluid.defaultFluidState().createLegacyBlock());
			} else {
				PlayerHelper.checkedPlaceBlock(player, pos, fluid.defaultFluidState().createLegacyBlock());
			}
		}
	}

	/**
	 * Gets an ItemHandler of a specific tile from the given side. Falls back to using wrappers if the tile is an instance of an ISidedInventory/IInventory.
	 */
	@Nullable
	public static IItemHandler getItemHandler(@Nonnull BlockEntity tile, @Nullable Direction direction) {
		Optional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve();
		if (capability.isPresent()) {
			return capability.get();
		} else if (tile instanceof WorldlyContainer) {
			return new SidedInvWrapper((WorldlyContainer) tile, direction);
		} else if (tile instanceof Container) {
			return new InvWrapper((Container) tile);
		}
		return null;
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AABB getBroadDeepBox(BlockPos pos, Direction direction, int offset) {
		switch (direction) {
			case EAST:
				return new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX(), pos.getY() + offset, pos.getZ() + offset);
			case WEST:
				return new AABB(pos.getX(), pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case UP:
				return new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
			case DOWN:
				return new AABB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case SOUTH:
				return new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ());
			case NORTH:
				return new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ(), pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			default:
				return new AABB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AABB getDeepBox(BlockPos pos, Direction direction, int depth) {
		switch (direction) {
			case EAST:
				return new AABB(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			case WEST:
				return new AABB(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
			case UP:
				return new AABB(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			case DOWN:
				return new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
			case SOUTH:
				return new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
			case NORTH:
				return new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
			default:
				return new AABB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Returns in AABB that is always a single block deep but is size x size orthogonal to the side hit
	 */
	public static AABB getBroadBox(BlockPos pos, Direction direction, int size) {
		switch (direction) {
			case EAST:
			case WEST:
				return new AABB(pos.getX(), pos.getY() - size, pos.getZ() - size, pos.getX(), pos.getY() + size, pos.getZ() + size);
			case UP:
			case DOWN:
				return new AABB(pos.getX() - size, pos.getY(), pos.getZ() - size, pos.getX() + size, pos.getY(), pos.getZ() + size);
			case SOUTH:
			case NORTH:
				return new AABB(pos.getX() - size, pos.getY() - size, pos.getZ(), pos.getX() + size, pos.getY() + size, pos.getZ());
			default:
				return new AABB(0, 0, 0, 0, 0, 0);
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box. Y level remains constant. As such, a direction hit is unneeded.
	 */
	public static AABB getFlatYBox(BlockPos pos, int offset) {
		return new AABB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
	}

	/**
	 * Wrapper around BlockPos.getAllInBox() with an AABB Note that this is inclusive of all positions in the AABB!
	 */
	public static Iterable<BlockPos> getPositionsFromBox(AABB box) {
		return getPositionsFromBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
	}

	/**
	 * Wrapper around BlockPos.getAllInBox()
	 */
	public static Iterable<BlockPos> getPositionsFromBox(BlockPos corner1, BlockPos corner2) {
		return () -> BlockPos.betweenClosedStream(corner1, corner2).iterator();
	}


	public static List<BlockEntity> getTileEntitiesWithinAABB(Level world, AABB bBox) {
		List<BlockEntity> list = new ArrayList<>();
		for (BlockPos pos : getPositionsFromBox(bBox)) {
			BlockEntity tile = getTileEntity(world, pos);
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
		double dX = x - ent.getX();
		double dY = y - ent.getY();
		double dZ = z - ent.getZ();
		double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

		double vel = 1.0 - dist / 15.0;
		if (vel > 0.0D) {
			vel *= vel;
			ent.setDeltaMovement(ent.getDeltaMovement().add(dX / dist * vel * 0.1, dY / dist * vel * 0.2, dZ / dist * vel * 0.1));
		}
	}

	public static void growNearbyRandomly(boolean harvest, Level world, BlockPos pos, Player player) {
		if (!(world instanceof ServerLevel)) {
			return;
		}
		int chance = harvest ? 16 : 32;
		for (BlockPos currentPos : getPositionsFromBox(pos.offset(-5, -3, -5), pos.offset(5, 3, 5))) {
			currentPos = currentPos.immutable();
			BlockState state = world.getBlockState(currentPos);
			Block crop = state.getBlock();

			// Vines, leaves, tallgrass, deadbush, doubleplants
			if (crop instanceof IForgeShearable || crop instanceof FlowerBlock || crop instanceof DoublePlantBlock ||
				crop instanceof RootsBlock || crop instanceof NetherSproutsBlock) {
				if (harvest) {
					harvestBlock(world, currentPos, (ServerPlayer) player);
				}
			}
			// Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
			// Mushroom, potato, sapling, stems, tallgrass
			else if (crop instanceof BonemealableBlock) {
				BonemealableBlock growable = (BonemealableBlock) crop;
				if (!growable.isValidBonemealTarget(world, currentPos, state, false)) {
					if (harvest && !PETags.Blocks.BLACKLIST_HARVEST.contains(crop)) {
						if (crop != Blocks.KELP_PLANT || world.getBlockState(currentPos.below()).is(crop)) {
							//Don't harvest the bottom of help but otherwise allow harvesting them
							harvestBlock(world, currentPos, (ServerPlayer) player);
						}
					}
				} else if (ProjectEConfig.server.items.harvBandGrass.get() || !isGrassLikeBlock(crop)) {
					if (world.random.nextInt(chance) == 0) {
						growable.performBonemeal((ServerLevel) world, world.random, currentPos, state);
					}
				}
			}
			// All modded
			// Cactus, Reeds, Netherwart, Flower
			else if (crop instanceof IPlantable) {
				if (world.random.nextInt(chance / 4) == 0) {
					for (int i = 0; i < (harvest ? 8 : 4); i++) {
						state.randomTick((ServerLevel) world, currentPos, world.random);
					}
				}
				if (harvest) {
					if (crop == Blocks.SUGAR_CANE || crop == Blocks.CACTUS) {
						if (world.getBlockState(currentPos.above()).is(crop) && world.getBlockState(currentPos.above(2)).is(crop)) {
							for (int i = crop == Blocks.SUGAR_CANE ? 1 : 0; i < 3; i++) {
								harvestBlock(world, currentPos.above(i), (ServerPlayer) player);
							}
						}
					} else if (crop == Blocks.NETHER_WART) {
						if (state.getValue(NetherWartBlock.AGE) == 3) {
							harvestBlock(world, currentPos, (ServerPlayer) player);
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
	private static void harvestBlock(Level world, BlockPos pos, @Nullable ServerPlayer player) {
		if (player == null || PlayerHelper.hasBreakPermission(player, pos)) {
			world.destroyBlock(pos, true, player);
		}
	}

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static int harvestVein(Level world, Player player, ItemStack stack, BlockPos pos, Block target, List<ItemStack> currentDrops, int numMined) {
		if (numMined >= Constants.MAX_VEIN_SIZE) {
			return numMined;
		}
		AABB b = new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		for (BlockPos currentPos : getPositionsFromBox(b)) {
			BlockState currentState = world.getBlockState(currentPos);
			if (currentState.getBlock() == target) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				currentPos = currentPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, currentPos)) {
					numMined++;
					currentDrops.addAll(Block.getDrops(currentState, (ServerLevel) world, currentPos, getTileEntity(world, currentPos), player, stack));
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

	public static void igniteNearby(Level world, Player player) {
		for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-8, -5, -8), player.blockPosition().offset(8, 5, 8))) {
			if (world.random.nextInt(128) == 0 && world.isEmptyBlock(pos)) {
				PlayerHelper.checkedPlaceBlock((ServerPlayer) player, pos.immutable(), Blocks.FIRE.defaultBlockState());
			}
		}
	}

	private static boolean validRepelEntity(Entity entity, Tag<EntityType<?>> blacklistTag) {
		if (!entity.isSpectator() && !entity.getType().is(blacklistTag)) {
			if (entity instanceof Projectile) {
				//Accept any projectile's that are not in the ground, but fail for ones that are in the ground
				return !entity.isOnGround();
			}
			return entity instanceof Mob;
		}
		return false;
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given point
	 */
	public static void repelEntitiesInterdiction(Level world, AABB effectBounds, double x, double y, double z) {
		Vec3 vec = new Vec3(x, y, z);
		Predicate<Entity> repelPredicate = ProjectEConfig.server.effects.interdictionMode.get() ? INTERDICTION_REPEL_HOSTILE_PREDICATE : INTERDICTION_REPEL_PREDICATE;
		for (Entity ent : world.getEntitiesOfClass(Entity.class, effectBounds, repelPredicate)) {
			repelEntity(vec, ent);
		}
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given player, if the player is not the thrower of the projectile
	 */
	public static void repelEntitiesSWRG(Level world, AABB effectBounds, Player player) {
		Vec3 playerVec = player.position();
		for (Entity ent : world.getEntitiesOfClass(Entity.class, effectBounds, SWRG_REPEL_PREDICATE)) {
			if (ent instanceof Projectile) {
				Entity owner = ((Projectile) ent).getOwner();
				//Note: Eventually we would like to remove the check for if the world is remote and the thrower is null, but
				// it is needed to make sure it renders properly for when a player throws an ender pearl, or other throwable
				// as the client doesn't know the owner of things like ender pearls and thus renders it improperly
				if (world.isClientSide() && owner == null || owner != null && player.getUUID().equals(owner.getUUID())) {
					continue;
				}
			}
			repelEntity(playerVec, ent);
		}
	}

	private static void repelEntity(Vec3 vec, Entity entity) {
		Vec3 t = new Vec3(entity.getX(), entity.getY(), entity.getZ());
		Vec3 r = new Vec3(t.x - vec.x, t.y - vec.y, t.z - vec.z);
		double distance = vec.distanceTo(t) + 0.1;
		entity.setDeltaMovement(entity.getDeltaMovement().add(r.scale(1 / 1.5 * 1 / distance)));
	}

	@Nonnull
	public static InteractionResult igniteBlock(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction side = ctx.getClickedFace();
		BlockState state = world.getBlockState(pos);
		if (BaseFireBlock.canBePlacedAt(world, pos, side)) {
			if (!world.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				world.setBlockAndUpdate(pos, BaseFireBlock.getState(world, pos));
				world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else if (CampfireBlock.canLight(state)) {
			if (!world.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
				world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else if (state.isFlammable(world, pos, side)) {
			if (!world.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				// Ignite the block
				state.onCaughtFire(world, pos, side, player);
				if (state.getBlock() instanceof TntBlock) {
					world.removeBlock(pos, false);
				}
				world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else {
			return InteractionResult.PASS;
		}
		return InteractionResult.SUCCESS;
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
	public static boolean isBlockLoaded(@Nullable BlockGetter world, @Nonnull BlockPos pos) {
		if (world == null) {
			return false;
		} else if (world instanceof LevelReader reader) {
			if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
				return false;
			}
			return reader.hasChunkAt(pos);
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
	public static BlockEntity getTileEntity(@Nullable BlockGetter world, @Nonnull BlockPos pos) {
		if (!isBlockLoaded(world, pos)) {
			//If the world is null or its a world reader and the block is not loaded, return null
			return null;
		}
		return world.getBlockEntity(pos);
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
	public static <T extends BlockEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable BlockGetter world, @Nonnull BlockPos pos) {
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
	public static <T extends BlockEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable BlockGetter world, @Nonnull BlockPos pos, boolean logWrongType) {
		BlockEntity tile = getTileEntity(world, pos);
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