package moze_intel.projecte.utils;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Utility class to get comparator outputs for a block
 */
public final class ComparatorHelper
{
	public static int getForAlchChest(World world, int x, int y, int z)
	{
		return Container.calcRedstoneFromInventory(((AlchChestTile) world.getTileEntity(x, y, z)));
	}

	public static int getForCollector(World world, int x, int y, int z)
	{
		CollectorMK1Tile tile = ((CollectorMK1Tile) world.getTileEntity(x, y, z));
		ItemStack charging = tile.getChargingItem();
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

	public static int getForCondenser(World world, int x, int y, int z)
	{
		return Container.calcRedstoneFromInventory(((CondenserTile) world.getTileEntity(x, y, z)));
	}

	public static int getForMatterFurnace(World world, int x, int y, int z)
	{
		return Container.calcRedstoneFromInventory(((RMFurnaceTile) world.getTileEntity(x, y, z)));
	}

	public static int getForRelay(World world, int x, int y, int z)
	{
		RelayMK1Tile relay = ((RelayMK1Tile) world.getTileEntity(x, y, z));
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
	}
}
