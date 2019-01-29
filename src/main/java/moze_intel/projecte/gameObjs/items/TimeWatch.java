package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Sets;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.CollectionHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class TimeWatch extends ItemPE implements IModeChanger, IBauble, IPedestalItem, IItemCharge
{
	// TODO 1.13 remove
	private static final Set<String> internalBlacklist = Sets.newHashSet(
			"moze_intel.projecte.gameObjs.tiles.DMPedestalTile", // Never let the the pedestal update another pedestal
			"com.sci.torcherino.blocks.tiles.TileTorcherino", // same goes for other tickrate increasing tiles
			"com.sci.torcherino.blocks.tiles.TileCompressedTorcherino",
			"com.sci.torcherino.blocks.tiles.TileDoubleCompressedTorcherino"
	);
	private static final Predicate<TileEntity> TILE_FILTER = tile -> tile instanceof ITickable && !internalBlacklist.contains(tile.getClass().getName()) && !CollectionHelper.contains(ProjectEConfig.effects.timeWatchTEBlacklist, Objects.toString(TileEntity.getKey(tile.getClass())));

	public TimeWatch()
	{
		this.setTranslationKey("time_watch");
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.addPropertyOverride(ACTIVE_NAME, ACTIVE_GETTER);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
		{
			if (!ProjectEConfig.items.enableTimeWatch)
			{
				player.sendMessage(new TextComponentTranslation("pe.timewatch.disabled"));
				return ActionResult.newResult(EnumActionResult.FAIL, stack);
			}

			byte current = getTimeBoost(stack);

			setTimeBoost(stack, (byte) (current == 2 ? 0 : current + 1));

			player.sendMessage(new TextComponentTranslation("pe.timewatch.mode_switch", new TextComponentTranslation(getTimeName(stack)).getUnformattedComponentText()));
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld)
	{
		super.onUpdate(stack, world, entity, invSlot, isHeld);

		if (!(entity instanceof EntityPlayer) || invSlot > 8)
		{
			return;
		}

		if (!ProjectEConfig.items.enableTimeWatch)
		{
			return;
		}

		byte timeControl = getTimeBoost(stack);

		if (world.getGameRules().getBoolean("doDaylightCycle"))
		{
			if (timeControl == 1)
			{
				if (world.getWorldTime() + ((getCharge(stack) + 1) * 4) > Long.MAX_VALUE)
				{
					world.setWorldTime(Long.MAX_VALUE);
				}
				else
				{
					world.setWorldTime((world.getWorldTime() + ((getCharge(stack) + 1) * 4)));
				}
			}
			else if (timeControl == 2)
			{
				if (world.getWorldTime() - ((getCharge(stack) + 1) * 4) < 0)
				{
					world.setWorldTime(0);
				}
				else
				{
					world.setWorldTime((world.getWorldTime() - ((getCharge(stack) + 1) * 4)));
				}
			}
		}

		if (world.isRemote || !ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;
		double reqEmc = getEmcPerTick(this.getCharge(stack));

		if (!consumeFuel(player, stack, reqEmc, true))
		{
			return;
		}

		int charge = this.getCharge(stack);
		int bonusTicks;
		float mobSlowdown;

		if (charge == 0)
		{
			bonusTicks = 8;
			mobSlowdown = 0.25F;
		}
		else if (charge == 1)
		{
			bonusTicks = 12;
			mobSlowdown = 0.16F;
		}
		else
		{
			bonusTicks = 16;
			mobSlowdown = 0.12F;
		}

		AxisAlignedBB aabb = player.getEntityBoundingBox().grow(8);

		speedUpTileEntities(world, bonusTicks, aabb);
		speedUpRandomTicks(world, bonusTicks, aabb);
		slowMobs(world, aabb, mobSlowdown);
	}

	private static void slowMobs(World world, AxisAlignedBB aabb, float mobSlowdown)
	{
		if (world == null || aabb == null || mobSlowdown < 0 || mobSlowdown >= 1) // Sanity check for chunk unload weirdness
		{
			return;
		}
		for (EntityLiving e : world.getEntitiesWithinAABB(EntityLiving.class, aabb))
		{
			if (e.motionX != 0)
			{
				e.motionX *= mobSlowdown;
			}

			if (e.motionZ != 0)
			{
				e.motionZ *= mobSlowdown;
			}
		}
	}

	private static void speedUpTileEntities(World world, int bonusTicks, AxisAlignedBB aabb)
	{
		if (world == null || aabb == null || bonusTicks <= 0) // Sanity check the box for chunk unload weirdness
		{
			return;
		}

		List<TileEntity> list = WorldHelper.getTileEntitiesWithinAABB(world, aabb, TILE_FILTER);
		for (int i = 0; i < bonusTicks; i++)
		{
			for (TileEntity tile : list)
			{
				if (!tile.isInvalid())
				{
					((ITickable) tile).update();
				}
			}
		}
	}

	private static void speedUpRandomTicks(World world, int bonusTicks, AxisAlignedBB aabb)
	{
		if (world == null || aabb == null || bonusTicks <= 0) // Sanity check the box for chunk unload weirdness
		{
			return;
		}

		for (BlockPos pos : WorldHelper.getPositionsFromBox(aabb))
		{
			for (int i = 0; i < bonusTicks; i++)
			{
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (block.getTickRandomly()
						&& !(block instanceof BlockLiquid) // Don't speed vanilla non-source blocks - dupe issues
						&& !(block instanceof BlockFluidBase) // Don't speed Forge fluids - just in case of dupes as well
						&& !(block instanceof IGrowable)
						&& !(block instanceof IPlantable) // All plants should be sped using Harvest Goddess
						&& !CollectionHelper.contains(ProjectEConfig.effects.timeWatchBlockBlacklist, Objects.toString(block.getRegistryName())))
				{
					block.updateTick(world, pos, state, itemRand);
				}
			}
		}
	}

	private static String getTimeName(ItemStack stack)
	{
		byte mode = getTimeBoost(stack);
		switch (mode)
		{
			case 0:
				return "pe.timewatch.off";
			case 1:
				return "pe.timewatch.ff";
			case 2:
				return "pe.timewatch.rw";
			default:
				return "ERROR_INVALID_MODE";
		}
	}

	private static byte getTimeBoost(ItemStack stack)
	{
		return ItemHelper.getOrCreateCompound(stack).getByte("TimeMode");
	}

	private static void setTimeBoost(ItemStack stack, byte time)
	{
		ItemHelper.getOrCreateCompound(stack).setByte("TimeMode", (byte) MathHelper.clamp(time, 0, 2));
	}

	private static double getEmcPerTick(int charge)
	{
		int actualCharge = charge + 1;
		return (10.0D * actualCharge) / 20.0D;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
		return ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE) ? (byte) 1 : 0;
	}

	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		NBTTagCompound tag = ItemHelper.getOrCreateCompound(stack);
		tag.setBoolean(TAG_ACTIVE, !tag.getBoolean(TAG_ACTIVE));
		return true;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		list.add(I18n.format("pe.timewatch.tooltip1"));
		list.add(I18n.format("pe.timewatch.tooltip2"));

		if (stack.hasTagCompound())
		{
			list.add(I18n.format("pe.timewatch.mode",
					I18n.format(getTimeName(stack))));
		}
	}

	@Override
	@Optional.Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player)
	{
		this.onUpdate(stack, player.getEntityWorld(), player, 0, false);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		// Change from old EE2 behaviour (universally increased tickrate) for safety and impl reasons.

		if (!world.isRemote && ProjectEConfig.items.enableTimeWatch)
		{
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof DMPedestalTile)
			{
				AxisAlignedBB bBox = ((DMPedestalTile) te).getEffectBounds();
				if (ProjectEConfig.effects.timePedBonus > 0)
				{
					speedUpTileEntities(world, ProjectEConfig.effects.timePedBonus, bBox);
					speedUpRandomTicks(world, ProjectEConfig.effects.timePedBonus, bBox);
				}

				if (ProjectEConfig.effects.timePedMobSlowness < 1.0F)
				{
					slowMobs(world, bBox, ProjectEConfig.effects.timePedMobSlowness);
				}
			}
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<>();
		if (ProjectEConfig.effects.timePedBonus > 0)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.timewatch.pedestal1", ProjectEConfig.effects.timePedBonus));
		}
		if (ProjectEConfig.effects.timePedMobSlowness < 1.0F)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.timewatch.pedestal2", ProjectEConfig.effects.timePedMobSlowness));
		}
		return list;
	}

	public static void blacklist(Class<? extends TileEntity> clazz)
	{
		internalBlacklist.add(clazz.getName());
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack)
	{
		return 2;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0D - (double) getCharge(stack) / getNumCharges(stack);
	}
}
