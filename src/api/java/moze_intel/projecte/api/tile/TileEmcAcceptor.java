package moze_intel.projecte.api.tile;

import net.minecraft.tileentity.TileEntityType;
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
	public long acceptEMC(@Nonnull Direction side, long toAccept)
	{
		long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		addEMC(toAdd);
		return toAdd;
	}
}
