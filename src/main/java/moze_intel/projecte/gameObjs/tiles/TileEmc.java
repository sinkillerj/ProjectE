package moze_intel.projecte.gameObjs.tiles;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.api.tile.TileEmcBase;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class TileEmc extends TileEmcBase implements ITickable
{
	public TileEmc()
	{
		setMaximumEMC(Constants.TILE_MAX_EMC);
	}
	
	public TileEmc(long maxAmount)
	{
		setMaximumEMC(maxAmount);
	}

	@Override
	public final NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState state, @Nonnull IBlockState newState)
	{
		return state.getBlock() != newState.getBlock();
	}
	
	protected boolean hasMaxedEmc()
	{
		return getStoredEmc() >= getMaximumEmc();
	}

	/**
	 * The amount provided will be divided and evenly distributed as best as possible between adjacent IEMCAcceptors
	 * Remainder or rejected EMC is added back to this provider
	 *
	 * @param emc The maximum combined emc to send to others
	 */
	protected void sendToAllAcceptors(long emc)
	{
		if (!(this instanceof IEmcProvider))
		{
			// todo move this method somewhere
			throw new UnsupportedOperationException("sending without being a provider");
		}


		Map<EnumFacing, TileEntity> tiles = Maps.filterValues(WorldHelper.getAdjacentTileEntitiesMapped(world, this), Predicates.instanceOf(IEmcAcceptor.class));
		if (tiles.isEmpty())
		{
			return;
		}

		long emcPer = emc / tiles.size();
		for (Map.Entry<EnumFacing, TileEntity> entry : tiles.entrySet())
		{
			if (this instanceof RelayMK1Tile && entry.getValue() instanceof RelayMK1Tile)
			{
				continue;
			}
			long provide = ((IEmcProvider) this).provideEMC(entry.getKey().getOpposite(), emcPer);
			long remain = provide - ((IEmcAcceptor) entry.getValue()).acceptEMC(entry.getKey(), provide);
			this.addEMC(remain);
		}
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
