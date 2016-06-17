package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class PEToolBase extends ItemMode
{
	public static final float HAMMER_BASE_ATTACK = 13.0F;
	public static final float DARKSWORD_BASE_ATTACK = 12.0F;
	public static final float REDSWORD_BASE_ATTACK = 16.0F;
	public static final float STAR_BASE_ATTACK = 20.0F;
	public static final float KATAR_BASE_ATTACK = 23.0F;
	protected String pePrimaryToolClass;
	protected String peToolMaterial;
	protected final Set<Material> harvestMaterials;
	protected final Set<String> secondaryClasses;

	public PEToolBase(String unlocalName, byte numCharge, String[] modeDescrp)
	{
		super(unlocalName, numCharge, modeDescrp);
		harvestMaterials = Sets.newHashSet();
		secondaryClasses = Sets.newHashSet();
	}

	@Override
	public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack)
	{
		return harvestMaterials.contains(state.getMaterial());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public int getHarvestLevel(ItemStack stack, @Nonnull String toolClass)
	{
		if (this.pePrimaryToolClass.equals(toolClass) || this.secondaryClasses.contains(toolClass))
		{
			return 4; // TiCon
		}
		return -1;
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		if ("dm_tools".equals(this.peToolMaterial))
		{
			if (canHarvestBlock(state, stack))
			{
				return 14.0f + (12.0f * this.getCharge(stack));
			}
		}
		else if ("rm_tools".equals(this.peToolMaterial))
		{
			if (canHarvestBlock(state, stack))
			{
				return 16.0f + (14.0f * this.getCharge(stack));
			}
		}
		return 1.0F;
	}

	/**
	 * Clears the given OD name in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	protected void clearOdAOE(World world, ItemStack stack, EntityPlayer player, String odName, int emcCost)
	{
		byte charge = getCharge(stack);
		if (charge == 0 || world.isRemote || ProjectEConfig.disableAllRadiusMining)
		{
			return;
		}

		List<ItemStack> drops = Lists.newArrayList();

		int scaled1 = 5 * charge;
		int scaled2 = 10 * charge;

		for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(player).add(-scaled1, -scaled2, -scaled1), new BlockPos(player).add(scaled1, scaled2, scaled1)))
		{
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if (block.isAir(state, world, pos) || Item.getItemFromBlock(block) == null)
			{
				continue;
			}

			ItemStack s = new ItemStack(block);
			int[] oreIds = OreDictionary.getOreIDs(s);

			String oreName;
			if (oreIds.length == 0)
			{
				if (block == Blocks.BROWN_MUSHROOM_BLOCK || block == Blocks.RED_MUSHROOM_BLOCK)
				{
					oreName = "logWood";
				}
				else
				{
					continue;
				}
			}
			else {
				oreName = OreDictionary.getOreName(oreIds[0]);
			}

			if (odName.equals(oreName))
			{
				List<ItemStack> blockDrops = WorldHelper.getBlockDrops(world, player, state, stack, pos);

				if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos)
						&& consumeFuel(player, stack, emcCost, true))
				{
					drops.addAll(blockDrops);
					world.setBlockToAir(pos);
					if (world.rand.nextInt(5) == 0)
					{
						PacketHandler.sendToAllAround(new ParticlePKT(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY() + 1, pos.getZ(), 32));
					}
				}
			}
		}

		WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
		PlayerHelper.swingItem(player);
	}

	/**
	 * Tills in an AOE. Charge affects the AOE. Optional per-block EMC cost.
	 */
	protected void tillAOE(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing sidehit, int emcCost)
	{
		byte charge = this.getCharge(stack);
		boolean hasAction = false;
		boolean hasSoundPlayed = false;

		for (BlockPos newPos : BlockPos.getAllInBox(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge)))
		{
			IBlockState state = world.getBlockState(newPos);
			IBlockState stateAbove = world.getBlockState(newPos.up());
			Block block = state.getBlock();
			Block blockAbove = stateAbove.getBlock();

			if (!stateAbove.isOpaqueCube() && (block == Blocks.GRASS || block == Blocks.DIRT))
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
					if (MinecraftForge.EVENT_BUS.post(new UseHoeEvent(player, stack, world, newPos)))
					{
						continue;
					}

					// The initial block we target is always free
					if ((newPos.getX() == pos.getX() && newPos.getZ() == pos.getZ()) || consumeFuel(player, stack, emcCost, true))
					{
						PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), newPos, Blocks.FARMLAND.getDefaultState());

						if ((stateAbove.getMaterial() == Material.PLANTS || stateAbove.getMaterial() == Material.VINE)
								&& !(blockAbove.hasTileEntity(stateAbove)) // Just in case, you never know
								)
						{
							if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), newPos)) {
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
			player.worldObj.playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}

	/**
	 * Called by multiple tools' left click function. Charge has no effect. Free operation.
	 */
	protected void digBasedOnMode(ItemStack stack, World world, Block block, BlockPos pos, EntityLivingBase living)
	{
		if (world.isRemote || !(living instanceof EntityPlayer))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer) living;
		byte mode = this.getMode(stack);

		if (mode == 0) // Standard
		{
			return;
		}

		RayTraceResult mop = this.rayTrace(world, player, false);

		if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
		{
			return;
		}

		EnumFacing direction = mop.sideHit;
		BlockPos hitPos = mop.getBlockPos();
		AxisAlignedBB box = new AxisAlignedBB(hitPos, hitPos);

		if (!ProjectEConfig.disableAllRadiusMining) {
			switch (mode) {
				case 1: // 3x Tallshot
					box = new AxisAlignedBB(hitPos.offset(EnumFacing.DOWN, 1), hitPos.offset(EnumFacing.UP, 1)); break;
				case 2: // 3x Wideshot
					switch (direction.getAxis())
					{
						case X: box = new AxisAlignedBB(hitPos.offset(EnumFacing.SOUTH), hitPos.offset(EnumFacing.NORTH)); break;
						case Y:
							switch (player.getHorizontalFacing().getAxis())
							{
								case X: box = new AxisAlignedBB(hitPos.offset(EnumFacing.SOUTH), hitPos.offset(EnumFacing.NORTH)); break;
								case Z: box = new AxisAlignedBB(hitPos.offset(EnumFacing.WEST), hitPos.offset(EnumFacing.EAST)); break;
							}
							break;
						case Z: box = new AxisAlignedBB(hitPos.offset(EnumFacing.WEST), hitPos.offset(EnumFacing.EAST)); break;
					}
					break;
				case 3: // 3x Longshot
					box = new AxisAlignedBB(hitPos, hitPos.offset(direction.getOpposite(), 2)); break;
			}

		}

		List<ItemStack> drops = Lists.newArrayList();

		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box))
		{
			IBlockState state = world.getBlockState(digPos);
			Block b = state.getBlock();

			if (b != Blocks.AIR
					&& state.getBlockHardness(world, digPos) != -1
					&& PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), digPos)
					&& (canHarvestBlock(state, stack) || ForgeHooks.canToolHarvestBlock(world, digPos, stack)))
			{
				drops.addAll(WorldHelper.getBlockDrops(world, player, state, stack, digPos));
				world.setBlockToAir(digPos);
			}
		}

		WorldHelper.createLootDrop(drops, world, pos);
	}

	/**
	 * Carves in an AOE. Charge affects the breadth and/or depth of the AOE. Optional per-block EMC cost.
	 */
	protected void digAOE(ItemStack stack, World world, EntityPlayer player, boolean affectDepth, int emcCost)
	{
		if (world.isRemote || this.getCharge(stack) == 0 || ProjectEConfig.disableAllRadiusMining)
		{
			return;
		}

		RayTraceResult mop = this.rayTrace(world, player, false);

		if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
		{
			return;
		}

		AxisAlignedBB box = affectDepth ? WorldHelper.getBroadDeepBox(mop.getBlockPos(), mop.sideHit, this.getCharge(stack))
				: WorldHelper.getFlatYBox(mop.getBlockPos(), this.getCharge(stack));

		List<ItemStack> drops = Lists.newArrayList();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
		{
			IBlockState state = world.getBlockState(pos);
			Block b = state.getBlock();

			if (b != Blocks.AIR && state.getBlockHardness(world, pos) != -1
					&& canHarvestBlock(state, stack)
					&& PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos)
					&& consumeFuel(player, stack, emcCost, true)
					)
			{
				drops.addAll(WorldHelper.getBlockDrops(world, player, state, stack, pos));
				world.setBlockToAir(pos);
			}
		}

		WorldHelper.createLootDrop(drops, world, mop.getBlockPos());
		PlayerHelper.swingItem(player);

		if (!drops.isEmpty())
		{
			player.worldObj.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}

	/**
	 * Attacks through armor. Charge affects damage. Free operation.
	 */
	protected void attackWithCharge(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager, float baseDmg)
	{
		if (!(damager instanceof EntityPlayer) || damager.worldObj.isRemote)
		{
			return;
		}

		DamageSource dmg = DamageSource.causePlayerDamage((EntityPlayer) damager);
		byte charge = this.getCharge(stack);
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
	protected void attackAOE(ItemStack stack, EntityPlayer player, boolean slayAll, float damage, int emcCost)
	{
		if (player.worldObj.isRemote)
		{
			return;
		}

		byte charge = getCharge(stack);
		float factor = 2.5F * charge;
		AxisAlignedBB aabb = player.getEntityBoundingBox().expand(factor, factor, factor);
		List<Entity> toAttack = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, aabb);
		DamageSource src = DamageSource.causePlayerDamage(player);
		src.setDamageBypassesArmor();
		for (Entity entity : toAttack)
		{
			if (consumeFuel(player, stack, emcCost, true)) {
				if (entity instanceof IMob)
				{
					entity.attackEntityFrom(src, damage);
				}
				else if (entity instanceof EntityLivingBase && slayAll)
				{
					entity.attackEntityFrom(src, damage);
				}
			}
		}
		player.worldObj.playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
		PlayerHelper.swingItem(player);
	}

	/**
	 * Called when tools that act as shears start breaking a block. Free operation.
	 */
	protected void shearBlock(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if (player.worldObj.isRemote)
		{
			return;
		}

		Block block = player.worldObj.getBlockState(pos).getBlock();

		if (block instanceof IShearable)
		{
			IShearable target = (IShearable) block;

			if (target.isShearable(stack, player.worldObj, pos) && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos))
			{
				List<ItemStack> drops = target.onSheared(stack, player.worldObj, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
				Random rand = new Random();

				for(ItemStack drop : drops)
				{
					float f = 0.7F;
					double d = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)pos.getX() + d, (double)pos.getY() + d1, (double)pos.getZ() + d2, drop);
					entityitem.setPickupDelay(10);
					player.worldObj.spawnEntityInWorld(entityitem);
				}

				stack.damageItem(1, player);
				player.addStat(StatList.getBlockStats(block), 1);
			}
		}
	}

	/**
	 * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
	 */
	protected void shearEntityAOE(ItemStack stack, EntityPlayer player, int emcCost)
	{
		World world = player.worldObj;
		if (!world.isRemote)
		{
			byte charge = this.getCharge(stack);

			int offset = ((int) Math.pow(2, 2 + charge));

			AxisAlignedBB bBox = player.getEntityBoundingBox().expand(offset, offset / 2, offset);
			List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bBox);

			List<ItemStack> drops = Lists.newArrayList();

			for (Entity ent : list)
			{
				if (!(ent instanceof IShearable)) {
					continue;
				}

				IShearable target = (IShearable) ent;

				if (target.isShearable(stack, ent.worldObj, new BlockPos(ent))
						&& consumeFuel(player, stack, emcCost, true)
						)
				{
					List<ItemStack> entDrops = target.onSheared(stack, ent.worldObj, new BlockPos(ent), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

					if (!entDrops.isEmpty())
					{
						for (ItemStack drop : entDrops)
						{
							drop.stackSize *= 2;
						}

						drops.addAll(entDrops);
					}
				}
				if (Math.random() < 0.01)
				{
					Entity e = EntityList.createEntityByName(EntityList.getEntityString(ent), world);
					NBTTagCompound tag = new NBTTagCompound();
					e.writeToNBT(tag);
					e.readFromNBT(tag);
					if (e instanceof EntitySheep)
					{
						((EntitySheep) e).setFleeceColor(EnumDyeColor.values()[MathUtils.randomIntInRange(0, 15)]);
					}
					if (e instanceof EntityAgeable)
					{
						((EntityAgeable) e).setGrowingAge(-24000);
					}
					world.spawnEntityInWorld(e);
				}
			}

			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ);
			PlayerHelper.swingItem(player);
		}
	}

	/**
	 * Scans and harvests an ore vein. This is called already knowing the mop is pointing at an ore or gravel.
	 */
	protected void tryVeinMine(ItemStack stack, EntityPlayer player, RayTraceResult mop)
	{
		if (player.worldObj.isRemote || ProjectEConfig.disableAllRadiusMining)
		{
			return;
		}

		AxisAlignedBB aabb = WorldHelper.getBroadDeepBox(mop.getBlockPos(), mop.sideHit, getCharge(stack));
		IBlockState target = player.worldObj.getBlockState(mop.getBlockPos());
		if (target.getBlockHardness(player.worldObj, mop.getBlockPos()) <= -1 || !(canHarvestBlock(target, stack) || ForgeHooks.canToolHarvestBlock(player.worldObj, mop.getBlockPos(), stack)))
		{
			return;
		}

		List<ItemStack> drops = Lists.newArrayList();

		for (BlockPos pos : WorldHelper.getPositionsFromBox(aabb))
		{
			IBlockState state = player.worldObj.getBlockState(pos);
			if (state == target)
			{
				WorldHelper.harvestVein(player.worldObj, player, stack, pos, state, drops, 0);
			}
		}

		WorldHelper.createLootDrop(drops, player.worldObj, mop.getBlockPos());
		if (!drops.isEmpty())
		{
			player.worldObj.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}


	/**
	 * Mines all ore veins in a Box around the player.
	 */
	protected void mineOreVeinsInAOE(ItemStack stack, EntityPlayer player) {
		if (player.worldObj.isRemote || ProjectEConfig.disableAllRadiusMining)
		{
			return;
		}
		int offset = this.getCharge(stack) + 3;
		AxisAlignedBB box = player.getEntityBoundingBox().expand(offset, offset, offset);
		List<ItemStack> drops = Lists.newArrayList();
		World world = player.worldObj;

		for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
		{
			IBlockState state = world.getBlockState(pos);
			if (state != Blocks.AIR.getDefaultState() && ItemHelper.isOre(state) && state.getBlockHardness(player.worldObj, pos) != -1 && (canHarvestBlock(state, stack) || ForgeHooks.canToolHarvestBlock(world, pos, stack)))
			{
				WorldHelper.harvestVein(world, player, stack, pos, state, drops, 0);
			}
		}

		if (!drops.isEmpty())
		{
			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ );
			PlayerHelper.swingItem(player);
		}
	}
}
