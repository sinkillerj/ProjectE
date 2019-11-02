package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumMatterType;
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
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
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
public class ToolHelper {

	//TODO: Remove this modifier
	public static final UUID CHARGE_MODIFIER = UUID.fromString("69ADE509-46FF-3725-92AC-F59FB052BEC7");
	public static final ToolType TOOL_TYPE_HOE = ToolType.get("hoe");
	public static final ToolType TOOL_TYPE_SHEARS = ToolType.get("shears");
	public static final ToolType TOOL_TYPE_HAMMER = ToolType.get("hammer");
	public static final ToolType TOOL_TYPE_KATAR = ToolType.get("katar");
	public static final ToolType TOOL_TYPE_MORNING_STAR = ToolType.get("morning_star");

	/**
	 * Clears the given tag name in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static void clearTagAOE(World world, ItemStack stack, PlayerEntity player, Tag<Block> tag, long emcCost, Hand hand) {
		if (world.isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return;
		}
		int charge = getCharge(stack);
		if (charge == 0) {
			return;
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
	}

	/**
	 * Tills in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	public static ActionResultType tillAOE(Hand hand, PlayerEntity player, World world, BlockPos pos, Direction sidehit, long emcCost) {
		if (sidehit == Direction.DOWN) {
			//Don't allow hoeing a block from underneath
			return ActionResultType.PASS;
		}
		if (world.isRemote) {
			//If on client NO-OP as the sound gets played from the server anyways
			return ActionResultType.SUCCESS;
		}
		BlockState initialHoedState = HoeItem.HOE_LOOKUP.get(world.getBlockState(pos).getBlock());
		boolean ignoreInitial = initialHoedState == null;
		ItemStack stack = player.getHeldItem(hand);
		int charge = getCharge(stack);
		boolean hasAction = false;
		for (BlockPos newPos : BlockPos.getAllInBoxMutable(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge))) {
			BlockState stateAbove = world.getBlockState(newPos.up());
			if (!stateAbove.isOpaqueCube(world, newPos.up())) {
				BlockState hoedState = HoeItem.HOE_LOOKUP.get(world.getBlockState(newPos).getBlock());
				//Check to make sure the result we would get from hoeing the other block is the same as from our initial block
				// If we did not have an initial state, then just make sure the state we found is not null
				if (ignoreInitial ? hoedState != null : hoedState == initialHoedState) {
					//Some of the below methods don't behave properly when the blockpos is mutable, so now that we are onto ones where it may actually
					// matter we make sure to get an immutable instance of newPos
					BlockPos newPosImmutable = newPos.toImmutable();
					BlockRayTraceResult rtr = new BlockRayTraceResult(Vec3d.ZERO, Direction.UP, newPosImmutable, false);
					ItemUseContext context = new ItemUseContext(player, hand, rtr);
					int hoeUseResult = ForgeEventFactory.onHoeUse(context);
					if (hoeUseResult < 0) {
						//We were denied from using the hoe so continue to the next block
						continue;
					} else if (hoeUseResult > 0) {
						//Processing happened in the hook so we use our desired fuel amount if we are not at the initial position and continue
						if (newPosImmutable.getX() == pos.getX() && newPosImmutable.getZ() == pos.getZ()) {
							ItemPE.consumeFuel(player, stack, emcCost, true);
						}
						hasAction = true;
						continue;
					} //else we are allowed to figure out how to use the hoe
					// The initial block we target is always free
					if ((newPosImmutable.getX() == pos.getX() && newPosImmutable.getZ() == pos.getZ()) || ItemPE.consumeFuel(player, stack, emcCost, true)) {
						//Replace the block. Note it just directly sets it (in the same way that HoeItem does), rather than using our
						// checkedReplaceBlock so as to make the blocks not "blink" when getting changed. We don't bother using
						// checkedReplaceBlock as we already fired all the events/checks for seeing if we are allowed to use a hoe in this
						// location and were told that we are allowed to use a hoe.
						world.setBlockState(newPosImmutable, hoedState, 11);
						if ((stateAbove.getMaterial() == Material.PLANTS || stateAbove.getMaterial() == Material.TALL_PLANTS)
							&& !(stateAbove.getBlock().hasTileEntity(stateAbove))) {// Just in case, you never know
							//Note: We do check for breaking the block above that we attempt to break it though, as we
							// have not done any events that have told use we are allowed to break blocks in that spot.
							if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), newPosImmutable)) {
								world.destroyBlock(newPosImmutable.up(), true);
							}
						}
						if (!hasAction) {
							world.playSound(null, newPosImmutable, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
							hasAction = true;
						}
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
	public static void digAOE(ItemStack stack, World world, PlayerEntity player, boolean affectDepth, long emcCost, Hand hand, RayTracePointer tracePointer) {
		if (world.isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return;
		}
		int charge = getCharge(stack);
		if (charge == 0) {
			return;
		}

		RayTraceResult mop = tracePointer.rayTrace(world, player, FluidMode.NONE);
		if (!(mop instanceof BlockRayTraceResult)) {
			return;
		}

		BlockRayTraceResult rtr = (BlockRayTraceResult) mop;
		AxisAlignedBB box = affectDepth ? WorldHelper.getBroadDeepBox(rtr.getPos(), rtr.getFace(), charge) : WorldHelper.getFlatYBox(rtr.getPos(), charge);

		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();

			if (b != Blocks.AIR && state.getBlockHardness(world, pos) != -1 && stack.canHarvestBlock(state)
				&& PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos) && ItemPE.consumeFuel(player, stack, emcCost, true)) {
				drops.addAll(Block.getDrops(state, (ServerWorld) world, pos, world.getTileEntity(pos), player, stack));
				world.removeBlock(pos, false);
			}
		}

		WorldHelper.createLootDrop(drops, world, rtr.getPos());
		PlayerHelper.swingItem(player, hand);

		if (!drops.isEmpty()) {
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}

	/**
	 * Attacks through armor. Charge affects damage. Free operation.
	 */
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
	public static void shearBlock(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (player.getEntityWorld().isRemote) {
			return;
		}
		Block block = player.getEntityWorld().getBlockState(pos).getBlock();
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			if (target.isShearable(stack, player.getEntityWorld(), pos) && PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos)) {
				List<ItemStack> drops = target.onSheared(stack, player.getEntityWorld(), pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
				WorldHelper.createLootDrop(drops, player.getEntityWorld(), pos);
				player.addStat(Stats.BLOCK_MINED.get(block), 1);
			}
		}
	}

	/**
	 * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
	 */
	public static void shearEntityAOE(ItemStack stack, PlayerEntity player, long emcCost, Hand hand) {
		World world = player.getEntityWorld();
		if (!world.isRemote) {
			int charge = getCharge(stack);
			int offset = ((int) Math.pow(2, 2 + charge));

			AxisAlignedBB bBox = player.getBoundingBox().grow(offset, offset / 2, offset);
			List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bBox);
			List<ItemStack> drops = new ArrayList<>();
			for (Entity ent : list) {
				if (!(ent instanceof IShearable)) {
					continue;
				}
				IShearable target = (IShearable) ent;
				if (target.isShearable(stack, ent.getEntityWorld(), new BlockPos(ent)) && ItemPE.consumeFuel(player, stack, emcCost, true)) {
					List<ItemStack> entDrops = target.onSheared(stack, ent.getEntityWorld(), new BlockPos(ent), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
					if (!entDrops.isEmpty()) {
						for (ItemStack drop : entDrops) {
							drop.grow(drop.getCount());
						}
						drops.addAll(entDrops);
					}
				}
				if (Math.random() < 0.01) {
					Entity e = ent.getType().create(world);
					if (e != null) {
						e.setPosition(ent.posX, ent.posY, ent.posZ);
					}
					if (e instanceof MobEntity) {
						((MobEntity) e).onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(ent)), SpawnReason.EVENT, null, null);
					}
					if (e instanceof SheepEntity) {
						((SheepEntity) e).setFleeceColor(DyeColor.values()[MathUtils.randomIntInRange(0, 15)]);
					}
					if (e instanceof AgeableEntity) {
						((AgeableEntity) e).setGrowingAge(-24000);
					}
					world.addEntity(e);
				}
			}

			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
			PlayerHelper.swingItem(player, hand);
		}
	}

	/**
	 * Scans and harvests an ore vein.
	 */
	public static void tryVeinMine(ItemStack stack, PlayerEntity player, BlockRayTraceResult mop) {
		if (player.getEntityWorld().isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return;
		}

		AxisAlignedBB aabb = WorldHelper.getBroadDeepBox(mop.getPos(), mop.getFace(), getCharge(stack));
		BlockState target = player.getEntityWorld().getBlockState(mop.getPos());
		if (target.getBlockHardness(player.getEntityWorld(), mop.getPos()) <= -1 ||
			!(stack.canHarvestBlock(target) || ForgeHooks.canToolHarvestBlock(player.getEntityWorld(), mop.getPos(), stack))) {
			return;
		}

		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : WorldHelper.getPositionsFromBox(aabb)) {
			BlockState state = player.getEntityWorld().getBlockState(pos);
			if (target.getBlock() == state.getBlock()) {
				WorldHelper.harvestVein(player.getEntityWorld(), player, stack, pos, state.getBlock(), drops, 0);
			}
		}

		WorldHelper.createLootDrop(drops, player.getEntityWorld(), mop.getPos());
		if (!drops.isEmpty()) {
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}


	/**
	 * Mines all ore veins in a Box around the player.
	 */
	public static void mineOreVeinsInAOE(ItemStack stack, PlayerEntity player, Hand hand) {
		if (player.getEntityWorld().isRemote || ProjectEConfig.items.disableAllRadiusMining.get()) {
			return;
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
		}
	}

	public static float getDestroySpeed(float parentDestroySpeed, EnumMatterType matterType, int charge) {
		if (parentDestroySpeed == 1) {
			//If we cannot harvest the block leave the value be
			return parentDestroySpeed;
		}
		return parentDestroySpeed + matterType.getChargeModifier() * charge;
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
}