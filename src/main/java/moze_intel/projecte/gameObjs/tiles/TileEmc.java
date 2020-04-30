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

public abstract class TileEmc extends TileEmcBase implements ITickableTileEntity {

	public TileEmc(TileEntityType<?> type) {
		super(type);
		setMaximumEMC(Constants.TILE_MAX_EMC);
	}

	public TileEmc(TileEntityType<?> type, long maxAmount) {
		super(type);
		setMaximumEMC(maxAmount);
	}

	@Override
	public final CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	/**
	 * The amount provided will be divided and evenly distributed as best as possible between adjacent IEmcStorage. This is limited also by our max extract limit
	 *
	 * @param emc The maximum combined emc to send to others
	 *
	 * @return The amount of Emc we actually sent
	 */
	protected long sendToAllAcceptors(long emc) {
		if (world == null || !canProvideEmc()) {
			//If we cannot provide emc then just return
			return 0;
		}
		emc = Math.min(getEmcExtractLimit(), emc);
		long sentEmc = 0;

		List<IEmcStorage> targets = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			BlockPos neighboringPos = getPos().offset(dir);
			//Make sure the neighboring block is loaded as if we are on a chunk border on the edge of loaded chunks this may not be the case
			if (world.isBlockPresent(neighboringPos)) {
				TileEntity neighboringTile = world.getTileEntity(neighboringPos);
				if (neighboringTile != null) {
					neighboringTile.getCapability(ProjectEAPI.EMC_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(theirEmcStorage -> {
						if (!isRelay() || !theirEmcStorage.isRelay()) {
							//If they are both relays don't add the pairing so as to prevent thrashing
							if (theirEmcStorage.insertEmc(1, EmcAction.SIMULATE) > 0) {
								//If they would be wiling to accept any Emc then we consider them to be an "acceptor"
								targets.add(theirEmcStorage);
							}
						}
					});
				}
			}
		}

		if (!targets.isEmpty()) {
			long emcPer = emc / targets.size();
			for (IEmcStorage target : targets) {
				long emcCanProvide = extractEmc(emcPer, EmcAction.SIMULATE);
				long acceptedEmc = target.insertEmc(emcCanProvide, EmcAction.EXECUTE);
				extractEmc(acceptedEmc, EmcAction.EXECUTE);
				sentEmc += acceptedEmc;
			}
		}
		return sentEmc;
	}

	class StackHandler extends ItemStackHandler {

		StackHandler(int size) {
			super(size);
		}

		@Override
		public void onContentsChanged(int slot) {
			TileEmc.this.markDirty();
		}
	}
}