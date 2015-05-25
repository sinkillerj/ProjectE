package moze_intel.projecte.utils;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Utility class to get comparator outputs for a block
 */
public final class ComparatorHelper
{
	public static int getForAlchChest(World world, BlockPos pos)
	{
		return Container.calcRedstoneFromInventory(((AlchChestTile) world.getTileEntity(pos)));
	}

	public static int getForCollector(World world, BlockPos pos)
	{
		CollectorMK1Tile tile = ((CollectorMK1Tile) world.getTileEntity(pos));
		ItemStack charging = tile.getChargingItem();
		if (charging != null)
		{
			if (charging.getItem() == ObjHandler.kleinStars)
			{
				double max = EMCHelper.getKleinStarMaxEmc(charging);
				double current = ItemPE.getEmc(charging);
				return MathUtils.scaleToRedstone(current, max);
			} else
			{

				double needed = tile.getEmcToNextGoal();
				double current = tile.getStoredEmc();
				return MathUtils.scaleToRedstone(current, needed);
			}
		} else
		{
			return MathUtils.scaleToRedstone(tile.getStoredEmc(), tile.getMaxEmc());
		}
	}

	public static int getForCondenser(World world, BlockPos pos)
	{
		return Container.calcRedstoneFromInventory(((CondenserTile) world.getTileEntity(pos)));
	}

	public static int getForMatterFurnace(World world, BlockPos pos)
	{
		return Container.calcRedstoneFromInventory(((RMFurnaceTile) world.getTileEntity(pos)));
	}

	public static int getForRelay(World world, BlockPos pos)
	{
		RelayMK1Tile relay = ((RelayMK1Tile) world.getTileEntity(pos));
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaxEmc());
	}
}
