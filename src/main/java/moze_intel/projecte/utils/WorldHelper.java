package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.NetherSproutsBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.NetherrackBlock;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.block.RootsBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.fluids.IFluidBlock;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for anything that touches a World. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper {

	private static final Predicate<Entity> SWRG_REPEL_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_SWRG);
	private static final Predicate<Entity> INTERDICTION_REPEL_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_INTERDICTION);
	//Note: We don't need to check if the projectile entity is on the ground here or not, as if it is we would not get past validRepelEntity
	private static final Predicate<Entity> INTERDICTION_REPEL_HOSTILE_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_INTERDICTION) &&
																							(entity instanceof Enemy || entity instanceof Projectile);

	public static void createLootDrop(List<ItemStack> drops, Level level, BlockPos pos) {
		createLootDrop(drops, level, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void createLootDrop(List<ItemStack> drops, Level level, double x, double y, double z) {
		if (!drops.isEmpty()) {
			ItemHelper.compactItemListNoStacksize(drops);
			for (ItemStack drop : drops) {
				level.addFreshEntity(new ItemEntity(level, x, y, z, drop));
			}
		}
	}

	/**
	 * Equivalent of World.newExplosion
	 */
	public static void createNovaExplosion(Level level, Entity exploder, double x, double y, double z, float power) {
		NovaExplosion explosion = new NovaExplosion(level, exploder, x, y, z, power, Explosion.BlockInteraction.DESTROY);
		if (!NeoForge.EVENT_BUS.post(new ExplosionEvent.Start(level, explosion)).isCanceled()) {
			explosion.explode();
			explosion.finalizeExplosion(true);
		}
	}

	public static void drainFluid(@Nullable Player player, Level level, BlockPos pos, BlockState state, Fluid toMatch) {
		Block block = state.getBlock();
		if (block instanceof IFluidBlock fluidBlock && fluidBlock.getFluid().isSame(toMatch)) {
			//If it is a fluid block drain it (may be the case for some custom block?)
			// We double check though the fluid block represents a given one though, in case there is some weird thing
			// going on and we are a bucket pickup handler for the actual water and fluid state
			fluidBlock.drain(level, pos, FluidAction.EXECUTE);
		} else if (block instanceof BucketPickup bucketPickup) {
			//If it is a bucket pickup handler (so may be a fluid logged block) "pick it up"
			// This includes normal fluid blocks
			bucketPickup.pickupBlock(player, level, pos, state);
		}
	}

	public static void dropInventory(@Nullable IItemHandler inv, Level level, BlockPos pos) {
		if (inv != null) {
			for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty()) {
					level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
				}
			}
		}
	}

	public static void extinguishNearby(Level level, Player player) {
		BlockPos.betweenClosedStream(player.blockPosition().offset(-1, -1, -1), player.blockPosition().offset(1, 1, 1)).forEach(pos -> {
			pos = pos.immutable();
			if (level.getBlockState(pos).getBlock() == Blocks.FIRE && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				level.removeBlock(pos, false);
			}
		});
	}

	public static void freezeInBoundingBox(Level level, AABB box, Player player, boolean random) {
		for (BlockPos pos : getPositionsFromBox(box)) {
			BlockState state = level.getBlockState(pos);
			Block b = state.getBlock();
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.immutable();
			if (b == Blocks.WATER && (!random || level.random.nextInt(128) == 0)) {
				if (player != null) {
					PlayerHelper.checkedReplaceBlock((ServerPlayer) player, pos, Blocks.ICE.defaultBlockState());
				} else {
					level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
				}
			} else if (Block.isFaceFull(state.getCollisionShape(level, pos.below()), Direction.UP)) {
				BlockPos up = pos.above();
				BlockState stateUp = level.getBlockState(up);
				BlockState newState = null;

				if (stateUp.isAir() && (!random || level.random.nextInt(128) == 0)) {
					newState = Blocks.SNOW.defaultBlockState();
				} else if (stateUp.getBlock() == Blocks.SNOW && stateUp.getValue(SnowLayerBlock.LAYERS) < 8 && level.random.nextInt(512) == 0) {
					newState = stateUp.setValue(SnowLayerBlock.LAYERS, stateUp.getValue(SnowLayerBlock.LAYERS) + 1);
				}
				if (newState != null) {
					if (player != null) {
						PlayerHelper.checkedReplaceBlock((ServerPlayer) player, up, newState);
					} else {
						level.setBlockAndUpdate(up, newState);
					}
				}
			}
		}
	}

	/**
	 * Checks if a block is a {@link LiquidBlockContainer} that supports a specific fluid type.
	 */
	public static boolean isLiquidContainerForFluid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(player, level, pos, state, fluid);
	}

	/**
	 * Attempts to place a fluid in a specific spot if the spot is a {@link LiquidBlockContainer} that supports the fluid otherwise try to place it in the block that is
	 * on the given side of the clicked block.
	 */
	public static void placeFluid(@Nullable ServerPlayer player, Level level, BlockPos pos, Direction sideHit, FlowingFluid fluid, boolean checkWaterVaporize) {
		if (isLiquidContainerForFluid(player, level, pos, level.getBlockState(pos), fluid)) {
			//If the spot can be logged with our fluid then try using the position directly
			placeFluid(player, level, pos, fluid, checkWaterVaporize);
		} else {
			//Otherwise offset it because we clicked against the block
			placeFluid(player, level, pos.relative(sideHit), fluid, checkWaterVaporize);
		}
	}

	/**
	 * Attempts to place a fluid in a specific spot, if the spot is a {@link LiquidBlockContainer} that supports the fluid, insert it instead.
	 *
	 * @apiNote Call this from the server side
	 */
	public static void placeFluid(@Nullable ServerPlayer player, Level level, BlockPos pos, FlowingFluid fluid, boolean checkWaterVaporize) {
		BlockState blockState = level.getBlockState(pos);
		if (checkWaterVaporize && level.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
			level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
			for (int l = 0; l < 8; ++l) {
				level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
			}
		} else if (isLiquidContainerForFluid(player, level, pos, blockState, fluid)) {
			((LiquidBlockContainer) blockState.getBlock()).placeLiquid(level, pos, blockState, fluid.getSource(false));
			level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
		} else {
			//Based on BucketItem#emptyContents
			if (blockState.canBeReplaced(fluid) && !blockState.liquid()) {
				level.destroyBlock(pos, true);
			}
			if (player == null) {
				level.setBlockAndUpdate(pos, fluid.defaultFluidState().createLegacyBlock());
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			} else if (PlayerHelper.checkedPlaceBlock(player, pos, fluid.defaultFluidState().createLegacyBlock())) {
				level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
			}
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AABB getBroadDeepBox(BlockPos pos, Direction direction, int offset) {
		return switch (direction) {
			case EAST -> new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX(), pos.getY() + offset, pos.getZ() + offset);
			case WEST -> new AABB(pos.getX(), pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case UP -> new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
			case DOWN -> new AABB(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
			case SOUTH -> new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ());
			case NORTH -> new AABB(pos.getX() - offset, pos.getY() - offset, pos.getZ(), pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
		};
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AABB getDeepBox(BlockPos pos, Direction direction, int depth) {
		return switch (direction) {
			case EAST -> new AABB(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			case WEST -> new AABB(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
			case UP -> new AABB(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			case DOWN -> new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
			case SOUTH -> new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
			case NORTH -> new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
		};
	}

	/**
	 * Returns in AABB that is always a single block deep but is size x size orthogonal to the side hit
	 */
	public static AABB getBroadBox(BlockPos pos, Direction direction, int size) {
		return switch (direction) {
			case EAST, WEST -> new AABB(pos.getX(), pos.getY() - size, pos.getZ() - size, pos.getX(), pos.getY() + size, pos.getZ() + size);
			case UP, DOWN -> new AABB(pos.getX() - size, pos.getY(), pos.getZ() - size, pos.getX() + size, pos.getY(), pos.getZ() + size);
			case SOUTH, NORTH -> new AABB(pos.getX() - size, pos.getY() - size, pos.getZ(), pos.getX() + size, pos.getY() + size, pos.getZ());
		};
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
		return getPositionsFromBox(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ));
	}

	/**
	 * Wrapper around BlockPos.getAllInBox()
	 */
	public static Iterable<BlockPos> getPositionsFromBox(BlockPos corner1, BlockPos corner2) {
		return () -> BlockPos.betweenClosedStream(corner1, corner2).iterator();
	}


	public static List<BlockEntity> getBlockEntitiesWithinAABB(Level level, AABB bBox) {
		List<BlockEntity> list = new ArrayList<>();
		for (BlockPos pos : getPositionsFromBox(bBox)) {
			BlockEntity blockEntity = getBlockEntity(level, pos);
			if (blockEntity != null) {
				list.add(blockEntity);
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
			ent.addDeltaMovement(new Vec3(dX / dist * vel * 0.1, dY / dist * vel * 0.2, dZ / dist * vel * 0.1));
		}
	}

	public static void growNearbyRandomly(boolean harvest, Level level, BlockPos pos, Player player) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		boolean grewWater = false;
		int chance = harvest ? 16 : 32;
		for (BlockPos currentPos : getPositionsFromBox(pos.offset(-5, -3, -5), pos.offset(5, 3, 5))) {
			currentPos = currentPos.immutable();
			BlockState state = serverLevel.getBlockState(currentPos);
			Block crop = state.getBlock();

			// Vines, leaves, tallgrass, deadbush, doubleplants
			if (crop instanceof IShearable || crop instanceof FlowerBlock || crop instanceof DoublePlantBlock ||
				crop instanceof RootsBlock || crop instanceof NetherSproutsBlock || crop instanceof HangingRootsBlock) {
				if (harvest) {
					harvestBlock(serverLevel, currentPos, player);
				}
			}
			// Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
			// Mushroom, potato, sapling, stems, tallgrass
			else if (crop instanceof BonemealableBlock growable) {
				if (!growable.isValidBonemealTarget(serverLevel, currentPos, state)) {
					if (harvest && !state.is(PETags.Blocks.BLACKLIST_HARVEST)) {
						if (!leaveBottomBlock(crop) || serverLevel.getBlockState(currentPos.below()).is(crop)) {
							//Don't harvest the bottom of kelp but otherwise allow harvesting them
							harvestBlock(serverLevel, currentPos, player);
						}
					}
				} else if (ProjectEConfig.server.items.harvBandGrass.get() || !isGrassLikeBlock(crop)) {
					if (serverLevel.random.nextInt(chance) == 0) {
						growable.performBonemeal(serverLevel, serverLevel.random, currentPos, state);
						level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
					}
				}
			}
			// All modded
			// Cactus, Reeds, Netherwart, Flower
			else if (crop instanceof IPlantable) {
				if (serverLevel.random.nextInt(chance / 4) == 0) {
					for (int i = 0; i < (harvest ? 8 : 4); i++) {
						state.randomTick(serverLevel, currentPos, serverLevel.random);
					}
				}
				if (harvest) {
					if (crop == Blocks.SUGAR_CANE || crop == Blocks.CACTUS) {
						if (serverLevel.getBlockState(currentPos.above()).is(crop) && serverLevel.getBlockState(currentPos.above(2)).is(crop)) {
							for (int i = crop == Blocks.SUGAR_CANE ? 1 : 0; i < 3; i++) {
								harvestBlock(serverLevel, currentPos.above(i), player);
							}
						}
					} else if (crop == Blocks.NETHER_WART) {
						if (state.getValue(NetherWartBlock.AGE) == 3) {
							harvestBlock(serverLevel, currentPos, player);
						}
					}
				}
			}
			// Generic water plants
			else if (!grewWater && serverLevel.random.nextInt(512) == 0 && growWaterPlant(serverLevel, currentPos, state, null)) {
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
				grewWater = true;
			}
		}
	}

	private static boolean leaveBottomBlock(Block crop) {
		return crop == Blocks.KELP_PLANT || crop == Blocks.BAMBOO;
	}

	private static boolean isGrassLikeBlock(Block crop) {
		//Note: We count netherrack like a grass like block as it propagates growing to neighboring nylium blocks
		// and its can grow methods behave like one
		return crop instanceof GrassBlock || crop instanceof NyliumBlock || crop instanceof NetherrackBlock || crop instanceof MossBlock;
	}

	/**
	 * Breaks and "harvests" a block if the player has permission to break it or there is no player
	 */
	private static void harvestBlock(Level level, BlockPos pos, @Nullable Player player) {
		if (!(player instanceof ServerPlayer serverPlayer) || PlayerHelper.hasBreakPermission(serverPlayer, pos)) {
			level.destroyBlock(pos, true, player);
		}
	}

	//[VanillaCopy] slightly modified version of BoneMealItem#growWaterPlant
	public static boolean growWaterPlant(ServerLevel level, BlockPos pos, BlockState state, @Nullable Direction side) {
		boolean success = false;
		if (state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8) {
			RandomSource random = level.getRandom();
			label76:
			for (int i = 0; i < 128; ++i) {
				BlockPos blockpos = pos;
				for (int j = 0; j < i / 16; ++j) {
					blockpos = blockpos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2,
							random.nextInt(3) - 1);
					if (level.getBlockState(blockpos).isCollisionShapeFullBlock(level, blockpos)) {
						continue label76;
					}
				}
				BlockState newState = Blocks.SEAGRASS.defaultBlockState();
				Holder<Biome> biome = level.getBiome(blockpos);
				if (biome.is(Biomes.WARM_OCEAN)) {
					if (i == 0 && side != null && side.getAxis().isHorizontal()) {
						newState = getRandomState(BlockTags.WALL_CORALS, random, newState);
						if (newState.hasProperty(BaseCoralWallFanBlock.FACING)) {
							newState = newState.setValue(BaseCoralWallFanBlock.FACING, side);
						}
					} else if (random.nextInt(4) == 0) {
						newState = getRandomState(BlockTags.UNDERWATER_BONEMEALS, random, newState);
					}
				}
				if (newState.is(BlockTags.WALL_CORALS, s -> s.hasProperty(BaseCoralWallFanBlock.FACING))) {
					for (int k = 0; !newState.canSurvive(level, blockpos) && k < 4; ++k) {
						newState = newState.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
					}
				}
				if (newState.canSurvive(level, blockpos)) {
					BlockState stateToReplace = level.getBlockState(blockpos);
					if (stateToReplace.is(Blocks.WATER) && stateToReplace.getFluidState().getAmount() == 8) {
						level.setBlockAndUpdate(blockpos, newState);
						success = true;
					} else if (stateToReplace.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
						((BonemealableBlock) Blocks.SEAGRASS).performBonemeal(level, random, blockpos, stateToReplace);
						success = true;
					}
				}
			}
		}
		return success;
	}

	private static BlockState getRandomState(TagKey<Block> key, RandomSource random, BlockState fallback) {
		return BuiltInRegistries.BLOCK.getTag(key)
				.flatMap(holderSet -> holderSet.getRandomElement(random))
				.map(holder -> holder.value().defaultBlockState())
				.orElse(fallback);
	}

	/**
	 * Recursively mines out a vein of the given Block, starting from the provided coordinates
	 */
	public static int harvestVein(Level level, Player player, ItemStack stack, BlockPos pos, Block target, List<ItemStack> currentDrops, int numMined) {
		if (numMined >= Constants.MAX_VEIN_SIZE) {
			return numMined;
		}
		AABB b = new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		for (BlockPos currentPos : getPositionsFromBox(b)) {
			BlockState currentState = level.getBlockState(currentPos);
			if (currentState.getBlock() == target) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				currentPos = currentPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, currentPos)) {
					numMined++;
					currentDrops.addAll(Block.getDrops(currentState, (ServerLevel) level, currentPos, getBlockEntity(level, currentPos), player, stack));
					level.removeBlock(currentPos, false);
					numMined = harvestVein(level, player, stack, currentPos, target, currentDrops, numMined);
					if (numMined >= Constants.MAX_VEIN_SIZE) {
						break;
					}
				}
			}
		}
		return numMined;
	}

	public static void igniteNearby(Level level, Player player) {
		for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-8, -5, -8), player.blockPosition().offset(8, 5, 8))) {
			if (level.random.nextInt(128) == 0 && level.isEmptyBlock(pos)) {
				PlayerHelper.checkedPlaceBlock((ServerPlayer) player, pos.immutable(), Blocks.FIRE.defaultBlockState());
			}
		}
	}

	private static boolean validRepelEntity(Entity entity, TagKey<EntityType<?>> blacklistTag) {
		if (!entity.isSpectator() && !entity.getType().is(blacklistTag)) {
			if (entity instanceof Projectile) {
				//Accept any projectile's that are not in the ground, but fail for ones that are in the ground
				return !entity.onGround();
			}
			return entity instanceof Mob;
		}
		return false;
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given point
	 */
	public static void repelEntitiesInterdiction(Level level, AABB effectBounds, double x, double y, double z) {
		Vec3 vec = new Vec3(x, y, z);
		Predicate<Entity> repelPredicate = ProjectEConfig.server.effects.interdictionMode.get() ? INTERDICTION_REPEL_HOSTILE_PREDICATE : INTERDICTION_REPEL_PREDICATE;
		for (Entity ent : level.getEntitiesOfClass(Entity.class, effectBounds, repelPredicate)) {
			repelEntity(vec, ent);
		}
	}

	/**
	 * Repels projectiles and mobs in the given AABB away from a given player, if the player is not the thrower of the projectile
	 */
	public static void repelEntitiesSWRG(Level level, AABB effectBounds, Player player) {
		Vec3 playerVec = player.position();
		for (Entity ent : level.getEntitiesOfClass(Entity.class, effectBounds, SWRG_REPEL_PREDICATE)) {
			if (ent instanceof Projectile projectile) {
				Entity owner = projectile.getOwner();
				//Note: Eventually we would like to remove the check for if the world is remote and the thrower is null, but
				// it is needed to make sure it renders properly for when a player throws an ender pearl, or other throwable
				// as the client doesn't know the owner of things like ender pearls and thus renders it improperly
				if (level.isClientSide() && owner == null || owner != null && player.getUUID().equals(owner.getUUID())) {
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
		entity.addDeltaMovement(r.scale(1 / 1.5 * 1 / distance));
	}

	@NotNull
	public static InteractionResult igniteBlock(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction side = ctx.getClickedFace();
		BlockState state = level.getBlockState(pos);
		if (BaseFireBlock.canBePlacedAt(level, pos, side)) {
			if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
				level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else if (CampfireBlock.canLight(state)) {
			if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
				level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else if (state.isFlammable(level, pos, side)) {
			if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
				// Ignite the block
				state.onCaughtFire(level, pos, side, player);
				if (state.getBlock() instanceof TntBlock) {
					level.removeBlock(pos, false);
				}
				level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else {
			return InteractionResult.PASS;
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
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
	public static boolean isBlockLoaded(@Nullable BlockGetter world, @NotNull BlockPos pos) {
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
	 * Gets the capability of a block at a given location if it is loaded
	 *
	 * @param level   Level
	 * @param cap     Capability to look up
	 * @param pos     position
	 * @param context Capability context
	 *
	 * @return capability if present, null if either not found or not loaded
	 */
	@Nullable
	@Contract("null, _, _, _ -> null")
	public static <CAP, CONTEXT> CAP getCapability(@Nullable Level level, BlockCapability<CAP, CONTEXT> cap, BlockPos pos, CONTEXT context) {
		return getCapability(level, cap, pos, null, null, context);
	}

	/**
	 * Gets the capability of a block at a given location if it is loaded
	 *
	 * @param level       Level
	 * @param cap         Capability to look up
	 * @param pos         position
	 * @param state       the block state, if known, or {@code null} if unknown
	 * @param blockEntity the block entity, if known, or {@code null} if unknown
	 * @param context     Capability context
	 *
	 * @return capability if present, null if either not found or not loaded
	 */
	@Nullable
	@Contract("null, _, _, _, _, _ -> null")
	public static <CAP, CONTEXT> CAP getCapability(@Nullable Level level, BlockCapability<CAP, CONTEXT> cap, BlockPos pos, @Nullable BlockState state,
			@Nullable BlockEntity blockEntity, CONTEXT context) {
		if (!isBlockLoaded(level, pos)) {
			//If the world is null, or it is a world reader and the block is not loaded, return null
			return null;
		}
		return level.getCapability(cap, pos, state, blockEntity, context);
	}

	/**
	 * Gets a block entity if the location is loaded
	 *
	 * @param level world
	 * @param pos   position
	 *
	 * @return block entity if found, null if either not found or not loaded
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static BlockEntity getBlockEntity(@Nullable BlockGetter level, @NotNull BlockPos pos) {
		if (!isBlockLoaded(level, pos)) {
			//If the world is null or its a world reader and the block is not loaded, return null
			return null;
		}
		return level.getBlockEntity(pos);
	}

	/**
	 * Gets a block entity if the location is loaded
	 *
	 * @param clazz Class type of the block entity we expect to be in the position
	 * @param level world
	 * @param pos   position
	 *
	 * @return block entity if found, null if either not found, not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <BE extends BlockEntity> BE getBlockEntity(@NotNull Class<BE> clazz, @Nullable BlockGetter level, @NotNull BlockPos pos) {
		return getBlockEntity(clazz, level, pos, false);
	}

	/**
	 * Gets a block entity if the location is loaded
	 *
	 * @param clazz        Class type of the block entity we expect to be in the position
	 * @param level        world
	 * @param pos          position
	 * @param logWrongType Whether or not an error should be logged if a block entity of a different type is found at the position
	 *
	 * @return block entity if found, null if either not found or not loaded, or of the wrong type
	 *
	 * @implNote From Mekanism
	 */
	@Nullable
	public static <BE extends BlockEntity> BE getBlockEntity(@NotNull Class<BE> clazz, @Nullable BlockGetter level, @NotNull BlockPos pos, boolean logWrongType) {
		BlockEntity blockEntity = getBlockEntity(level, pos);
		if (blockEntity == null) {
			return null;
		}
		if (clazz.isInstance(blockEntity)) {
			return clazz.cast(blockEntity);
		} else if (logWrongType) {
			PECore.LOGGER.warn("Unexpected block entity class at {}, expected {}, but found: {}", pos, clazz, blockEntity.getClass());
		}
		return null;
	}
}