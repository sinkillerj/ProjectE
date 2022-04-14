package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolHelper {

	public static final ToolAction HAMMER_DIG = ToolAction.get("hammer_dig");
	public static final ToolAction KATAR_DIG = ToolAction.get("katar_dig");
	public static final ToolAction MORNING_STAR_DIG = ToolAction.get("morning_star_dig");

	public static final Set<ToolAction> DEFAULT_PE_HAMMER_ACTIONS = of(HAMMER_DIG);
	public static final Set<ToolAction> DEFAULT_PE_KATAR_ACTIONS = of(KATAR_DIG);
	public static final Set<ToolAction> DEFAULT_PE_MORNING_STAR_ACTIONS = of(MORNING_STAR_DIG);

	//Note: These all also do the check that super did before of making sure the entity is not spectating
	private static final Predicate<Entity> SHEARABLE = entity -> !entity.isSpectator() && entity instanceof IForgeShearable;
	private static final Predicate<Entity> SLAY_MOB = entity -> !entity.isSpectator() && entity instanceof Enemy;
	private static final Predicate<Entity> SLAY_ALL = entity -> !entity.isSpectator() && (entity instanceof Enemy || entity instanceof LivingEntity);

	private static Set<ToolAction> of(ToolAction... actions) {
		return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
	}

	/**
	 * Performs a set of actions, until we find a success or run out of actions.
	 *
	 * @implNote Only returns that we failed if all the tested actions failed.
	 */
	@SafeVarargs
	public static InteractionResult performActions(InteractionResult firstAction, Supplier<InteractionResult>... secondaryActions) {
		if (firstAction.consumesAction()) {
			return firstAction;
		}
		InteractionResult result = firstAction;
		boolean hasFailed = result == InteractionResult.FAIL;
		for (Supplier<InteractionResult> secondaryAction : secondaryActions) {
			result = secondaryAction.get();
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
		int scaled1 = 5 * charge;
		int scaled2 = 10 * charge;
		BlockPos corner1 = player.blockPosition().offset(-scaled1, -scaled2, -scaled1);
		BlockPos corner2 = player.blockPosition().offset(scaled1, scaled2, scaled1);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : WorldHelper.getPositionsFromBox(corner1, corner2)) {
			BlockState state = level.getBlockState(pos);
			if (state.is(tag)) {
				if (level.isClientSide) {
					return InteractionResult.SUCCESS;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
					if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
						drops.addAll(Block.getDrops(state, (ServerLevel) level, pos, WorldHelper.getBlockEntity(level, pos), player, stack));
						level.removeBlock(pos, false);
						hasAction = true;
						if (level.random.nextInt(5) == 0) {
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
			WorldHelper.createLootDrop(drops, level, player.getX(), player.getY(), player.getZ());
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
				level.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
			}
			CampfireBlock.dowse(player, level, pos, state);
			if (!level.isClientSide()) {
				level.setBlock(pos, state.setValue(CampfireBlock.LIT, Boolean.FALSE), Block.UPDATE_ALL_IMMEDIATE);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}

	/**
	 * Tills in an AOE using a hoe. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult tillAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAOE(context, clickedState, emcCost, ToolActions.HOE_TILL, SoundEvents.HOE_TILL, -1, new HoeToolAOEData());
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
		return useAOE(context, clickedState, emcCost, ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN, -1, new ShovelToolAOEData());
	}

	/**
	 * Strips logs in an AOE using an axe (ex: log to stripped log). Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static InteractionResult stripLogsAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAxeAOE(context, clickedState, emcCost, ToolActions.AXE_STRIP, SoundEvents.AXE_STRIP, -1);
	}

	public static InteractionResult scrapeAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAxeAOE(context, clickedState, emcCost, ToolActions.AXE_SCRAPE, SoundEvents.AXE_SCRAPE, LevelEvent.PARTICLES_SCRAPE);
	}

	public static InteractionResult waxOffAOE(UseOnContext context, BlockState clickedState, long emcCost) {
		return useAxeAOE(context, clickedState, emcCost, ToolActions.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF, LevelEvent.PARTICLES_WAX_OFF);
	}

	private static InteractionResult useAxeAOE(UseOnContext context, BlockState clickedState, long emcCost, ToolAction action, SoundEvent sound, int particle) {
		return useAOE(context, clickedState, emcCost, action, sound, particle, new AxeToolAOEData());
	}

	private static InteractionResult useAOE(UseOnContext context, BlockState clickedState, long emcCost, ToolAction action, SoundEvent sound, int particle,
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
		} else if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		//Process the block we interacted with initially and play the sound
		//Note: For more detailed comments on why/how we set the block and remove the block above see the for loop below
		level.setBlock(pos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
		if (particle != -1) {
			level.levelEvent(null, particle, pos, 0);
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
						//Run it without simulation in case there are any side effects
						state.getToolModifiedState(adjustedContext, action, false);
						//Replace the block. Note it just directly sets it (in the same way the normal tools do), rather than using our
						// checkedReplaceBlock to make the blocks not "blink" when getting changed. We don't bother using checkedReplaceBlock
						// as we already fired all the events/checks for seeing if we are allowed to use this item in this location and were
						// told that we are allowed to use our item.
						level.setBlock(newPos, modifiedState, Block.UPDATE_ALL_IMMEDIATE);
						if (particle != -1) {
							level.levelEvent(null, particle, newPos, 0);
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
	public static void digBasedOnMode(ItemStack stack, Level level, BlockPos pos, LivingEntity living, RayTracePointer tracePointer) {
		if (level.isClientSide || ProjectEConfig.server.items.disableAllRadiusMining.get() || !(living instanceof Player player)) {
			return;
		}
		byte mode = getMode(stack);
		if (mode == 0) {
			//Standard
			return;
		}
		HitResult mop = tracePointer.rayTrace(level, player, ClipContext.Fluid.NONE);
		if (!(mop instanceof BlockHitResult result) || result.getType() == Type.MISS || !pos.equals(result.getBlockPos())) {
			//Ensure that the ray trace agrees with the position we were told about
			return;
		}
		Direction sideHit = result.getDirection();
		AABB box = switch (mode) {
			//3x Tallshot
			case 1 -> new AABB(pos.below(), pos.above());
			//3x Wideshot
			case 2 -> switch (sideHit.getAxis()) {
				case X -> new AABB(pos.south(), pos.north());
				case Y -> switch (player.getDirection().getAxis()) {
					case X -> new AABB(pos.south(), pos.north());
					case Z -> new AABB(pos.west(), pos.east());
					default -> new AABB(pos, pos);
				};
				case Z -> new AABB(pos.west(), pos.east());
			};
			//3x Longshot
			case 3 -> new AABB(pos, pos.relative(sideHit.getOpposite(), 2));
			default -> new AABB(pos, pos);
		};
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box)) {
			if (level.isEmptyBlock(digPos)) {
				continue;
			}
			BlockState state = level.getBlockState(digPos);
			if (state.getDestroySpeed(level, digPos) != -1 && stack.isCorrectToolForDrops(state)) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				digPos = digPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, digPos)) {
					drops.addAll(Block.getDrops(state, (ServerLevel) level, digPos, WorldHelper.getBlockEntity(level, digPos), player, stack));
					level.removeBlock(digPos, false);
				}
			}
		}
		WorldHelper.createLootDrop(drops, level, pos);
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
		for (BlockPos newPos : WorldHelper.getPositionsFromBox(box)) {
			if (level.isEmptyBlock(newPos)) {
				continue;
			}
			BlockState state = level.getBlockState(newPos);
			if (state.getDestroySpeed(level, newPos) != -1 && stack.isCorrectToolForDrops(state)) {
				if (level.isClientSide) {
					return InteractionResult.SUCCESS;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				newPos = newPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, newPos)) {
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
			player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Attacks through armor. Charge affects damage. Free operation.
	 */
	public static void attackWithCharge(ItemStack stack, LivingEntity damaged, LivingEntity damager, float baseDmg) {
		if (!(damager instanceof Player player) || damager.getCommandSenderWorld().isClientSide) {
			return;
		}
		DamageSource dmg = DamageSource.playerAttack(player);
		int charge = getCharge(stack);
		float totalDmg = baseDmg;
		if (charge > 0) {
			dmg.bypassArmor();
			totalDmg += charge;
		}
		damaged.hurt(dmg, totalDmg);
	}

	/**
	 * Attacks in an AOE. Charge affects AOE, not damage (intentional). Optional per-entity EMC cost.
	 */
	public static void attackAOE(ItemStack stack, Player player, boolean slayAll, float damage, long emcCost, InteractionHand hand) {
		Level level = player.getCommandSenderWorld();
		if (level.isClientSide) {
			return;
		}
		int charge = getCharge(stack);
		List<Entity> toAttack = level.getEntities(player, player.getBoundingBox().inflate(2.5F * charge), slayAll ? SLAY_ALL : SLAY_MOB);
		DamageSource src = DamageSource.playerAttack(player).bypassArmor();
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
	 * Called when tools that act as shears start breaking a block. Free operation.
	 */
	public static InteractionResult shearBlock(ItemStack stack, BlockPos pos, Player player) {
		Level level = player.getCommandSenderWorld();
		Block block = level.getBlockState(pos).getBlock();
		if (block instanceof IForgeShearable target) {
			if (target.isShearable(stack, level, pos) && (level.isClientSide || PlayerHelper.hasBreakPermission((ServerPlayer) player, pos))) {
				List<ItemStack> drops = target.onSheared(player, stack, level, pos, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack));
				if (!drops.isEmpty()) {
					if (!level.isClientSide) {
						//Protect against any mods that may use unmodifiable lists in their onSheared return
						WorldHelper.createLootDrop(new ArrayList<>(drops), level, pos);
						player.awardStat(Stats.BLOCK_MINED.get(block), 1);
						level.gameEvent(player, GameEvent.SHEAR, pos);
					}
					//NOTE: We only mark it as a success if we actually got drops otherwise we let it continue breaking the block
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	/**
	 * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
	 */
	public static InteractionResult shearEntityAOE(Player player, InteractionHand hand, long emcCost) {
		Level level = player.getCommandSenderWorld();
		ItemStack stack = player.getItemInHand(hand);
		int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
		int offset = (int) Math.pow(2, 2 + getCharge(stack));
		//Get all entities also making sure that they are shearable
		List<Entity> list = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(offset, offset / 2.0, offset), SHEARABLE);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (Entity ent : list) {
			BlockPos entityPosition = ent.blockPosition();
			IForgeShearable target = (IForgeShearable) ent;
			if (target.isShearable(stack, level, entityPosition)) {
				if (level.isClientSide) {
					return InteractionResult.SUCCESS;
				}
				if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
					List<ItemStack> entDrops = target.onSheared(player, stack, level, entityPosition, fortune);
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
			if (!level.isClientSide && Math.random() < 0.01) {
				Entity e = ent.getType().create(level);
				if (e != null) {
					e.setPos(ent.getX(), ent.getY(), ent.getZ());
					if (e instanceof Mob mob) {
						mob.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(entityPosition), MobSpawnType.EVENT, null, null);
					}
					if (e instanceof Sheep sheep) {
						sheep.setColor(DyeColor.byId(MathUtils.randomIntInRange(0, 15)));
					}
					if (e instanceof AgeableMob mob) {
						mob.setAge(-24000);
					}
					level.addFreshEntity(e);
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, level, player.getX(), player.getY(), player.getZ());
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
		Level level = player.getCommandSenderWorld();
		BlockState target = level.getBlockState(pos);
		if (target.getDestroySpeed(level, pos) <= -1 || !stack.isCorrectToolForDrops(target)) {
			return InteractionResult.FAIL;
		}
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos newPos : WorldHelper.getPositionsFromBox(WorldHelper.getBroadDeepBox(pos, sideHit, getCharge(stack)))) {
			if (!level.isEmptyBlock(newPos)) {
				BlockState state = level.getBlockState(newPos);
				if (target.getBlock() == state.getBlock()) {
					if (level.isClientSide) {
						return InteractionResult.SUCCESS;
					}
					//Ensure we are immutable so that changing blocks doesn't act weird
					if (WorldHelper.harvestVein(level, player, stack, newPos.immutable(), state.getBlock(), drops, 0) > 0) {
						hasAction = true;
					}
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, level, pos);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	/**
	 * Mines all ore veins in a Box around the player.
	 */
	public static InteractionResult mineOreVeinsInAOE(Player player, InteractionHand hand) {
		if (ProjectEConfig.server.items.disableAllRadiusMining.get()) {
			return InteractionResult.PASS;
		}
		Level level = player.getCommandSenderWorld();
		ItemStack stack = player.getItemInHand(hand);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : WorldHelper.getPositionsFromBox(player.getBoundingBox().inflate(getCharge(stack) + 3))) {
			if (level.isEmptyBlock(pos)) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			if (ItemHelper.isOre(state) && state.getDestroySpeed(level, pos) != -1 && stack.isCorrectToolForDrops(state)) {
				if (level.isClientSide) {
					return InteractionResult.SUCCESS;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				if (WorldHelper.harvestVein(level, player, stack, pos.immutable(), state.getBlock(), drops, 0) > 0) {
					hasAction = true;
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, level, player.getX(), player.getY(), player.getZ());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static float getDestroySpeed(float parentDestroySpeed, EnumMatterType matterType, int charge) {
		if (parentDestroySpeed == 1) {
			//If we cannot harvest the block leave the value be
			return parentDestroySpeed;
		}
		return parentDestroySpeed + matterType.getChargeModifier() * charge;
	}

	public static boolean canMatterMine(EnumMatterType matterType, Block block) {
		return block instanceof IMatterBlock matterBlock && matterBlock.getMatterType().getMatterTier() <= matterType.getMatterTier();
	}

	private static int getCharge(ItemStack stack) {
		return stack.getCapability(PECapabilities.CHARGE_ITEM_CAPABILITY).map(itemCharge -> itemCharge.getCharge(stack)).orElse(0);
	}

	private static byte getMode(ItemStack stack) {
		return stack.getCapability(PECapabilities.MODE_CHANGER_ITEM_CAPABILITY).map(itemMode -> itemMode.getMode(stack)).orElse((byte) 0);
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
			return BlockPos.betweenClosed(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius));
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
			Material material = aboveState.getMaterial();
			if (material == Material.REPLACEABLE_PLANT || material == Material.REPLACEABLE_FIREPROOF_PLANT) {
				return !aboveState.isSolidRender(level, abovePos);
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
			return WorldHelper.getPositionsFromBox(WorldHelper.getBroadBox(pos, side, radius));
		}

		@Nullable
		private Axis getAxis(BlockState state) {
			return state.hasProperty(RotatedPillarBlock.AXIS) ? state.getValue(RotatedPillarBlock.AXIS) : null;
		}
	}

	@FunctionalInterface
	public interface RayTracePointer {

		HitResult rayTrace(Level level, Player player, Fluid fluidMode);
	}

	public static class ChargeAttributeCache {

		private static final UUID CHARGE_MODIFIER = UUID.fromString("69ADE509-46FF-3725-92AC-F59FB052BEC7");

		//Note: It is an array map instead of hash map as the number of charges are very small by default
		private final Int2ObjectMap<Multimap<Attribute, AttributeModifier>> cachedMaps = new Int2ObjectArrayMap<>();

		public Multimap<Attribute, AttributeModifier> addChargeAttributeModifier(Multimap<Attribute, AttributeModifier> currentModifiers,
				@NotNull EquipmentSlot slot, ItemStack stack) {
			if (slot == EquipmentSlot.MAINHAND) {
				int charge = getCharge(stack);
				if (charge > 0) {
					return cachedMaps.computeIfAbsent(charge, c -> {
						Builder<Attribute, AttributeModifier> attributesBuilder = ImmutableMultimap.builder();
						attributesBuilder.putAll(currentModifiers);
						//If we have any charge take it into account for calculating the damage
						attributesBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(CHARGE_MODIFIER, "Charge modifier", c, Operation.ADDITION));
						return attributesBuilder.build();
					});
				}
			}
			return currentModifiers;
		}
	}
}