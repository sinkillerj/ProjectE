package moze_intel.projecte.utils;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.network.packets.to_client.NovaExplosionSyncPKT;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.ItemStackMap;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for anything that touches a World. Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class WorldHelper {

	private static final Predicate<Entity> SWRG_REPEL_PREDICATE = entity -> validRepelEntity(entity, PETags.Entities.BLACKLIST_SWRG);
	private static final Map<Block, IntegerProperty> AGE_PROPERTIES = new Reference2ObjectOpenHashMap<>();

	public static void clearCachedAgeProperties() {
		AGE_PROPERTIES.clear();
	}

	/**
	 * Drops all the items in the list at the given location compacting as much as possible.
	 *
	 * @param drops Items to drop. Will not be modified by the function
	 */
	public static void createLootDrop(List<ItemStack> drops, Level level, BlockPos pos) {
		createLootDrop(drops, level, pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Drops all the items in the list at the given location compacting as much as possible.
	 *
	 * @param drops Items to drop. Will not be modified by the function
	 */
	public static void createLootDrop(List<ItemStack> drops, Level level, Vec3 pos) {
		createLootDrop(drops, level, pos.x(), pos.y(), pos.z());
	}

	/**
	 * Drops all the items in the list at the given location compacting as much as possible.
	 *
	 * @param drops Items to drop. Will not be modified by the function
	 */
	public static void createLootDrop(List<ItemStack> drops, Level level, double x, double y, double z) {
		if (!drops.isEmpty()) {
			//Note: We need to ensure that the dropped items do not exceed the max stack size so that
			// there is not an error when the item entities are saved to disk
			Map<ItemStack, ItemEntity> knownItems = ItemStackMap.createTypeAndTagMap();
			for (ItemStack drop : drops) {
				if (!drop.isEmpty()) {
					int dropCount = drop.getCount();
					ItemEntity itemEntity = knownItems.get(drop);
					if (itemEntity != null) {
						int availableRoom = drop.getMaxStackSize() - itemEntity.getItem().getCount();
						if (dropCount <= availableRoom) {
							itemEntity.getItem().grow(dropCount);
							if (dropCount == availableRoom) {
								//Item entity is now holding as much as it can. Remove it from tracked items
								// and add it as a fresh entity
								knownItems.remove(drop);
								level.addFreshEntity(itemEntity);
							}
							//Skip to next item as this one has been fully handled
							continue;
						} else {
							itemEntity.getItem().grow(availableRoom);
							//Item entity is now holding as much as it can. Remove it from tracked items
							// and add it as a fresh entity
							knownItems.remove(drop);
							level.addFreshEntity(itemEntity);
							// Decrement how much we have left to drop and continue on
							dropCount -= availableRoom;
						}
					}
					//Copy the drop with the amount that is actually being dropped and track it in our known items map
					itemEntity = new ItemEntity(level, x, y, z, drop.copyWithCount(dropCount));
					knownItems.put(drop, itemEntity);
				}
			}
			for (ItemEntity itemEntity : knownItems.values()) {
				level.addFreshEntity(itemEntity);
			}
		}
	}

	/**
	 * Equivalent of Level#explode and ServerLevel#explode
	 */
	public static void createNovaExplosion(Level level, Entity exploder, double x, double y, double z, float power) {
		if (level instanceof ServerLevel serverLevel) {
			Explosion.BlockInteraction mode = level.getGameRules().getBoolean(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
			NovaExplosion explosion = new NovaExplosion(level, exploder, x, y, z, power, mode);
			if (!EventHooks.onExplosionStart(level, explosion)) {
				explosion.explode();
				List<BlockPos> particlePositions = explosion.finalizeExplosion();
				NovaExplosionSyncPKT packet = new NovaExplosionSyncPKT(explosion.center(), explosion.radius(), explosion.getExplosionSound(), particlePositions);
				for (ServerPlayer player : serverLevel.players()) {
					//Based on ServerLevel#explode's range check
					if (player.distanceToSqr(x, y, z) < 4_096.0) {
						PacketDistributor.sendToPlayer(player, packet);
					}
				}
			}
		}
	}

	public static void drainFluid(@Nullable Player player, Level level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (block instanceof BucketPickup bucketPickup) {
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
		for (BlockPos pos : getPositionsInBox(player.getBoundingBox().inflate(1))) {
			pos = pos.immutable();
			if (level.getBlockState(pos).is(Blocks.FIRE) && PlayerHelper.hasBreakPermission((ServerPlayer) player, level, pos)) {
				level.removeBlock(pos, false);
			}
		}
	}

	public static void freezeInBoundingBox(Level level, AABB box, Player player, boolean random) {
		for (BlockPos pos : getPositionsInBox(box)) {
			BlockState state = level.getBlockState(pos);
			//Ensure we are immutable so that changing blocks doesn't act weird
			pos = pos.immutable();
			if (state.is(Blocks.WATER) && (!random || level.random.nextInt(128) == 0)) {
				if (player != null) {
					PlayerHelper.checkedReplaceBlock((ServerPlayer) player, level, pos, Blocks.ICE.defaultBlockState());
				} else {
					level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
				}
			} else if (Block.isFaceFull(state.getCollisionShape(level, pos.below()), Direction.UP)) {
				BlockPos up = pos.above();
				BlockState stateUp = level.getBlockState(up);
				BlockState newState = null;

				if (stateUp.isAir() && (!random || level.random.nextInt(128) == 0)) {
					newState = Blocks.SNOW.defaultBlockState();
				} else if (stateUp.is(Blocks.SNOW) && stateUp.getValue(SnowLayerBlock.LAYERS) < SnowLayerBlock.MAX_HEIGHT && level.random.nextInt(Block.UPDATE_LIMIT) == 0) {
					newState = stateUp.setValue(SnowLayerBlock.LAYERS, stateUp.getValue(SnowLayerBlock.LAYERS) + 1);
				}
				if (newState != null) {
					if (player != null) {
						PlayerHelper.checkedReplaceBlock((ServerPlayer) player, level, up, newState);
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
	public static void placeFluid(@Nullable Player player, Level level, BlockPos pos, Direction sideHit, FlowingFluid fluid, boolean checkWaterVaporize) {
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
	public static void placeFluid(@Nullable Player player, Level level, BlockPos pos, FlowingFluid fluid, boolean checkWaterVaporize) {
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
			} else if (PlayerHelper.checkedPlaceBlock(player, level, pos, fluid.defaultFluidState().createLegacyBlock())) {
				level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
			}
		}
	}

	public static void copySignData(Level level, BlockPos pos, SignBlockEntity oldSign) {
		if (oldSign != null && level.getBlockEntity(pos) instanceof SignBlockEntity newSign) {
			newSign.setText(oldSign.getText(true), true);
			newSign.setText(oldSign.getText(false), false);
			newSign.setAllowedPlayerEditor(oldSign.getPlayerWhoMayEdit());
			newSign.setWaxed(oldSign.isWaxed());
		}
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AABB getBroadDeepBox(BlockPos pos, Direction direction, int offset) {
		return getBroadDeepBox(pos, direction, offset, 2 * offset);
	}

	/**
	 * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
	 */
	public static AABB getBroadDeepBox(BlockPos pos, Direction direction, int breadth, int depth) {
		AABB box = getBroadBox(pos, direction, breadth);
		if (depth == 0) {//Short circuit if a zero depth is passed
			return box;
		}
		return box.expandTowards(depth * -direction.getStepX(), depth * -direction.getStepY(), depth * -direction.getStepZ());
	}

	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	public static AABB getDeepBox(BlockPos pos, Direction direction, int depth) {
		return getBroadDeepBox(pos, direction, 1, depth);
	}

	/**
	 * Returns in AABB that is always a single block deep but is size x size orthogonal to the side hit
	 */
	public static AABB getBroadBox(BlockPos pos, Direction direction, int breadth) {
		AABB point = new AABB(pos);
		if (breadth == 0) {//Short circuit to just returning the block itself
			return point;
		}
		return switch (direction) {
			case EAST, WEST -> point.inflate(0, breadth, breadth);
			case UP, DOWN -> point.inflate(breadth, 0, breadth);
			case SOUTH, NORTH -> point.inflate(breadth, breadth, 0);
		};
	}

	/**
	 * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box. Y level remains constant. As such, a direction hit is unneeded.
	 */
	public static AABB getFlatYBox(BlockPos pos, int offset) {
		return getBroadBox(pos, Direction.UP, offset);
	}

	/**
	 * Similar to vanilla's {@link BlockPos#betweenClosedStream(AABB)} but calling {@link BlockPos#betweenClosed(int, int, int, int, int, int)} instead of
	 * {@link BlockPos#betweenClosedStream(int, int, int, int, int, int)}
	 *
	 * Note that this is inclusive of all positions in the AABB (except those that start on the edge)! This is different from vanilla's method which contains blocks on
	 * the edge.
	 */
	public static Iterable<BlockPos> getPositionsInBox(AABB box) {//TODO: Re-evaluate all our BlockPos#immutable calls, as some may no longer be necessary
		float epsilon = com.mojang.math.Constants.EPSILON;
		//Similar to as if we did box = box.deflate(epsilon), but without creating the extra intermediary AABB
		return BlockPos.betweenClosed(
				Mth.floor(box.minX + epsilon),
				Mth.floor(box.minY + epsilon),
				Mth.floor(box.minZ + epsilon),
				Mth.floor(box.maxX - epsilon),
				Mth.floor(box.maxY - epsilon),
				Mth.floor(box.maxZ - epsilon)
		);
	}

	public static Iterable<BlockPos> horizontalPositionsAround(BlockPos pos, int horizontalRadius) {
		return positionsAround(pos, horizontalRadius, 0, horizontalRadius);
	}

	public static Iterable<BlockPos> positionsAround(BlockPos pos, int radius) {
		return positionsAround(pos, radius, radius, radius);
	}

	public static Iterable<BlockPos> positionsAround(BlockPos pos, int xRadius, int yRadius, int zRadius) {
		return BlockPos.betweenClosed(pos.offset(-xRadius, -yRadius, -zRadius), pos.offset(xRadius, yRadius, zRadius));
	}

	public static List<BlockEntity> getBlockEntitiesWithinAABB(Level level, AABB box, Predicate<BlockEntity> predicate) {
		List<BlockEntity> list = new ArrayList<>();
		for (BlockPos pos : getPositionsInBox(box)) {
			BlockEntity blockEntity = getBlockEntity(level, pos);
			if (blockEntity != null && predicate.test(blockEntity)) {
				list.add(blockEntity);
			}
		}
		return list;
	}

	/**
	 * Gravitates an entity, vanilla xp orb style, towards a position Code adapted from EntityXPOrb and OpenBlocks Vacuum Hopper, mostly the former
	 */
	public static void gravitateEntityTowards(Entity ent, Vec3 target) {
		Vec3 difference = target.subtract(ent.position());
		double vel = 1.0 - difference.length() / 15.0;
		if (vel > 0.0D) {
			vel *= vel;
			ent.addDeltaMovement(difference.normalize()
					.scale(vel)
					.multiply(0.1, 0.2, 0.1)
			);
		}
	}

	public static void growNearbyRandomly(boolean harvest, Level level, Player player) {
		growNearbyRandomly(harvest, level, player.getBoundingBox().inflate(5, 3, 5), player);
	}

	public static void growNearbyRandomly(boolean harvest, Level level, AABB box, @Nullable Player player) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		boolean grewWater = false;
		int chance = harvest ? 16 : 32;
		for (BlockPos currentPos : getPositionsInBox(box)) {
			currentPos = currentPos.immutable();
			BlockState state = level.getBlockState(currentPos);
			if (state.getBlock() instanceof BonemealableBlock growable) {
				//Note: We intentionally don't fire the bone meal used event, as we aren't actually applying bone meal to the target
				if (growable.isValidBonemealTarget(level, currentPos, state)) {
					if (ProjectEConfig.server.items.harvBandIndirect.get() || !onlyAffectsOtherBlocks(state.getBlock())) {
						//Based on our chance, apply bonemeal if the subchance for that growable also passes
						if (level.random.nextInt(chance) == 0 && growable.isBonemealSuccess(level, level.random, currentPos, state)) {
							growable.performBonemeal(serverLevel, level.random, currentPos, state);
							level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
						}
					}
				} else {
					//Fully grown block, try to harvest it
					tryHarvest(level, currentPos, state, player, harvest);
				}
			} else if (isPlantable(state)) {
				//Any modded or vanilla plants that are not bonemealable
				//Note: While things like mangroves leaves do return true for isPlantable, they won't be handled by this branch as they are bonemealable
				// and thus will be handled by the corresponding above check
				if (state.isRandomlyTicking() && level.random.nextInt(chance / 4) == 0) {
					//If the block accepts random ticks, apply a chance to give it said extra random tick.
					// This includes things like vanilla flowers (modded flowers might have random ticks, or they might not)
					Block initialType = state.getBlock();
					for (int i = 0, ticks = harvest ? 8 : 4; i < ticks; i++) {
						state.randomTick(serverLevel, currentPos, level.random);
						state = level.getBlockState(currentPos);
						if (!state.is(initialType)) {
							//If the state changed blocks (not just states) we are in a state we aren't quite sure how to handle
							break;
						}
					}
					if (!state.is(initialType)) {
						//If the type got changed on us, continue to the next position
						continue;
					}
				}
				tryHarvest(level, currentPos, state, player, harvest);
			}
			// Generic water plants
			else if (!grewWater && level.random.nextInt(Block.UPDATE_LIMIT) == 0 && BoneMealItem.growWaterPlant(ItemStack.EMPTY, level, currentPos, null)) {
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
				grewWater = true;
			}
		}
	}

	/**
	 * Checks if a block should be harvested, and if it should it tries to break and "harvests" the block if the player has permission to break it or there is no player
	 */
	private static void tryHarvest(Level level, BlockPos pos, BlockState state, @Nullable Player player, boolean harvest) {
		if (harvest && shouldHarvest(level, pos, state)) {
			if (player == null || PlayerHelper.hasEditPermission(player, level, pos)) {
				if (!(player instanceof ServerPlayer serverPlayer) || PlayerHelper.checkBreakPermission(serverPlayer, level, pos)) {
					level.destroyBlock(pos, true, player);
				}
			}
		}
	}

	private static boolean shouldHarvest(Level level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (state.is(PETags.Blocks.BLACKLIST_HARVEST) || isUnharvestableImplementation(block)) {
			return false;
		} else if (block instanceof BambooStalkBlock || block instanceof SugarCaneBlock) {
			//Don't harvest the bottom of sugar cane or bamboo plants so that they will be able to keep growing
			return level.getBlockState(pos.below()).is(block);
		} else if (block instanceof CactusBlock) {
			//Only harvest cactus if it is the bottom block and there is no cactus under it. This makes it so that it doesn't get destroyed when breaking
			return !level.getBlockState(pos.below()).is(block) && level.getBlockState(pos.above()).is(block);
		} else if (block instanceof GrowingPlantBlock growingPlantBlock) {
			//Don't harvest the base block of plants that grow vertically (be it ones that grow down, or ones that grow up)
			return level.getBlockState(pos.relative(growingPlantBlock.growthDirection, -1)).is(growingPlantBlock.getBodyBlock());
		} else if (block instanceof LeavesBlock leavesBlock) {
			//Only harvest leaves if they would be decaying
			return leavesBlock.decaying(state);
		} else if (block instanceof VineBlock) {
			//Allow harvesting vines if they have another vine block above them
			return level.getBlockState(pos.above()).is(block);
		} else if (block instanceof GlowLichenBlock) {
			//TODO: Eventually we might want to implement better handling for this to try and not break the top/source block
		}
		if (block instanceof CropBlock cropBlock) {
			return cropBlock.isMaxAge(state);
		}
		//Fallback handling for any states that have an age declared, but are not at the max age
		// Things like sugar cane that are partially grown, get handled above, so won't be prevented from being harvested by this
		// but things like nether wart, will be handled by this
		IntegerProperty ageProperty = null;
		//Note: We can't use computeIfAbsent, as we explicitly want to not compute if there is a stored value equal to null
		if (AGE_PROPERTIES.containsKey(block)) {
			//If we have a cached value, grab it
			ageProperty = AGE_PROPERTIES.get(block);
		} else {
			//Figure out what age property this block uses
			for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
				if (entry.getKey().getName().equals("age")) {
					if (entry.getValue() instanceof IntegerProperty intProperty) {
						//It is a type of property we understand how to handle
						ageProperty = intProperty;
					}
					break;
				}
			}
			AGE_PROPERTIES.put(block, ageProperty);
		}
		//If the block doesn't have an age property, or it is at the max age. Allow harvesting
		return ageProperty == null || state.getValue(ageProperty) == ageProperty.max;
	}

	/**
	 * The instances this method checks exist to do our best to support modded versions out of the box, as there aren't vanilla or neo tags for these types.
	 */
	public static boolean isUnharvestableImplementation(Block block) {
		//Instance check for blocks that get handled because of being plantable from the instanceof BushBlock check
		//Note: We can't just include these by default in the blacklist harvest tag, as then we might harvest modded ones that we don't want to
		return block instanceof StemBlock || block instanceof AttachedStemBlock || block instanceof WaterlilyBlock || onlyAffectsOtherBlocks(block);
	}

	/**
	 * Non-bonemealable plants. Contains all vanilla, and best effort attempt at modded plants.
	 */
	private static boolean isPlantable(BlockState state) {
		return state.is(PETags.Blocks.OVERRIDE_PLANTABLE) || isPlantableImplementation(state.getBlock());
	}

	/**
	 * The instances this method checks exist to do our best to support modded versions out of the box, as there aren't vanilla or neo tags for these types.
	 */
	public static boolean isPlantableImplementation(Block block) {
		return block instanceof CactusBlock || block instanceof SugarCaneBlock || block instanceof VineBlock || block instanceof BushBlock;
	}

	public static boolean isCrop(BlockState state) {
		if (state.getBlock() instanceof BonemealableBlock) {
			return ProjectEConfig.server.items.harvBandIndirect.get() || !onlyAffectsOtherBlocks(state.getBlock());
		}
		return isPlantable(state);
	}

	private static boolean onlyAffectsOtherBlocks(Block block) {
		//We don't want these to be broken as the bonemeal affects a different block than the one in their position,
		// and either doesn't or has a chance of not changing whether bonemeal can be applied
		return block instanceof GrassBlock || block instanceof NyliumBlock || block instanceof MossBlock;
	}

	private static <DATA> boolean validState(DATA data, BiPredicate<BlockState, DATA> stateChecker, BlockState state, Level level, BlockPos pos, Player player) {
		return stateChecker.test(state, data) && state.getDestroySpeed(level, pos) != Block.INDESTRUCTIBLE && PlayerHelper.hasEditPermission(player, level, pos);
	}

	public static <DATA> int harvestVein(Level level, Player player, ItemStack stack, AABB area, List<ItemStack> currentDrops, DATA data,
			BiPredicate<BlockState, DATA> stateChecker) {
		record TargetInfo(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {

			public static TargetInfo create(Level level, BlockPos pos, BlockState state, Player player) {
				//Note: Similar to vanilla we query the block entity before calling playerWillDestroy
				BlockEntity blockEntity = state.hasBlockEntity() ? getBlockEntity(level, pos) : null;
				return new TargetInfo(pos, state.getBlock().playerWillDestroy(level, pos, state, player), blockEntity);
			}
		}
		int numMined = 0;
		Set<BlockPos> traversed = new HashSet<>();
		Queue<TargetInfo> frontier = new ArrayDeque<>();
		VeinStateChecker<DATA> validState;
		//Ensure the block can be destroyed and the player can target the block at that position
		if (level.isClientSide) {
			validState = WorldHelper::validState;
		} else {
			//If we are server side we want to perform an extra check to determine if the player can break the block
			validState = (dat, checker, state, lvl, pos, p) ->
					validState(dat, checker, state, lvl, pos, p) && PlayerHelper.checkBreakPermission((ServerPlayer) p, lvl, pos);
		}

		for (BlockPos pos : WorldHelper.getPositionsInBox(area)) {
			BlockState state = level.getBlockState(pos);
			if (validState.test(data, stateChecker, state, level, pos, player)) {
				if (level.isClientSide) {
					return 1;
				}
				pos = pos.immutable();
				frontier.add(TargetInfo.create(level, pos, state, player));
			}
			//Regardless of if it is valid or not mark it as  having been traversed
			traversed.add(pos.immutable());
		}

		while (!frontier.isEmpty()) {
			TargetInfo targetInfo = frontier.poll();
			BlockPos pos = targetInfo.pos();
			BlockState state = targetInfo.state();
			if (state.onDestroyedByPlayer(level, pos, player, true, level.getFluidState(pos))) {
				Block block = state.getBlock();
				block.destroy(level, pos, state);
				player.awardStat(Stats.BLOCK_MINED.get(block));
				currentDrops.addAll(Block.getDrops(state, (ServerLevel) level, pos, targetInfo.blockEntity(), player, stack));
				if (++numMined >= Constants.MAX_VEIN_SIZE) {
					break;
				}

				for (BlockPos nextPos : positionsAround(pos, 1)) {
					//Ensure the position is immutable before we add it to what positions we have traversed
					nextPos = nextPos.immutable();
					if (traversed.add(nextPos) && isBlockLoaded(level, nextPos)) {
						BlockState nextState = level.getBlockState(nextPos);
						if (validState.test(data, stateChecker, nextState, level, nextPos, player)) {
							frontier.add(TargetInfo.create(level, nextPos, nextState, player));
						}
					}
				}
			}
		}
		return numMined;
	}

	public static void igniteNearby(Level level, Player player) {
		for (BlockPos pos : getPositionsInBox(player.getBoundingBox().inflate(8, 5, 8))) {
			if (level.random.nextInt(128) == 0 && level.isEmptyBlock(pos)) {
				PlayerHelper.checkedPlaceBlock(player, level, pos.immutable(), Blocks.FIRE.defaultBlockState());
			}
		}
	}

	public static boolean validRepelEntity(Entity entity, TagKey<EntityType<?>> blacklistTag) {
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

	public static void repelEntity(Vec3 vec, Entity entity) {
		double distance = vec.distanceTo(entity.position()) + 0.1;
		entity.push(entity.position()
				.subtract(vec)
				.scale(1 / (1.5 * distance)));
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
		if (BaseFireBlock.canBePlacedAt(level, pos, side)) {
			if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, level, pos)) {
				level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
				level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		} else {
			BlockState state = level.getBlockState(pos);
			if (state.getToolModifiedState(ctx, ItemAbilities.FIRESTARTER_LIGHT, true) != null) {
				if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, level, pos)) {
					BlockState modifiedState = state.getToolModifiedState(ctx, ItemAbilities.FIRESTARTER_LIGHT, false);
					if (modifiedState != null) {//Theoretically should not be null as we just simulated, but validate it just in case
						level.setBlockAndUpdate(pos, modifiedState);
						level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
					}
				}
			} else if (state.isFlammable(level, pos, side)) {
				if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, level, pos)) {
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
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	/**
	 * Checks if the chunk at the given position is loaded but does not validate the position is in bounds of the world.
	 *
	 * @param world world
	 * @param pos   position
	 *
	 * @see #isBlockLoaded(BlockGetter, BlockPos)
	 */
	@Contract("null, _ -> false")
	public static boolean isChunkLoaded(@Nullable LevelReader world, @NotNull BlockPos pos) {
		return isChunkLoaded(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
	}

	/**
	 * Checks if the chunk at the given position is loaded.
	 *
	 * @param world    world
	 * @param chunkPos Chunk position
	 */
	@Contract("null, _ -> false")
	public static boolean isChunkLoaded(@Nullable LevelReader world, ChunkPos chunkPos) {
		return isChunkLoaded(world, chunkPos.x, chunkPos.z);
	}

	/**
	 * Checks if the chunk at the given position is loaded.
	 *
	 * @param world  world
	 * @param chunkX Chunk X coordinate
	 * @param chunkZ Chunk Z coordinate
	 */
	@Contract("null, _, _ -> false")
	public static boolean isChunkLoaded(@Nullable LevelReader world, int chunkX, int chunkZ) {
		if (world == null) {
			return false;
		} else if (world instanceof LevelAccessor accessor) {
			return accessor.hasChunk(chunkX, chunkZ);
		}
		return world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
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
			//TODO: If any cases come up where things are behaving oddly due to the change from reader.hasChunkAt(pos)
			// re-evaluate this and if the specific case is being handled properly
			return isChunkLoaded(reader, pos);
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

	@FunctionalInterface
	private interface VeinStateChecker<DATA> {

		boolean test(DATA data, BiPredicate<BlockState, DATA> stateChecker, BlockState state, Level level, BlockPos pos, Player player);
	}
}