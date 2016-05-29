package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
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
	protected Set<Material> harvestMaterials;
	protected Set<String> secondaryClasses;

	public PEToolBase(String unlocalName, byte numCharge, String[] modeDescrp)
	{
		super(unlocalName, numCharge, modeDescrp);
		harvestMaterials = Sets.newHashSet();
		secondaryClasses = Sets.newHashSet();
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		return harvestMaterials.contains(block.getMaterial());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass)
	{
		if (this.pePrimaryToolClass.equals(toolClass) || this.secondaryClasses.contains(toolClass))
		{
			return 4; // TiCon
		}
		return -1;
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if ("dm_tools".equals(this.peToolMaterial))
		{
			if (canHarvestBlock(block, stack) || ForgeHooks.canToolHarvestBlock(block, metadata, stack))
			{
				return 14.0f + (12.0f * this.getCharge(stack));
			}
		}
		else if ("rm_tools".equals(this.peToolMaterial))
		{
			if (canHarvestBlock(block, stack) || ForgeHooks.canToolHarvestBlock(block, metadata, stack))
			{
				return 16.0f + (14.0f * this.getCharge(stack));
			}
		}
		return 1.0F;
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture(peToolMaterial, pePrimaryToolClass));
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

		for (int x = (int) player.posX - (5 * charge); x <= player.posX + (5 * charge); x++)
			for (int y = (int) player.posY - (10 * charge); y <= player.posY + (10 * charge); y++)
				for (int z = (int) player.posZ - (5 * charge); z <= player.posZ + (5 * charge); z++)
				{
					Block block = world.getBlock(x, y, z);

					if (block == Blocks.air)
					{
						continue;
					}

					ItemStack s = new ItemStack(block);
					int[] oreIds = OreDictionary.getOreIDs(s);

					String oreName;
					if (oreIds.length == 0)
					{
						if (block == Blocks.brown_mushroom_block || block == Blocks.red_mushroom_block)
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
						ArrayList<ItemStack> blockDrops = WorldHelper.getBlockDrops(world, player, block, stack, x, y, z);

						if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), x, y, z)
							&& consumeFuel(player, stack, emcCost, true))
						{
							drops.addAll(blockDrops);
							world.setBlockToAir(x, y, z);
							if (world.rand.nextInt(5) == 0)
							{
								PacketHandler.sendToAllAround(new ParticlePKT("largesmoke", x, y, z), new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y + 1, z, 32));
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
	protected void tillAOE(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta, int emcCost)
	{
		byte charge = this.getCharge(stack);
		boolean hasAction = false;
		boolean hasSoundPlayed = false;

		for (int i = x - charge; i <= x + charge; i++)
		{
			for (int j = z - charge; j <= z + charge; j++)
			{
				Block block = world.getBlock(i, y, j);
				Block blockAbove = world.getBlock(i, y + 1, j);

				if (!blockAbove.isOpaqueCube() && (block == Blocks.grass || block == Blocks.dirt))
				{
					if (!hasSoundPlayed)
					{
						world.playSoundEffect((double)((float)i + 0.5F), (double)((float)y + 0.5F), (double)((float)j + 0.5F), Blocks.farmland.stepSound.getStepResourcePath(), (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.farmland.stepSound.getPitch() * 0.8F);
						hasSoundPlayed = true;
					}

					if (world.isRemote)
					{
						return;
					}
					else
					{
						if (MinecraftForge.EVENT_BUS.post(new UseHoeEvent(player, stack, world, i, y, j)))
						{
							continue;
						}

						// The initial block we target is always free
						if ((i == x && j == z) || consumeFuel(player, stack, emcCost, true))
						{
							PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), i, y, j, Blocks.farmland, 0);

							if ((blockAbove.getMaterial() == Material.plants || blockAbove.getMaterial() == Material.vine)
									&& !(blockAbove instanceof ITileEntityProvider)
									) {
								if (PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), i, y + 1, j))
								{
									world.func_147480_a(i, y + 1, j, true);
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
		}
		if (hasAction)
		{
			player.worldObj.playSoundAtEntity(player, "projecte:item.pecharge", 1.0F, 1.0F);
		}
	}

	/**
	 * Called by multiple tools' left click function. Charge has no effect. Free operation.
	 */
	protected void digBasedOnMode(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase living)
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

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		AxisAlignedBB box;

		if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
		{
			return;
		}

		ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);

		if (ProjectEConfig.disableAllRadiusMining) {
			box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
		} else if (mode == 1) // 3x Tallshot
		{
			box = AxisAlignedBB.getBoundingBox(x, y - 1, z, x, y + 1, z);
		}
		else if (mode == 2) // 3x Wideshot
		{
			if (direction.offsetX != 0)
			{
				box = AxisAlignedBB.getBoundingBox(x, y, z - 1, x, y, z + 1);
			}
			else if (direction.offsetZ != 0)
			{
				box = AxisAlignedBB.getBoundingBox(x - 1, y, z, x + 1, y, z);
			}
			else
			{
				int dir = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

				if (dir == 0 || dir == 2)
				{
					box = AxisAlignedBB.getBoundingBox(x, y, z - 1, x, y, z + 1);
				}
				else
				{
					box = AxisAlignedBB.getBoundingBox(x - 1, y, z, x + 1, y, z);
				}
			}
		}
		else // 3x Longshot
		{
			if (direction.offsetX == 1)
			{
				box = AxisAlignedBB.getBoundingBox(x - 2, y, z, x, y, z);
			}
			else if (direction.offsetX == - 1)
			{
				box = AxisAlignedBB.getBoundingBox(x, y, z, x + 2, y, z);
			}
			else if (direction.offsetZ == 1)
			{
				box = AxisAlignedBB.getBoundingBox(x, y, z - 2, x, y, z);
			}
			else if (direction.offsetZ == -1)
			{
				box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z + 2);
			}
			else if (direction.offsetY == 1)
			{
				box = AxisAlignedBB.getBoundingBox(x, y - 2, z, x, y, z);
			}
			else
			{
				box = AxisAlignedBB.getBoundingBox(x, y, z, x, y + 2, z);
			}
		}

		List<ItemStack> drops = Lists.newArrayList();

		for (int i = (int) box.minX; i <= box.maxX; i++)
			for (int j = (int) box.minY; j <= box.maxY; j++)
				for (int k = (int) box.minZ; k <= box.maxZ; k++)
				{
					Block b = world.getBlock(i, j, k);

					if (b != Blocks.air
							&& b.getBlockHardness(world, i, j, k) != -1
							&& PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), i, j, k)
							&& (canHarvestBlock(block, stack) || ForgeHooks.canToolHarvestBlock(block, world.getBlockMetadata(i, j, k), stack)))
					{
						drops.addAll(WorldHelper.getBlockDrops(world, player, b, stack, i, j, k));
						world.setBlockToAir(i, j, k);
					}
				}

		WorldHelper.createLootDrop(drops, world, x, y, z);
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

		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
		{
			return;
		}

		AxisAlignedBB box = affectDepth ? WorldHelper.getBroadDeepBox(new Coordinates(mop.blockX, mop.blockY, mop.blockZ), ForgeDirection.getOrientation(mop.sideHit), this.getCharge(stack))
				: WorldHelper.getFlatYBox(new Coordinates(mop.blockX, mop.blockY, mop.blockZ), this.getCharge(stack));

		List<ItemStack> drops = Lists.newArrayList();

		for (int i = (int) box.minX; i <= box.maxX; i++)
			for (int j = (int) box.minY; j <= box.maxY; j++)
				for (int k = (int) box.minZ; k <= box.maxZ; k++)
				{
					Block b = world.getBlock(i, j, k);

					if (b != Blocks.air && b.getBlockHardness(world, i, j, k) != -1
							&& canHarvestBlock(b, stack)
							&& PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), i, j, k)
							&& consumeFuel(player, stack, emcCost, true)
							)
					{
						drops.addAll(WorldHelper.getBlockDrops(world, player, b, stack, i, j, k));
						world.setBlockToAir(i, j, k);
					}
				}

		WorldHelper.createLootDrop(drops, world, mop.blockX, mop.blockY, mop.blockZ);
		PlayerHelper.swingItem(player);
		if (!drops.isEmpty())
		{
			world.playSoundAtEntity(player, "projecte:item.pedestruct", 1.0F, 1.0F);
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
		AxisAlignedBB aabb = player.boundingBox.expand(factor, factor, factor);
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
		player.worldObj.playSoundAtEntity(player, "projecte:item.pecharge", 1.0F, 1.0F);
		PlayerHelper.swingItem(player);
	}

	/**
	 * Called when tools that act as shears start breaking a block. Free operation.
	 */
	protected void shearBlock(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		if (player.worldObj.isRemote)
		{
			return;
		}

		Block block = player.worldObj.getBlock(x, y, z);

		if (block instanceof IShearable)
		{
			IShearable target = (IShearable) block;

			if (target.isShearable(stack, player.worldObj, x, y, z) && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), x, y, z))
			{
				ArrayList<ItemStack> drops = target.onSheared(stack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
				Random rand = new Random();

				for(ItemStack drop : drops)
				{
					float f = 0.7F;
					double d = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, drop);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}

				stack.damageItem(1, player);
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
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

			AxisAlignedBB bBox = player.boundingBox.expand(offset, offset / 2, offset);
			List<Entity> list = world.getEntitiesWithinAABB(IShearable.class, bBox);

			List<ItemStack> drops = Lists.newArrayList();

			for (Entity ent : list)
			{
				IShearable target = (IShearable) ent;

				if (target.isShearable(stack, ent.worldObj, (int) ent.posX, (int) ent.posY, (int) ent.posZ)
						&& consumeFuel(player, stack, emcCost, true)
						)
				{
					ArrayList<ItemStack> entDrops = target.onSheared(stack, ent.worldObj, (int) ent.posX, (int) ent.posY, (int) ent.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));

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
					e.copyDataFrom(ent, true);
					if (e instanceof EntitySheep)
					{
						((EntitySheep) e).setFleeceColor(MathUtils.randomIntInRange(0, 16));
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
	protected void tryVeinMine(ItemStack stack, EntityPlayer player, MovingObjectPosition mop)
	{
		if (player.worldObj.isRemote || ProjectEConfig.disableAllRadiusMining)
		{
			return;
		}

		AxisAlignedBB aabb = WorldHelper.getBroadDeepBox(new Coordinates(mop.blockX, mop.blockY, mop.blockZ), ForgeDirection.getOrientation(mop.sideHit), getCharge(stack));
		Block target = player.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		if (target.getBlockHardness(player.worldObj, mop.blockX, mop.blockY, mop.blockZ) <= -1 || !(canHarvestBlock(target, stack) || ForgeHooks.canToolHarvestBlock(target, player.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ), stack)))
		{
			return;
		}

		List<ItemStack> drops = Lists.newArrayList();

		for (int i = (int) aabb.minX; i <= aabb.maxX; i++)
		{
			for (int j = (int) aabb.minY; j <= aabb.maxY; j++)
			{
				for (int k = (int) aabb.minZ; k <= aabb.maxZ; k++)
				{
					Block b = player.worldObj.getBlock(i, j, k);
					if (b == target)
					{
						WorldHelper.harvestVein(player.worldObj, player, stack, new Coordinates(i, j, k), b, drops, 0);
					}
				}
			}
		}

		WorldHelper.createLootDrop(drops, player.worldObj, mop.blockX, mop.blockY, mop.blockZ);
		if (!drops.isEmpty())
		{
			player.worldObj.playSoundAtEntity(player, "projecte:item.pedestruct", 1.0F, 1.0F);
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
		AxisAlignedBB box = player.boundingBox.expand(offset, offset, offset);
		List<ItemStack> drops = Lists.newArrayList();
		World world = player.worldObj;

		for (int x = (int) box.minX; x <= box.maxX; x++)
			for (int y = (int) box.minY; y <= box.maxY; y++)
				for (int z = (int) box.minZ; z <= box.maxZ; z++)
				{
					Block block = world.getBlock(x, y, z);

					if (ItemHelper.isOre(block, world.getBlockMetadata(x, y, z)) && block.getBlockHardness(player.worldObj, x, y, z) != -1 && (canHarvestBlock(block, stack) || ForgeHooks.canToolHarvestBlock(block, world.getBlockMetadata(x, y, z), stack)))
					{
						WorldHelper.harvestVein(world, player, stack, new Coordinates(x, y, z), block, drops, 0);
					}
				}

		if (!drops.isEmpty())
		{
			WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ );
			PlayerHelper.swingItem(player);
		}
	}
}
