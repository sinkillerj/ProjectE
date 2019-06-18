package moze_intel.projecte.api.tile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

/**
 * Reference implementation of IEMCAcceptor
 *
 * @author williewillus
 */
public class TileEmcAcceptor extends TileEmcBase implements IEmcAcceptor
{
	public TileEmcAcceptor(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public double acceptEMC(@Nonnull Direction side, double toAccept)
	{
		double toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		addEMC(toAdd);
		return toAdd;
	}
}
