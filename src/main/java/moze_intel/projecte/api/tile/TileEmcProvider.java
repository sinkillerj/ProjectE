package moze_intel.projecte.api.tile;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Reference implementation for IEMCProvider
 *
 * @author williewillus
 */
public class TileEmcProvider extends TileEmcBase implements IEmcProvider
{
	@Override
	public long provideEMC(@Nonnull EnumFacing side, long toExtract)
	{
		long toRemove = Math.min(currentEMC, toExtract);
		removeEMC(toRemove);
		return toRemove;
	}
}
