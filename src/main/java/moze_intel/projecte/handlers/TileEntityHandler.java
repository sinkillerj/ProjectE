package moze_intel.projecte.handlers;

import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityHandler
{
	private static final List<Coordinates> CONDENSERS = new ArrayList<Coordinates>();

	public static void addCondenser(CondenserTile tile)
	{
		Coordinates coords = new Coordinates(tile);

		if (!CONDENSERS.contains(coords))
		{
			PELogger.logDebug("Added condenser at coords: " + coords);
			CONDENSERS.add(coords);
		}
	}

	public static void removeCondenser(CondenserTile tile)
	{
		Coordinates coords = new Coordinates(tile);

		if (CONDENSERS.contains(coords))
		{
			Iterator<Coordinates> iter = CONDENSERS.iterator();

			while (iter.hasNext())
			{
				if (iter.next().equals(coords))
				{
					iter.remove();
					PELogger.logDebug("Condenser at " + coords + " has been removed.");
					return;
				}
			}
		}
		else
		{
			PELogger.logFatal("Condenser at coordinates: " + coords + " hasn't been mapped!");
		}
	}

	public static void checkAllCondensers(World world)
	{
		Iterator<Coordinates> iter = CONDENSERS.iterator();

		while (iter.hasNext())
		{
			Coordinates coords = iter.next();

			TileEntity tile = world.getTileEntity(coords.x, coords.y, coords.z);

			if (tile instanceof CondenserTile)
			{
				((CondenserTile) tile).checkLockAndUpdate();
			}
			else
			{
				PELogger.logFatal("Condenser not found at coordinates: " + coords);
				iter.remove();
			}
		}
	}

	public static void clearAll()
	{
		CONDENSERS.clear();
	}
}

