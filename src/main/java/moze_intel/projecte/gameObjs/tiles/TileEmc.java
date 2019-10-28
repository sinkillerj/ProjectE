package moze_intel.projecte.gameObjs.tiles;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage;
import moze_intel.projecte.api.tile.TileEmcBase;
import moze_intel.projecte.utils.Constants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

public abstract class TileEmc extends TileEmcBase implements ITickableTileEntity
{
	public TileEmc(TileEntityType<?> type)
	{
		super(type);
		setMaximumEMC(Constants.TILE_MAX_EMC);
	}
	
	public TileEmc(TileEntityType<?> type, long maxAmount)
	{
		super(type);
		setMaximumEMC(maxAmount);
	}

	@Override
	public final CompoundNBT getUpdateTag()
	{
		return write(new CompoundNBT());
	}

	/**
	 * The amount provided will be divided and evenly distributed as best as possible between adjacent IEmcStorage. This is limited also by our max extract limit
	 *
	 * @param emc The maximum combined emc to send to others
	 * @return The amount of Emc we actually sent
	 */
	protected long sendToAllAcceptors(long emc) {
		if (!canProvideEmc()) {
			//If we cannot provide emc then just return
			return 0;
		}
		//TODO: Given we do it this way, just directly reference ourself??
		emc = Math.min(getEmcExtractLimit(), emc);
		long sentEmc = 0;

		//We use a list instead of a map as it is possible depending on implementation that our cap is the same for each side
		// In fact for our default implementation, this is the case
		List<Pair<IEmcStorage, IEmcStorage>> emcStoragePairings = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			BlockPos neighboringPos = getPos().offset(dir);
			//Make sure the neighboring block is loaded as if we are on a chunk border on the edge of loaded chunks this may not be the case
			if (world.isBlockLoaded(neighboringPos)) {
				TileEntity neighboringTile = world.getTileEntity(neighboringPos);
				if (neighboringTile != null) {
					neighboringTile.getCapability(ProjectEAPI.EMC_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(theirEmcStorage -> {
						//If they would be wiling to accept any Emc then we consider them to be an "acceptor"
						if (theirEmcStorage.insertEmc(1, EmcAction.SIMULATE) > 0) {
							getCapability(ProjectEAPI.EMC_STORAGE_CAPABILITY, dir).ifPresent(ourEmcStorage -> {
								if (!ourEmcStorage.isRelay() || !theirEmcStorage.isRelay()) {
									//If they are both relays don't add the pairing so as to prevent thrashing
									emcStoragePairings.add(Pair.of(ourEmcStorage, theirEmcStorage));
								}
							});
						}
					});
				}
			}
		}

		if (!emcStoragePairings.isEmpty()) {
			long emcPer = emc / emcStoragePairings.size();
			for (Pair<IEmcStorage, IEmcStorage> entry : emcStoragePairings) {
				IEmcStorage ourEmcStorage = entry.getLeft();
				long emcCanProvide = ourEmcStorage.extractEmc(emcPer, EmcAction.SIMULATE);
				long acceptedEmc = entry.getRight().insertEmc(emcCanProvide, EmcAction.EXECUTE);
				ourEmcStorage.extractEmc(acceptedEmc, EmcAction.EXECUTE);
				sentEmc += acceptedEmc;
			}
		}
		return sentEmc;
	}

	class StackHandler extends ItemStackHandler
	{
		StackHandler(int size)
		{
			super(size);
		}

		@Override
		public void onContentsChanged(int slot)
		{
			TileEmc.this.markDirty();
		}
	}
}