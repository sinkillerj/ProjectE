package moze_intel.projecte.utils;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;

//TODO: If some of these are only used by one tool type inline them
//TODO: Replace canHarvestBlock checks with ForgeHooks.canHarvestBlock ??
//TODO: Go through and improve the results these return (especially for when we are on the client side)
// This includes figuring out if we should be just returning pass or success on the client of if we can do any processing
public class ToolHelper {

	public static final UUID CHARGE_MODIFIER = UUID.fromString("69ADE509-46FF-3725-92AC-F59FB052BEC7");
	public static final ToolType TOOL_TYPE_HOE = ToolType.get("hoe");
	public static final ToolType TOOL_TYPE_SHEARS = ToolType.get("shears");
	public static final ToolType TOOL_TYPE_HAMMER = ToolType.get("hammer");
	public static final ToolType TOOL_TYPE_KATAR = ToolType.get("katar");
	public static final ToolType TOOL_TYPE_MORNING_STAR = ToolType.get("morning_star");

	private static final Predicate<Entity> SHEARABLE_NOT_SPECTATING = entity -> !entity.isSpectator() && entity instanceof IShearable;

	public static Multimap<String, AttributeModifier> addChargeAttributeModifier(Multimap<String, AttributeModifier> currentModifiers, @Nonnull EquipmentSlotType slot, ItemStack stack) {
		if (slot == EquipmentSlotType.MAINHAND) {
			int charge = getCharge(stack);
			if (charge > 0) {
				//If we have any charge take it into account for calculating the damage
				currentModifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(CHARGE_MODIFIER, "Charge modifier", charge, Operation.ADDITION));
			}
		}
		return currentModifiers;
	}

	public static ActionResultType performActions(ActionResultType firstAction, ActionSupplier... secondaryActions) {
		if (firstAction == ActionResultType.SUCCESS) {
			return ActionResultType.SUCCESS;
		}
		ActionResultType result = firstAction;
		boolean hasFailed = result == ActionResultType.FAIL;
		for (ActionSupplier secondaryAction : secondaryActions) {
			result = secondaryAction.get();
			if (result == ActionResultType.SUCCESS) {
				//If we were successful
				return ActionResultType.SUCCESS;
			}
			hasFailed |= result == ActionResultType.FAIL;
		}
		//TODO: Decide if this should only be fail if ALL of them failed
		if (hasFailed) {
			//If at least one step failed, consider ourselves unsuccessful
			return ActionResultType.FAIL;
		}
		return result;
	}

	/**
	 * Clears the given tag name in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	//TODO: Evaluate/modernize
	public static ActionResultType clearTagAOE(World world, ItemStack stack, PlayerEntity player, Tag<Block> tag, long emcCost, Hand hand) {
		if (world.isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return ActionResultType.PASS;
		}
		int charge = getCharge(stack);
		if (charge == 0) {
			return ActionResultType.PASS;
		}

		List<ItemStack> drops = new ArrayList<>();

		int scaled1 = 5 * charge;
		int scaled2 = 10 * charge;

		BlockPos.getAllInBox(new BlockPos(player).add(-scaled1, -scaled2, -scaled1), new BlockPos(player).add(scaled1, scaled2, scaled1)).forEach(pos -> {
			BlockState state = world.getBlockState(pos);

			if (tag.contains(state.getBlock())) {
				List<ItemStack> blockDrops = Block.getDrops(state, (ServerWorld) world, pos, world.getTileEntity(pos), player, stack);

				if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos) && ItemPE.consumeFuel(player, stack, emcCost, true)) {
					drops.addAll(blockDrops);
					world.removeBlock(pos, false);
					if (world.rand.nextInt(5) == 0) {
						((ServerWorld) world).spawnParticle(ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0);
					}
				}
			}
		});

		WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
		PlayerHelper.swingItem(player, hand);
		return ActionResultType.SUCCESS;
	}

	/**
	 * Tills in an AOE using a hoe. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static ActionResultType tillHoeAOE(Hand hand, PlayerEntity player, World world, BlockPos pos, Direction sideHit, long emcCost) {
		return tillAOE(hand, player, world, pos, sideHit, emcCost, HoeItem.HOE_LOOKUP, ForgeEventFactory::onHoeUse, SoundEvents.ITEM_HOE_TILL);
	}

	/**
	 * Tills in an AOE using a shovel (ex: grass to grass path). Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static ActionResultType tillShovelAOE(Hand hand, PlayerEntity player, World world, BlockPos pos, Direction sideHit, long emcCost) {
		//TODO: ForgeEventFactory::onShovelUse https://github.com/MinecraftForge/MinecraftForge/pull/6294
		// For now it just pretends it is always valid
		return tillAOE(hand, player, world, pos, sideHit, emcCost, ShovelItem.field_195955_e, context -> 0, SoundEvents.ITEM_SHOVEL_FLATTEN);
	}

	/**
	 * Tills in an AOE using the specified lookup map and tester. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static ActionResultType tillAOE(Hand hand, PlayerEntity player, World world, BlockPos pos, Direction sideHit, long emcCost, Map<Block, BlockState> lookup,
			ToIntFunction<ItemUseContext> onItemUse, SoundEvent sound) {
		if (sideHit == Direction.DOWN) {
			//Don't allow tilling a block from underneath
			return ActionResultType.PASS;
		}
		BlockState tilledState = lookup.get(world.getBlockState(pos).getBlock());
		if (tilledState == null) {
			//Skip tilling the block if the one we clicked cannot be tilled
			return ActionResultType.PASS;
		}
		if (world.isRemote) {
			//If on client NO-OP as the sound gets played from the server anyways
			//TODO: Re-evaluate this as we may want to return pass if nothing happened?
			return ActionResultType.SUCCESS;
		}
		ItemStack stack = player.getHeldItem(hand);
		int charge = getCharge(stack);
		boolean hasAction = false;
		for (BlockPos newPos : BlockPos.getAllInBoxMutable(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge))) {
			BlockState stateAbove = world.getBlockState(newPos.up());
			//Check to make sure the block above is not opaque and that the result we would get from tilling the other block is
			// the same as the one we got on the initial block we interacted with
			if (!stateAbove.isOpaqueCube(world, newPos.up()) && tilledState == lookup.get(world.getBlockState(newPos).getBlock())) {
				//Some of the below methods don't behave properly when the blockpos is mutable, so now that we are onto ones where it may actually
				// matter we make sure to get an immutable instance of newPos
				BlockPos newPosImmutable = newPos.toImmutable();
				ItemUseContext context = new ItemUseContext(player, hand, new BlockRayTraceResult(Vec3d.ZERO, Direction.UP, newPosImmutable, false));
				int useResult = onItemUse.applyAsInt(context);
				if (useResult < 0) {
					//We were denied from using the item so continue to the next block
					continue;
				} else if (useResult > 0) {
					//Processing happened in the hook so we use our desired fuel amount if we are not at the initial position and continue
					if (newPosImmutable.getX() == pos.getX() && newPosImmutable.getZ() == pos.getZ()) {
						ItemPE.consumeFuel(player, stack, emcCost, true);
					}
					hasAction = true;
					continue;
				} //else we are allowed to use the item
				// The initial block we target is always free
				if ((newPosImmutable.getX() == pos.getX() && newPosImmutable.getZ() == pos.getZ()) || ItemPE.consumeFuel(player, stack, emcCost, true)) {
					//Replace the block. Note it just directly sets it (in the same way that HoeItem/ShovelItem do), rather than using our
					// checkedReplaceBlock so as to make the blocks not "blink" when getting changed. We don't bother using
					// checkedReplaceBlock as we already fired all the events/checks for seeing if we are allowed to use this item in this
					// location and were told that we are allowed to use our item.
					world.setBlockState(newPosImmutable, tilledState, 11);
					Material aboveMaterial = stateAbove.getMaterial();
					if ((aboveMaterial == Material.PLANTS || aboveMaterial == Material.TALL_PLANTS) && !stateAbove.getBlock().hasTileEntity(stateAbove)) {
						//If the block above the one we tilled is a plant (and not a tile entity because you never know), then we try to remove it
						//Note: We do check for breaking the block above that we attempt to break it though, as we
						// have not done any events that have told use we are allowed to break blocks in that spot.
						if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), newPosImmutable)) {
							world.destroyBlock(newPosImmutable.up(), true);
						}
					}
					if (!hasAction) {
						world.playSound(null, newPosImmutable, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
						hasAction = true;
					}
				}
			}
		}
		if (hasAction) {
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	/**
	 * Called by multiple tools' left click function. Charge has no effect. Free operation.
	 */
	//TODO: Evaluate/modernize
	public static void digBasedOnMode(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity living, RayTracePointer tracePointer) {
		if (world.isRemote || !(living instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) living;
		byte mode = getMode(stack);

		if (mode == 0) {
			//Standard
			return;
		}

		RayTraceResult mop = tracePointer.rayTrace(world, player, RayTraceContext.FluidMode.NONE);

		if (!(mop instanceof BlockRayTraceResult)) {
			//TODO: Evaluate if this is needed if we can just get this via the ItemUseContext
			return;
		}

		Direction direction = ((BlockRayTraceResult) mop).getFace();
		BlockPos hitPos = ((BlockRayTraceResult) mop).getPos();
		AxisAlignedBB box = new AxisAlignedBB(hitPos, hitPos);

		if (!ProjectEConfig.items.disableAllRadiusMining.get()) {
			switch (mode) {
				case 1: // 3x Tallshot
					box = new AxisAlignedBB(hitPos.offset(Direction.DOWN, 1), hitPos.offset(Direction.UP, 1));
					break;
				case 2: // 3x Wideshot
					switch (direction.getAxis()) {
						case X:
							box = new AxisAlignedBB(hitPos.offset(Direction.SOUTH), hitPos.offset(Direction.NORTH));
							break;
						case Y:
							switch (player.getHorizontalFacing().getAxis()) {
								case X:
									box = new AxisAlignedBB(hitPos.offset(Direction.SOUTH), hitPos.offset(Direction.NORTH));
									break;
								case Z:
									box = new AxisAlignedBB(hitPos.offset(Direction.WEST), hitPos.offset(Direction.EAST));
									break;
							}
							break;
						case Z:
							box = new AxisAlignedBB(hitPos.offset(Direction.WEST), hitPos.offset(Direction.EAST));
							break;
					}
					break;
				case 3: // 3x Longshot
					box = new AxisAlignedBB(hitPos, hitPos.offset(direction.getOpposite(), 2));
					break;
			}

		}

		List<ItemStack> drops = new ArrayList<>();

		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(digPos);
			Block b = state.getBlock();

			if (b != Blocks.AIR && state.getBlockHardness(world, digPos) != -1 && (stack.canHarvestBlock(state) || ForgeHooks.canToolHarvestBlock(world, digPos, stack))
				&& PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), digPos)) {
				drops.addAll(Block.getDrops(state, (ServerWorld) world, digPos, world.getTileEntity(digPos), player, stack));
				world.removeBlock(digPos, false);
			}
		}

		WorldHelper.createLootDrop(drops, world, pos);
	}

	/**
	 * Carves in an AOE. Charge affects the breadth and/or depth of the AOE. Optional per-block EMC cost.
	 */
	//TODO: Evaluate/modernize
	@Deprecated //Replace usages with other method
	public static ActionResultType digAOE(World world, PlayerEntity player, boolean affectDepth, long emcCost, Hand hand, RayTracePointer tracePointer) {
		RayTraceResult mop = tracePointer.rayTrace(world, player, FluidMode.NONE);
		if (!(mop instanceof BlockRayTraceResult)) {
			//TODO: Evaluate if this is needed if we can just get this via the ItemUseContext
			return ActionResultType.PASS;
		}
		BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
		return digAOE(world, player, hand, rtr.getPos(), rtr.getFace(), affectDepth, emcCost);
	}

	/**
	 * Carves in an AOE. Charge affects the breadth and/or depth of the AOE. Optional per-block EMC cost.
	 */
	//TODO: Evaluate/modernize
	public static ActionResultType digAOE(World world, PlayerEntity player, Hand hand, BlockPos pos, Direction sideHit, boolean affectDepth, long emcCost) {
		if (world.isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return ActionResultType.PASS;
		}
		ItemStack stack = player.getHeldItem(hand);
		int charge = getCharge(stack);
		if (charge == 0) {
			return ActionResultType.PASS;
		}

		AxisAlignedBB box = affectDepth ? WorldHelper.getBroadDeepBox(pos, sideHit, charge) : WorldHelper.getFlatYBox(pos, charge);

		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos newPos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(newPos);
			Block b = state.getBlock();

			if (b != Blocks.AIR && state.getBlockHardness(world, newPos) != -1 && stack.canHarvestBlock(state)
				&& PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), newPos) && ItemPE.consumeFuel(player, stack, emcCost, true)) {
				drops.addAll(Block.getDrops(state, (ServerWorld) world, newPos, world.getTileEntity(newPos), player, stack));
				world.removeBlock(newPos, false);
			}
		}
		if (!drops.isEmpty()) {
			WorldHelper.createLootDrop(drops, world, pos);
			PlayerHelper.swingItem(player, hand);
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	/**
	 * Attacks through armor. Charge affects damage. Free operation.
	 */
	//TODO: Evaluate/modernize
	public static void attackWithCharge(ItemStack stack, LivingEntity damaged, LivingEntity damager, float baseDmg) {
		if (!(damager instanceof PlayerEntity) || damager.getEntityWorld().isRemote) {
			return;
		}
		DamageSource dmg = DamageSource.causePlayerDamage((PlayerEntity) damager);
		int charge = getCharge(stack);
		float totalDmg = baseDmg;
		if (charge > 0) {
			dmg.setDamageBypassesArmor();
			totalDmg += charge;
		}
		damaged.attackEntityFrom(dmg, totalDmg);
	}

	/**
	 * Attacks in an AOE. Charge affects AOE, not damage (intentional). Optional per-entity EMC cost.
	 */
	//TODO: Evaluate/modernize
	public static void attackAOE(ItemStack stack, PlayerEntity player, boolean slayAll, float damage, long emcCost, Hand hand) {
		if (player.getEntityWorld().isRemote) {
			return;
		}
		int charge = getCharge(stack);
		float factor = 2.5F * charge;
		AxisAlignedBB aabb = player.getBoundingBox().grow(factor);
		List<Entity> toAttack = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, aabb);
		DamageSource src = DamageSource.causePlayerDamage(player);
		src.setDamageBypassesArmor();
		for (Entity entity : toAttack) {
			if (ItemPE.consumeFuel(player, stack, emcCost, true)) {
				if (entity instanceof IMob) {
					entity.attackEntityFrom(src, damage);
				} else if (entity instanceof LivingEntity && slayAll) {
					entity.attackEntityFrom(src, damage);
				}
			}
		}
		player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
		PlayerHelper.swingItem(player, hand);
	}

	/**
	 * Called when tools that act as shears start breaking a block. Free operation.
	 */
	//TODO: Evaluate/modernize
	public static ActionResultType shearBlock(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (player.getEntityWorld().isRemote) {
			return ActionResultType.PASS;
		}
		Block block = player.getEntityWorld().getBlockState(pos).getBlock();
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			if (target.isShearable(stack, player.getEntityWorld(), pos) && PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos)) {
				List<ItemStack> drops = target.onSheared(stack, player.getEntityWorld(), pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
				if (!drops.isEmpty()) {
					WorldHelper.createLootDrop(drops, player.getEntityWorld(), pos);
					player.addStat(Stats.BLOCK_MINED.get(block), 1);
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	/**
	 * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
	 */
	public static ActionResultType shearEntityAOE(PlayerEntity player, Hand hand, long emcCost) {
		World world = player.getEntityWorld();
		if (world.isRemote) {
			return ActionResultType.PASS;
		}
		ItemStack stack = player.getHeldItem(hand);
		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
		int offset = (int) Math.pow(2, 2 + getCharge(stack));
		//Note: There is no division issue here as we are a power of 2
		AxisAlignedBB bBox = player.getBoundingBox().grow(offset, offset / 2, offset);
		//Get all entities also making sure that they are shearable
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bBox, SHEARABLE_NOT_SPECTATING);
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (Entity ent : list) {
			BlockPos entityPosition = ent.getPosition();
			IShearable target = (IShearable) ent;
			if (target.isShearable(stack, world, entityPosition) && ItemPE.consumeFuel(player, stack, emcCost, true)) {
				List<ItemStack> entDrops = target.onSheared(stack, world, entityPosition, fortune);
				if (!entDrops.isEmpty()) {
					//Double all drops (just add them all twice because we compact the list later anyways)
					//Note: The reason we don't grow the stacks like we used to is to ensure if a modded mob drops
					// items with over half their max stack size, we don't end up potentially messing up the logic
					// in the stack/trying to spawn in overly full stacks
					drops.addAll(entDrops);
					drops.addAll(entDrops);
				}
				if (!hasAction) {
					hasAction = true;
				}
			}
			if (Math.random() < 0.01) {
				Entity e = ent.getType().create(world);
				if (e != null) {
					e.setPosition(ent.posX, ent.posY, ent.posZ);
					if (e instanceof MobEntity) {
						((MobEntity) e).onInitialSpawn(world, world.getDifficultyForLocation(entityPosition), SpawnReason.EVENT, null, null);
					}
					if (e instanceof SheepEntity) {
						((SheepEntity) e).setFleeceColor(DyeColor.byId(MathUtils.randomIntInRange(0, 15)));
					}
					if (e instanceof AgeableEntity) {
						((AgeableEntity) e).setGrowingAge(-24000);
					}
					world.addEntity(e);
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
			PlayerHelper.swingItem(player, hand);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	/**
	 * Scans and harvests an ore vein.
	 */
	//TODO: Evaluate/modernize
	@Deprecated //Replace this with the below method
	public static ActionResultType tryVeinMine(Hand hand, PlayerEntity player, BlockRayTraceResult mop) {
		return tryVeinMine(hand, player, mop.getPos(), mop.getFace());
	}

	/**
	 * Scans and harvests an ore vein.
	 */
	//TODO: Evaluate/modernize
	public static ActionResultType tryVeinMine(Hand hand, PlayerEntity player, BlockPos pos, Direction sideHit) {
		if (player.getEntityWorld().isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return ActionResultType.PASS;
		}
		ItemStack stack = player.getHeldItem(hand);
		AxisAlignedBB aabb = WorldHelper.getBroadDeepBox(pos, sideHit, getCharge(stack));
		BlockState target = player.getEntityWorld().getBlockState(pos);
		if (target.getBlockHardness(player.getEntityWorld(), pos) <= -1 ||
			!(stack.canHarvestBlock(target) || ForgeHooks.canToolHarvestBlock(player.getEntityWorld(), pos, stack))) {
			return ActionResultType.FAIL;
		}

		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos newPos : WorldHelper.getPositionsFromBox(aabb)) {
			BlockState state = player.getEntityWorld().getBlockState(newPos);
			if (target.getBlock() == state.getBlock()) {
				if (WorldHelper.harvestVein(player.getEntityWorld(), player, stack, newPos, state.getBlock(), drops, 0) > 0) {
					if (!hasAction) {
						hasAction = true;
					}
				}
			}
		}
		if (hasAction) {
			WorldHelper.createLootDrop(drops, player.getEntityWorld(), pos);
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	/**
	 * Mines all ore veins in a Box around the player.
	 */
	//TODO: Evaluate/modernize
	public static ActionResultType mineOreVeinsInAOE(ItemStack stack, PlayerEntity player, Hand hand) {
		if (player.getEntityWorld().isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return ActionResultType.PASS;
		}
		int offset = getCharge(stack) + 3;
		AxisAlignedBB box = player.getBoundingBox().grow(offset);
		List<ItemStack> drops = new ArrayList<>();
		World world = player.getEntityWorld();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(pos);
			if (ItemHelper.isOre(state.getBlock()) && state.getBlockHardness(player.getEntityWorld(), pos) != -1 &&
				(stack.canHarvestBlock(state) || ForgeHooks.canToolHarvestBlock(world, pos, stack))) {
				WorldHelper.harvestVein(world, player, stack, pos, state.getBlock(), drops, 0);
			}
		}
		if (!drops.isEmpty()) {
			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
			PlayerHelper.swingItem(player, hand);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	public static float getDestroySpeed(float parentDestroySpeed, EnumMatterType matterType, int charge) {
		if (parentDestroySpeed == 1) {
			//If we cannot harvest the block leave the value be
			return parentDestroySpeed;
		}
		return parentDestroySpeed + matterType.getChargeModifier() * charge;
	}

	public static boolean canMatterMine(EnumMatterType matterType, Block block) {
		return block instanceof IMatterBlock && ((IMatterBlock) block).getMatterType().getMatterTier() <= matterType.getMatterTier();
	}

	private static int getCharge(ItemStack stack) {
		return LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.CHARGE_ITEM_CAPABILITY)).map(itemCharge -> itemCharge.getCharge(stack)).orElse(0);
	}

	private static byte getMode(ItemStack stack) {
		return LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY)).map(itemMode -> itemMode.getMode(stack)).orElse((byte) 0);
	}

	@FunctionalInterface
	public interface RayTracePointer {

		RayTraceResult rayTrace(World world, PlayerEntity player, FluidMode fluidMode);
	}

	//Helper interface to remove warnings of varargs of generics
	public interface ActionSupplier extends Supplier<ActionResultType> {
	}
}