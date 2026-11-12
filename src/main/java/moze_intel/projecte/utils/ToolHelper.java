package moze_intel.projecte.utils;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.tools.PEPickaxe.PickaxeMode;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

public class ToolHelper {

	private static final Identifier CHARGE_MODIFIER_ID = PECore.rl("charge_modifier");

	public static final ItemAbility HAMMER_DIG = ItemAbility.get("hammer_dig");
	public static final ItemAbility KATAR_DIG = ItemAbility.get("katar_dig");
	public static final ItemAbility MORNING_STAR_DIG = ItemAbility.get("morning_star_dig");
	public static final ItemAbility HOE_TILL = ItemAbility.get("hoe_till");
	public static final ItemAbility SHOVEL_FLATTEN = ItemAbility.get("shovel_flatten");
	public static final ItemAbility AXE_STRIP = ItemAbility.get("axe_strip");
	public static final ItemAbility AXE_SCRAPE = ItemAbility.get("axe_scrape");
	public static final ItemAbility AXE_WAX_OFF = ItemAbility.get("axe_wax_off");

	public static final Set<ItemAbility> KATAR_TOOL_ACTIONS = of(
			HOE_TILL, SHOVEL_FLATTEN, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF, ItemAbilities.SWORD_SWEEP,
			ItemAbilities.SHEARS_DIG, ItemAbilities.SHEARS_HARVEST, ItemAbilities.SHEARS_CARVE, ItemAbilities.SHEARS_TRIM,
			KATAR_DIG);
	public static final Set<ItemAbility> MORNING_STAR_TOOL_ACTIONS = of(SHOVEL_FLATTEN, ItemAbilities.SHOVEL_DOUSE, HAMMER_DIG, MORNING_STAR_DIG);

	public static final Set<ItemAbility> DEFAULT_PE_HAMMER_ACTIONS = of(HAMMER_DIG);
	public static final Set<ItemAbility> DEFAULT_PE_KATAR_ACTIONS = of(KATAR_DIG);
	public static final Set<ItemAbility> DEFAULT_PE_MORNING_STAR_ACTIONS = of(MORNING_STAR_DIG);

	//Note: These all also do the check that super did before of making sure the entity is not spectating
	private static final Predicate<Entity> SHEARABLE = entity -> !entity.isSpectator() && entity instanceof IShearable;
	private static final Predicate<Entity> SLAY_MOB = entity -> !entity.isSpectator() && entity instanceof Enemy;
	private static final Predicate<Entity> SLAY_ALL = entity -> !entity.isSpectator() && (entity instanceof Enemy || entity instanceof LivingEntity);

	private static Set<ItemAbility> of(ItemAbility... actions) {
		return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
	}

	/**
	 * Performs a set of actions, until we find a success or run out of actions.
	 *
	 * @implNote Only returns that we failed if all the tested actions failed.
	 */
	@SafeVarargs
	public static InteractionResult performActions(UseOnContext context, BlockState state, InteractionResult firstAction,
			BiFunction<UseOnContext, BlockState, InteractionResult>... secondaryActions) {
		if (firstAction.consumesAction()) {
			return firstAction;
		}
		InteractionResult result = firstAction;
		boolean hasFailed = result == InteractionResult.FAIL;
		for (BiFunction<UseOnContext, BlockState, InteractionResult> secondaryAction : secondaryActions) {
			result = secondaryAction.apply(context, state);
			if (result.consumesAction()) {
				//If we were successful
				return result;
			}
			hasFailed &= result == InteractionResult.FAIL;
		}
		if (hasFailed) {
			//If at least one step failed, consider ourselves unsuccessful
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Clears the given tag in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult clearTagAOE(Level level, Player player, InteractionHand hand, ItemStack stack, long emcCost, TagKey<Block> tag) {
		if (ProjectEConfig.server.items.disableAllRadiusMining.get()) {
			return InteractionResult.PASS;
		}
		int charge = getCharge(stack);
		if (charge == 0) {
			return InteractionResult.PASS;
		}
		int horizontalRadius = 5 * charge;
		int verticalRadius = 2 * horizontalRadius;
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : WorldHelper.getPositionsInBox(player.getBoundingBox().inflate(horizontalRadius, verticalRadius, horizontalRadius))) {
			BlockState state = level.getBlockState(pos);
			if (state.is(tag)) {
				if (level.isClientSide()) {
					return InteractionResult.SUCCESS;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, level, pos)) {
					if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
						drops.addAll(Block.getDrops(state, (ServerLevel) level, pos, WorldHelper.getBlockEntity(level, pos), player, stack));
						level.removeBlock(pos, false);
						hasAction = true;
						if (level.getRandom().nextInt(5) == 0) {
							((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0);
						}
					} else {
						//If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future blocks
						break;
					}
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, level, player.position());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static InteractionResult dowseCampfire(UseOnContext context, BlockState state) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();
			if (!level.isClientSide()) {
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			if (!level.isClientSide()) {
				level.setBlock(pos, state.setValue(CampfireBlock.LIT, Boolean.FALSE), Block.UPDATE_ALL_IMMEDIATE);
			}
			return WorldHelper.sidedSuccess(level);
		}
		return InteractionResult.PASS;
	}

	/**
	 * Tills in an AOE using a hoe. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult tillAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAOE(context, clickedState, emcCost, HOE_TILL, SoundEvents.HOE_TILL.value(), -1, new HoeToolAOEData());
	}

	/**
	 * Tills in an AOE using a shovel (ex: grass to grass path). Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult flattenAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		Direction sideHit = context.getClickedFace();
		if (sideHit == Direction.DOWN) {
			//Don't allow flattening a block from underneath
			return InteractionResult.PASS;
		}
		return useAOE(context, clickedState, emcCost, SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN.value(), -1, new ShovelToolAOEData());
	}

	/**
	 * Strips logs in an AOE using an axe (ex: log to stripped log). Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult stripLogsAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAxeAOE(context, clickedState, emcCost, AXE_STRIP, SoundEvents.AXE_STRIP.value(), -1);
	}

	public static InteractionResult scrapeAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAxeAOE(context, clickedState, emcCost, AXE_SCRAPE, SoundEvents.AXE_SCRAPE.value(), -1);
	}

	public static InteractionResult waxOffAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAxeAOE(context, clickedState, emcCost, AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF.value(), -1);
	}

	private static InteractionResult useAxeAOE(UseOnContext context, BlockState clickedState, long emcCost, ItemAbility action, SoundEvent sound, int particle) {
		return useAOE(context, clickedState, emcCost, action, sound, particle, new AxeToolAOEData());
	}

	private static InteractionResult useAOE(UseOnContext context, BlockState clickedState, long emcCost, ItemAbility action, SoundEvent sound, int particle,
			IToolAOEData toolAOEData) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!toolAOEData.isValid(level, pos, clickedState)) {
			//Skip modifying the blocks if there is something we think is invalid about the position in the world in general
			return InteractionResult.PASS;
		}
		BlockState modifiedState = clickedState.getToolModifiedState(context, action, false);
		if (modifiedState == null) {
			//Skip modifying the blocks if the one we clicked cannot be modified
			return InteractionResult.PASS;
		} else if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		//Process the block we interacted with initially and play the sound
		//Note: For more detailed comments on why/how we set the block and remove the block above see the for loop below
		CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, context.getItemInHand());
		level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
		if (particle != -1) {
			level.levelEvent(particle, pos, 0);
		}
		ItemStack stack = context.getItemInHand();
		int charge = getCharge(stack);
		if (charge > 0) {
			Direction side = context.getClickedFace();
			toolAOEData.persistData(level, pos, clickedState, side);
			for (BlockPos newPos : toolAOEData.getTargetPositions(pos, side, charge)) {
				if (pos.equals(newPos)) {
					//Skip the source position as we manually handled it before the loop
					continue;
				}
				//Check to make that the result we would get from modifying the other block is the same as the one we got on the initial block we interacted with
				// Also make sure that it is properly valid
				BlockState state = level.getBlockState(newPos);
				//Create a new used context based on the original one to try and pass the proper information to the conversion
				UseOnContext adjustedContext = new UseOnContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(
						context.getClickLocation().add(newPos.getX() - pos.getX(), newPos.getY() - pos.getY(), newPos.getZ() - pos.getZ()),
						context.getClickedFace(), newPos, context.isInside()));
				if (toolAOEData.isValid(level, newPos, state) && modifiedState == state.getToolModifiedState(adjustedContext, action, true)) {
					if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
						//Some of the below methods don't behave properly when the BlockPos is mutable, so now that we are onto ones where it may actually
						// matter we make sure to get an immutable instance of newPos
						newPos = newPos.immutable();
						CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, newPos, context.getItemInHand());
						//Run it without simulation in case there are any side effects
						state.getToolModifiedState(adjustedContext, action, false);
						//Replace the block. Note it just directly sets it (in the same way the normal tools do), rather than using our
						// checkedReplaceBlock to make the blocks not "blink" when getting changed. We don't bother using checkedReplaceBlock
						// as we already fired all the events/checks for seeing if we are allowed to use this item in this location and were
						// told that we are allowed to use our item.
						level.setBlock(newPos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
						if (particle != -1) {
							level.levelEvent(particle, newPos, 0);
						}
					} else {
						//If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future blocks
						break;
					}
				}
			}
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		return InteractionResult.CONSUME;
	}

	/**
	 * Called by multiple tools' left click function. Charge has no effect. Free operation.
	 */
	public static void digBasedOnMode(ItemStack stack, Level level, BlockPos pos, LivingEntity living, RayTracePointer tracePointer, PickaxeMode mode) {
		if (level.isClientSide() || mode == PickaxeMode.STANDARD || ProjectEConfig.server.items.disableAllRadiusMining.get() || !(living instanceof Player player)) {
			return;
		}
		BlockHitResult result = tracePointer.rayTrace(level, player, ClipContext.Fluid.NONE);
		if (result.getType() == Type.MISS || !pos.equals(result.getBlockPos())) {
			//Ensure that the ray trace agrees with the position we were told about
			return;
		}
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos digPos : getTargets(pos, player, result.getDirection(), mode)) {
			BlockState state = level.getBlockState(digPos);
			if (!state.isAir() && state.getDestroySpeed(level, digPos) != Block.INDESTRUCTIBLE && stack.isCorrectToolForDrops(state)) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				digPos = digPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, level, digPos)) {
					drops.addAll(Block.getDrops(state, (ServerLevel) level, digPos, WorldHelper.getBlockEntity(level, digPos), player, stack));
					level.removeBlock(digPos, false);
				}
			}
		}
		WorldHelper.createLootDrop(drops, level, pos);
	}

	private static Iterable<BlockPos> getTargets(BlockPos pos, Player player, Direction sideHit, PickaxeMode mode) {
		return switch (mode) {
			case TALLSHOT -> BlockPos.betweenClosed(pos.below(), pos.above());
			//if the axis is vertical then try to use the player's facing direction to determine which direction to break
			case WIDESHOT -> switch (sideHit.getAxis() == Axis.Y ? player.getDirection().getAxis() : sideHit.getAxis()) {
				case X -> BlockPos.betweenClosed(pos.south(), pos.north());
				case Z -> BlockPos.betweenClosed(pos.west(), pos.east());
				default -> Collections.singleton(pos);
			};
			case LONGSHOT -> BlockPos.betweenClosed(pos, pos.relative(sideHit.getOpposite(), 2));
			default -> Collections.singleton(pos);
		};
	}

	/**
	 * Carves in an AOE. Charge affects the breadth and/or depth of the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult digAOE(Level level, Player player, InteractionHand hand, ItemStack stack, BlockPos pos, Direction sideHit, boolean affectDepth, long emcCost) {
		if (ProjectEConfig.server.items.disableAllRadiusMining.get()) {
			return InteractionResult.PASS;
		}
		int charge = getCharge(stack);
		if (charge == 0) {
			return InteractionResult.PASS;
		}
		AABB box = affectDepth ? WorldHelper.getBroadDeepBox(pos, sideHit, charge) : WorldHelper.getFlatYBox(pos, charge);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos newPos : WorldHelper.getPositionsInBox(box)) {
			BlockState state = level.getBlockState(newPos);
			if (!state.isAir() && state.getDestroySpeed(level, newPos) != Block.INDESTRUCTIBLE && stack.isCorrectToolForDrops(state)) {
				if (level.isClientSide()) {
					return InteractionResult.SUCCESS;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				newPos = newPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, level, newPos)) {
					if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
						drops.addAll(Block.getDrops(state, (ServerLevel) level, newPos, WorldHelper.getBlockEntity(level, newPos), player, stack));
						level.removeBlock(newPos, false);
						hasAction = true;
					} else {
						//If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future blocks
						break;
					}
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, level, pos);
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Attacks through armor. Charge affects damage. Free operation.
	 */
	public static void attackWithCharge(ItemStack stack, LivingEntity damaged, LivingEntity damager, float baseDmg) {
		if (!(damager instanceof Player player) || damager.level().isClientSide()) {
			return;
		}
		DamageSource dmg;
		int charge = getCharge(stack);
		float totalDmg = baseDmg;
		if (charge > 0) {
			dmg = PEDamageTypes.BYPASS_ARMOR_PLAYER_ATTACK.source(player);
			totalDmg += charge;
		} else {
			dmg = damager.damageSources().playerAttack(player);
		}
		damaged.hurt(dmg, totalDmg);
	}

	/**
	 * Attacks in an AOE. Charge affects AOE, not damage (intentional). Optional per-entity EMC cost.
	 */
	public static void attackAOE(ItemStack stack, Player player, boolean slayAll, float damage, long emcCost, InteractionHand hand) {
		Level level = player.level();
		if (level.isClientSide()) {
			return;
		}
		int charge = getCharge(stack);
		List<Entity> toAttack = level.getEntities(player, player.getBoundingBox().inflate(2.5F * charge), slayAll ? SLAY_ALL : SLAY_MOB);
		DamageSource src = PEDamageTypes.BYPASS_ARMOR_PLAYER_ATTACK.source(player);
		boolean hasAction = false;
		for (Entity entity : toAttack) {
			if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
				entity.hurt(src, damage);
				hasAction = true;
			} else {
				//If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future blocks
				break;
			}
		}
		if (hasAction) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			PlayerHelper.swingItem(player, hand);
		}
	}

	/**
	 * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
	 */
	public static InteractionResult shearEntityAOE(Player player, InteractionHand hand, long emcCost) {
		Level level = player.level();
		ItemStack stack = player.getItemInHand(hand);
		int offset = (int) Math.pow(2, 2 + getCharge(stack));
		//Get all entities also making sure that they are shearable
		List<Entity> list = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(offset, offset / 2.0, offset), SHEARABLE);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (Entity ent : list) {
			BlockPos entityPosition = ent.blockPosition();
			IShearable target = (IShearable) ent;
			if (target.isShearable(player, stack, level, entityPosition)) {
				if (level.isClientSide()) {
					return InteractionResult.SUCCESS;
				}
				if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
					List<ItemStack> entDrops = target.onSheared(player, stack, level, entityPosition);
					ent.gameEvent(GameEvent.SHEAR, player);
					if (!entDrops.isEmpty()) {
						//Double all drops (just add them all twice because we compact the list later anyways)
						//Note: The reason we don't grow the stacks like we used to is to ensure if a modded mob drops
						// items with over half their max stack size, we don't end up potentially messing up the logic
						// in the stack/trying to spawn in overly full stacks
						drops.addAll(entDrops);
						drops.addAll(entDrops);
					}
					hasAction = true;
				} else {
					//If we failed to consume EMC but needed EMC just break out early as we won't have the required EMC for any of the future blocks
					break;
				}
			}
			if (!level.isClientSide() && Math.random() < 0.01) {
				Entity e = ent.getType().create(level, EntitySpawnReason.EVENT);
				if (e != null) {
					e.setPos(ent.getX(), ent.getY(), ent.getZ());
					if (e instanceof Mob mob) {
						EventHooks.finalizeMobSpawn(mob, (ServerLevel) level, ((ServerLevel) level).getCurrentDifficultyAt(entityPosition), EntitySpawnReason.EVENT, null);
					}
					if (e instanceof Sheep sheep) {
						sheep.setColor(DyeColor.byId(level.getRandom().nextInt(16)));
					}
					if (e instanceof AgeableMob mob) {
						mob.setAge(AgeableMob.BABY_START_AGE);
					}
					level.addFreshEntity(e);
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, level, player.position());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Scans and harvests an ore vein.
	 */
	public static InteractionResult tryVeinMine(Player player, ItemStack stack, BlockPos pos, Direction sideHit) {
		if (ProjectEConfig.server.items.disableAllRadiusMining.get()) {
			return InteractionResult.PASS;
		}
		Level level = player.level();
		BlockState target = level.getBlockState(pos);
		if (target.getDestroySpeed(level, pos) == Block.INDESTRUCTIBLE || !stack.isCorrectToolForDrops(target)) {
			return InteractionResult.FAIL;
		}
		AABB area = WorldHelper.getBroadDeepBox(pos, sideHit, getCharge(stack));
		return harvestVein(level, player, pos, stack, area, target.getBlock(), BlockStateBase::is, (drops, lvl, p) -> {
			WorldHelper.createLootDrop(drops, lvl, p);
			lvl.playSound(null, p.getX(), p.getY(), p.getZ(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		});
	}

	/**
	 * Mines all ore veins in a Box around the player.
	 */
	public static InteractionResult mineOreVeinsInAOE(Player player, InteractionHand hand) {
		if (ProjectEConfig.server.items.disableAllRadiusMining.get()) {
			return InteractionResult.PASS;
		}
		Level level = player.level();
		ItemStack stack = player.getItemInHand(hand);
		BiPredicate<BlockState, ItemStack> stateChecker = (state, itemStack) -> state.is(Tags.Blocks.ORES) && itemStack.isCorrectToolForDrops(state);
		AABB area = player.getBoundingBox().inflate(getCharge(stack) + 3);
		return harvestVein(level, player, player.blockPosition(), stack, area, stack, stateChecker, WorldHelper::createLootDrop);
	}

	public static <DATA> InteractionResult harvestVein(Level level, Player player, BlockPos dropPos, ItemStack stack, AABB area, DATA data,
			BiPredicate<BlockState, DATA> stateChecker, DropSpawner spawnDrops) {
		if (ProjectEConfig.server.items.disableAllRadiusMining.get()) {
			return InteractionResult.PASS;
		}
		List<ItemStack> drops = new ArrayList<>();
		if (WorldHelper.harvestVein(level, player, stack, area, drops, data, stateChecker) > 0) {
			if (!level.isClientSide()) {
				spawnDrops.drop(drops, level, dropPos);
			}
			return WorldHelper.sidedSuccess(level);
		}
		return InteractionResult.PASS;
	}

	public static float getDestroySpeed(float parentDestroySpeed, IMatterType matterType, int charge) {
		if (parentDestroySpeed == 1) {
			//If we cannot harvest the block leave the value be
			return parentDestroySpeed;
		}
		return parentDestroySpeed + matterType.getChargeModifier() * charge;
	}

	public static boolean canMatterMine(IMatterType matterType, Block block) {
		return block instanceof IMatterBlock matterBlock && matterBlock.getMatterType().getMatterTier() <= matterType.getMatterTier();
	}

	private static int getCharge(ItemStack stack) {
		IItemCharge charge = stack.getCapability(PECapabilities.CHARGE_ITEM_CAPABILITY);
		return charge == null ? 0 : charge.getCharge(stack);
	}

	public static void applyChargeAttributes(ItemAttributeModifierEvent event) {
		int charge = getCharge(event.getItemStack());
		if (charge > 0) {
			event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(CHARGE_MODIFIER_ID, charge, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		}
	}

	@FunctionalInterface
	public interface DropSpawner {

		void drop(List<ItemStack> drops, Level level, BlockPos pos);
	}

	private interface IToolAOEData {

		boolean isValid(Level level, BlockPos pos, BlockState state);

		default void persistData(Level level, BlockPos pos, BlockState state, Direction side) {
		}

		Iterable<BlockPos> getTargetPositions(BlockPos pos, Direction side, int radius);
	}

	private static abstract class FlatToolAOEData implements IToolAOEData {

		@Override
		public Iterable<BlockPos> getTargetPositions(BlockPos pos, Direction side, int radius) {
			return WorldHelper.horizontalPositionsAround(pos, radius);
		}
	}

	private static class HoeToolAOEData extends FlatToolAOEData {

		@Override
		public boolean isValid(Level level, BlockPos pos, BlockState state) {
			//Always return that we are valid if we could find a conversion, we unfortunately are no longer able
			// to allow conversions when there is plants on top of it as then the tool modified state won't return
			// anything
			return true;
		}
	}

	private static class ShovelToolAOEData extends FlatToolAOEData {

		@Override
		public boolean isValid(Level level, BlockPos pos, BlockState state) {
			BlockPos abovePos = pos.above();
			BlockState aboveState = level.getBlockState(abovePos);
			//Allow flattening a block when the above block is air
			if (aboveState.isAir()) {
				return true;
			}
			//Or it is a replaceable plant that is also not solid (such as tall grass)
			//Note: This may not be the most optimal way of checking this, but it gives a decent enough estimate of it
			//TODO: Do we want to try and come up with a better tag or check for if it is a replaceable plant?
			if (aboveState.is(PETags.Blocks.FARMING_OVERRIDE) || aboveState.canBeReplaced() && aboveState.is(BlockTags.REPLACEABLE_BY_TREES)) {
				return aboveState.getFluidState().isEmpty() && !aboveState.isSolid();
			}
			return false;
		}
	}

	private static class AxeToolAOEData implements IToolAOEData {

		@Nullable
		private Axis axis;
		private boolean isSet;

		@Override
		public boolean isValid(Level level, BlockPos blockPos, BlockState state) {
			return !isSet || axis == getAxis(state);
		}

		@Override
		public void persistData(Level level, BlockPos pos, BlockState state, Direction side) {
			axis = getAxis(state);
			isSet = true;
		}

		@Override
		public Iterable<BlockPos> getTargetPositions(BlockPos pos, Direction side, int radius) {
			return WorldHelper.getPositionsInBox(WorldHelper.getBroadBox(pos, side, radius));
		}

		@Nullable
		private Axis getAxis(BlockState state) {
			return state.hasProperty(RotatedPillarBlock.AXIS) ? state.getValue(RotatedPillarBlock.AXIS) : null;
		}
	}

	@FunctionalInterface
	public interface RayTracePointer {

		BlockHitResult rayTrace(Level level, Player player, ClipContext.Fluid fluidMode);
	}
}
