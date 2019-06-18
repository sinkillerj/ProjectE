package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.init.Particles;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PEToolBase extends ItemMode
{
	public static final float HAMMER_BASE_ATTACK = 13.0F;
	public static final float DARKSWORD_BASE_ATTACK = 12.0F;
	public static final float REDSWORD_BASE_ATTACK = 16.0F;
	public static final float STAR_BASE_ATTACK = 20.0F;
	public static final float KATAR_BASE_ATTACK = 23.0F;
	protected EnumMatterType peToolMaterial;
	protected final Set<Material> harvestMaterials = new HashSet<>();

	public PEToolBase(Properties props, byte numCharge, String[] modeDescrp)
	{
		super(props, numCharge, modeDescrp);
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, @Nonnull BlockState state)
	{
		return harvestMaterials.contains(state.getMaterial());
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if (this.peToolMaterial == EnumMatterType.DARK_MATTER)
		{
			if (canHarvestBlock(stack, state))
			{
				return 14.0f + (12.0f * this.getCharge(stack));
			}
		}
		else if (this.peToolMaterial == EnumMatterType.RED_MATTER)
		{
			if (canHarvestBlock(stack, state))
			{
				return 16.0f + (14.0f * this.getCharge(stack));
			}
		}
		return 1.0F;
	}

	/**
	 * Clears the given tag name in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	protected void clearTagAOE(World world, ItemStack stack, PlayerEntity player, Tag<Block> tag, long emcCost, Hand hand)
	{
		int charge = getCharge(stack);
		if (charge == 0 || world.isRemote || ProjectEConfig.items.disableAllRadiusMining.get())
		{
			return;
		}

		List<ItemStack> drops = new ArrayList<>();

		int scaled1 = 5 * charge;
		int scaled2 = 10 * charge;

		for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(player).add(-scaled1, -scaled2, -scaled1), new BlockPos(player).add(scaled1, scaled2, scaled1)))
		{
			BlockState state = world.getBlockState(pos);

			if (tag.contains(state.getBlock()))
			{
				List<ItemStack> blockDrops = WorldHelper.getBlockDrops(world, player, state, stack, pos);

				if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos)
						&& consumeFuel(player, stack, emcCost, true))
				{
					drops.addAll(blockDrops);
					world.removeBlock(pos);
					if (world.rand.nextInt(5) == 0)
					{
						((ServerWorld) world).spawnParticle(Particles.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0);
					}
				}
			}
		}

		WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
		PlayerHelper.swingItem(player, hand);
	}

	/**
	 * Tills in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	protected void tillAOE(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction sidehit, long emcCost)
	{
		int charge = this.getCharge(stack);
		boolean hasAction = false;
		boolean hasSoundPlayed = false;

		for (BlockPos newPos : BlockPos.getAllInBox(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge)))
		{
			BlockState state = world.getBlockState(newPos);
			BlockState stateAbove = world.getBlockState(newPos.up());
			Block block = state.getBlock();
			Block blockAbove = stateAbove.getBlock();

			if (!stateAbove.isOpaqueCube(world, pos) && (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT))
			{
				if (!hasSoundPlayed)
				{
					world.playSound(null, newPos, Blocks.FARMLAND.getSoundType().getStepSound(), SoundCategory.BLOCKS, (Blocks.FARMLAND.getSoundType().getVolume() + 1.0F) / 2.0F, Blocks.FARMLAND.getSoundType().getPitch() * 0.8F);
					hasSoundPlayed = true;
				}

				if (world.isRemote)
				{
					return;
				}
				else
				{
					ItemUseContext ctx = new ItemUseContext(player, stack, newPos, Direction.UP, 0, 0, 0);
					if (MinecraftForge.EVENT_BUS.post(new UseHoeEvent(ctx)))
					{
						continue;
					}

					// The initial block we target is always free
					if ((newPos.getX() == pos.getX() && newPos.getZ() == pos.getZ()) || consumeFuel(player, stack, emcCost, true))
					{
						PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) player), newPos, Blocks.FARMLAND.getDefaultState());

						if ((stateAbove.getMaterial() == Material.PLANTS || stateAbove.getMaterial() == Material.VINE)
								&& !(blockAbove.hasTileEntity(stateAbove)) // Just in case, you never know
								)
						{
							if (PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), newPos)) {
								world.destroyBlock(newPos.up(), true);
							}
						}

						if (!hasAction)
						{
							hasAction = true;
						}
					}
				}
			}
		}
		if (hasAction)
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}

	/**
	 * Called by multiple tools' left click function. Charge has no effect. Free operation.
	 */
	protected void digBasedOnMode(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity living)
	{
		if (world.isRemote || !(living instanceof PlayerEntity))
		{
			return;
		}

		PlayerEntity player = (PlayerEntity) living;
		byte mode = this.getMode(stack);

		if (mode == 0) // Standard
		{
			return;
		}

		RayTraceResult mop = this.rayTrace(world, player, false);

		if (mop == null || mop.type != RayTraceResult.Type.BLOCK)
		{
			return;
		}

		Direction direction = mop.sideHit;
		BlockPos hitPos = mop.getBlockPos();
		AxisAlignedBB box = new AxisAlignedBB(hitPos, hitPos);

		if (!ProjectEConfig.items.disableAllRadiusMining.get()) {
			switch (mode) {
				case 1: // 3x Tallshot
					box = new AxisAlignedBB(hitPos.offset(Direction.DOWN, 1), hitPos.offset(Direction.UP, 1)); break;
				case 2: // 3x Wideshot
					switch (direction.getAxis())
					{
						case X: box = new AxisAlignedBB(hitPos.offset(Direction.SOUTH), hitPos.offset(Direction.NORTH)); break;
						case Y:
							switch (player.getHorizontalFacing().getAxis())
							{
								case X: box = new AxisAlignedBB(hitPos.offset(Direction.SOUTH), hitPos.offset(Direction.NORTH)); break;
								case Z: box = new AxisAlignedBB(hitPos.offset(Direction.WEST), hitPos.offset(Direction.EAST)); break;
							}
							break;
						case Z: box = new AxisAlignedBB(hitPos.offset(Direction.WEST), hitPos.offset(Direction.EAST)); break;
					}
					break;
				case 3: // 3x Longshot
					box = new AxisAlignedBB(hitPos, hitPos.offset(direction.getOpposite(), 2)); break;
			}

		}

		List<ItemStack> drops = new ArrayList<>();

		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box))
		{
			BlockState state = world.getBlockState(digPos);
			Block b = state.getBlock();

			if (b != Blocks.AIR
					&& state.getBlockHardness(world, digPos) != -1
					&& (canHarvestBlock(stack, state) || ForgeHooks.canToolHarvestBlock(world, digPos, stack))
					&& PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), digPos))
			{
				// shulker boxes are implemented stupidly and drop whenever we set it to air, so don't dupe
				if (!(b instanceof ShulkerBoxBlock))
					drops.addAll(WorldHelper.getBlockDrops(world, player, state, stack, digPos));
				world.removeBlock(digPos);
			}
		}

		WorldHelper.createLootDrop(drops, world, pos);
	}

	/**
	 * Carves in an AOE. Charge affects the breadth and/or depth of the AOE. Optional per-block EMC cost.
	 */
	protected void digAOE(ItemStack stack, World world, PlayerEntity player, boolean affectDepth, long emcCost, Hand hand)
	{
		if (world.isRemote || this.getCharge(stack) == 0 || ProjectEConfig.items.disableAllRadiusMining.get())
		{
			return;
		}

		RayTraceResult mop = this.rayTrace(world, player, false);

		if (mop == null || mop.type != RayTraceResult.Type.BLOCK)
		{
			return;
		}

		AxisAlignedBB box = affectDepth ? WorldHelper.getBroadDeepBox(mop.getBlockPos(), mop.sideHit, this.getCharge(stack))
				: WorldHelper.getFlatYBox(mop.getBlockPos(), this.getCharge(stack));

		List<ItemStack> drops = new ArrayList<>();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
		{
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();

			if (b != Blocks.AIR && state.getBlockHardness(world, pos) != -1
					&& canHarvestBlock(stack, state)
					&& PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos)
					&& consumeFuel(player, stack, emcCost, true)
					)
			{
				// shulker boxes are implemented stupidly and drop whenever we set it to air, so don't dupe
				if (!(b instanceof ShulkerBoxBlock))
					drops.addAll(WorldHelper.getBlockDrops(world, player, state, stack, pos));
				world.removeBlock(pos);
			}
		}

		WorldHelper.createLootDrop(drops, world, mop.getBlockPos());
		PlayerHelper.swingItem(player, hand);

		if (!drops.isEmpty())
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}

	/**
	 * Attacks through armor. Charge affects damage. Free operation.
	 */
	protected void attackWithCharge(ItemStack stack, LivingEntity damaged, LivingEntity damager, float baseDmg)
	{
		if (!(damager instanceof PlayerEntity) || damager.getEntityWorld().isRemote)
		{
			return;
		}

		DamageSource dmg = DamageSource.causePlayerDamage((PlayerEntity) damager);
		int charge = this.getCharge(stack);
		float totalDmg = baseDmg;

		if (charge > 0)
		{
			dmg.setDamageBypassesArmor();
			totalDmg += charge;
		}

		damaged.attackEntityFrom(dmg, totalDmg);
	}

	/**
	 * Attacks in an AOE. Charge affects AOE, not damage (intentional). Optional per-entity EMC cost.
	 */
	protected void attackAOE(ItemStack stack, PlayerEntity player, boolean slayAll, float damage, long emcCost, Hand hand)
	{
		if (player.getEntityWorld().isRemote)
		{
			return;
		}

		int charge = getCharge(stack);
		float factor = 2.5F * charge;
		AxisAlignedBB aabb = player.getBoundingBox().grow(factor);
		List<Entity> toAttack = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, aabb);
		DamageSource src = DamageSource.causePlayerDamage(player);
		src.setDamageBypassesArmor();
		for (Entity entity : toAttack)
		{
			if (consumeFuel(player, stack, emcCost, true)) {
				if (entity instanceof IMob)
				{
					entity.attackEntityFrom(src, damage);
				}
				else if (entity instanceof LivingEntity && slayAll)
				{
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
	protected void shearBlock(ItemStack stack, BlockPos pos, PlayerEntity player)
	{
		if (player.getEntityWorld().isRemote)
		{
			return;
		}

		Block block = player.getEntityWorld().getBlockState(pos).getBlock();

		if (block instanceof IShearable)
		{
			IShearable target = (IShearable) block;

			if (target.isShearable(stack, player.getEntityWorld(), pos) && PlayerHelper.hasBreakPermission(((ServerPlayerEntity) player), pos))
			{
				List<ItemStack> drops = target.onSheared(stack, player.getEntityWorld(), pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

				WorldHelper.createLootDrop(drops, player.getEntityWorld(), pos);

				stack.damageItem(1, player);
				player.addStat(Stats.BLOCK_MINED.get(block), 1);
			}
		}
	}

	/**
	 * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
	 */
	protected void shearEntityAOE(ItemStack stack, PlayerEntity player, long emcCost, Hand hand)
	{
		World world = player.getEntityWorld();
		if (!world.isRemote)
		{
			int charge = this.getCharge(stack);

			int offset = ((int) Math.pow(2, 2 + charge));

			AxisAlignedBB bBox = player.getBoundingBox().grow(offset, offset / 2, offset);
			List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bBox);

			List<ItemStack> drops = new ArrayList<>();

			for (Entity ent : list)
			{
				if (!(ent instanceof IShearable)) {
					continue;
				}

				IShearable target = (IShearable) ent;

				if (target.isShearable(stack, ent.getEntityWorld(), new BlockPos(ent))
						&& consumeFuel(player, stack, emcCost, true)
						)
				{
					List<ItemStack> entDrops = target.onSheared(stack, ent.getEntityWorld(), new BlockPos(ent), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

					if (!entDrops.isEmpty())
					{
						for (ItemStack drop : entDrops)
						{
							drop.grow(drop.getCount());
						}

						drops.addAll(entDrops);
					}
				}
				if (Math.random() < 0.01)
				{
					Entity e = ent.getType().create(world);

					if (e != null)
					{
						e.setPosition(ent.posX, ent.posY, ent.posZ);
					}

					if (e instanceof MobEntity)
					{
						((MobEntity) e).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(ent)), null, null);
					}

					if (e instanceof SheepEntity)
					{
						((SheepEntity) e).setFleeceColor(DyeColor.values()[MathUtils.randomIntInRange(0, 15)]);
					}

					if (e instanceof AgeableEntity)
					{
						((AgeableEntity) e).setGrowingAge(-24000);
					}
					world.spawnEntity(e);
				}
			}

			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
			PlayerHelper.swingItem(player, hand);
		}
	}

	/**
	 * Scans and harvests an ore vein. This is called already knowing the mop is pointing at an ore or gravel.
	 */
	protected void tryVeinMine(ItemStack stack, PlayerEntity player, RayTraceResult mop)
	{
		if (player.getEntityWorld().isRemote || ProjectEConfig.items.disableAllRadiusMining.get())
		{
			return;
		}

		AxisAlignedBB aabb = WorldHelper.getBroadDeepBox(mop.getBlockPos(), mop.sideHit, getCharge(stack));
		BlockState target = player.getEntityWorld().getBlockState(mop.getBlockPos());
		if (target.getBlockHardness(player.getEntityWorld(), mop.getBlockPos()) <= -1 || !(canHarvestBlock(stack, target) || ForgeHooks.canToolHarvestBlock(player.getEntityWorld(), mop.getBlockPos(), stack)))
		{
			return;
		}

		List<ItemStack> drops = new ArrayList<>();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(aabb))
		{
			BlockState state = player.getEntityWorld().getBlockState(pos);
			if (target.getBlock() == state.getBlock())
			{
				WorldHelper.harvestVein(player.getEntityWorld(), player, stack, pos, state.getBlock(), drops, 0);
			}
		}

		WorldHelper.createLootDrop(drops, player.getEntityWorld(), mop.getBlockPos());
		if (!drops.isEmpty())
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}


	/**
	 * Mines all ore veins in a Box around the player.
	 */
	protected void mineOreVeinsInAOE(ItemStack stack, PlayerEntity player, Hand hand) {
		if (player.getEntityWorld().isRemote || ProjectEConfig.items.disableAllRadiusMining.get())
		{
			return;
		}
		int offset = this.getCharge(stack) + 3;
		AxisAlignedBB box = player.getBoundingBox().grow(offset);
		List<ItemStack> drops = new ArrayList<>();
		World world = player.getEntityWorld();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
		{
			BlockState state = world.getBlockState(pos);
			if (ItemHelper.isOre(state.getBlock()) && state.getBlockHardness(player.getEntityWorld(), pos) != -1 && (canHarvestBlock(stack, state) || ForgeHooks.canToolHarvestBlock(world, pos, stack)))
			{
				WorldHelper.harvestVein(world, player, stack, pos, state.getBlock(), drops, 0);
			}
		}

		if (!drops.isEmpty())
		{
			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ );
			PlayerHelper.swingItem(player, hand);
		}
	}
}
