package moze_intel.projecte.utils;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Utility class to get comparator outputs for a block
 */
public final class ComparatorHelper
{
	public static int getForAlchChest(World world, BlockPos pos)
	{
		return calcRedstoneFromInventory(world.getTileEntity(pos)
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
	}

	public static int getForCollector(World world, BlockPos pos)
	{
		CollectorMK1Tile tile = ((CollectorMK1Tile) world.getTileEntity(pos));
		ItemStack charging = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP).getStackInSlot(CollectorMK1Tile.UPGRADING_SLOT);
		if (charging != null)
		{
			if (charging.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) charging.getItem());
				double max = itemEmc.getMaximumEmc(charging);
				double current = itemEmc.getStoredEmc(charging);
				return MathUtils.scaleToRedstone(current, max);
			} else
			{
				double needed = tile.getEmcToNextGoal();
				double current = tile.getStoredEmc();
				return MathUtils.scaleToRedstone(current, needed);
			}
		} else
		{
			return MathUtils.scaleToRedstone(tile.getStoredEmc(), tile.getMaximumEmc());
		}
	}

	public static int getForCondenser(World world, BlockPos pos)
	{
		return calcRedstoneFromInventory(world.getTileEntity(pos)
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
	}

	public static int getForMatterFurnace(World world, BlockPos pos)
	{
		return calcRedstoneFromInventory(world.getTileEntity(pos)
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
	}

	public static int getForRelay(World world, BlockPos pos)
	{
		RelayMK1Tile relay = ((RelayMK1Tile) world.getTileEntity(pos));
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
	}

	// Copy of Container.calcRedstoneFromInventory for IItemHandler
	private static int calcRedstoneFromInventory(IItemHandler handler)
	{
		if (handler == null)
		{
			return 0;
		}
		else
		{
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < handler.getSlots(); ++j)
			{
				ItemStack itemstack = handler.getStackInSlot(j);

				if (itemstack != null)
				{
					f += (float)itemstack.stackSize / itemstack.getMaxStackSize(); // todo (float)Math.min(handler.getInventoryStackLimit(), itemstack.getMaxStackSize());
					++i;
				}
			}

			f = f / (float)handler.getSlots();
			return MathHelper.floor_float(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
