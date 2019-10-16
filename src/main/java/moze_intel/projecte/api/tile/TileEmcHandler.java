package moze_intel.projecte.api.tile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

/**
 * Reference implementation of both IEMCAcceptor and IEMCProvider
 *
 * @author williewillus
 */
public class TileEmcHandler extends TileEmcBase implements IEmcAcceptor, IEmcProvider
{
	public TileEmcHandler(TileEntityType<?> type)
	{
		super(type);
		this.maximumEMC = Long.MAX_VALUE;
	}

	public TileEmcHandler(TileEntityType<?> type, long max)
	{
		super(type);
		this.maximumEMC = max;
	}

	// -- IEMCAcceptor -- //
	@Override
	public long acceptEMC(@Nonnull Direction side, long toAccept)
	{
		long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		currentEMC += toAdd;
		return toAdd;
	}

	// -- IEMCProvider -- //
	@Override
	public long provideEMC(@Nonnull Direction side, long toExtract)
	{
		long toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}

	// -- IEMCStorage --//
	@Override
	public long getStoredEmc()
	{
		return currentEMC;
	}

	@Override
	public long getMaximumEmc()
	{
		return maximumEMC;
	}
}
