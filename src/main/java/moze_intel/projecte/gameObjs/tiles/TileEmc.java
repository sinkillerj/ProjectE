package moze_intel.projecte.gameObjs.tiles;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.api.tile.TileEmcBase;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class TileEmc extends TileEmcBase implements ITickable
{
	public TileEmc()
	{
		setMaximumEMC(Constants.TILE_MAX_EMC);
	}
	
	public TileEmc(int maxAmount)
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
	protected void sendToAllAcceptors(double emc)
	{
		if (!(this instanceof IEmcProvider))
		{
			// todo move this method somewhere
			throw new UnsupportedOperationException("sending without being a provider");
		}


		Map<EnumFacing, TileEntity> tiles = Maps.filterValues(WorldHelper.getAdjacentTileEntitiesMapped(worldObj, this), Predicates.instanceOf(IEmcAcceptor.class));

		double emcPer = emc / tiles.size();
		for (Map.Entry<EnumFacing, TileEntity> entry : tiles.entrySet())
		{
			if (this instanceof RelayMK1Tile && entry.getValue() instanceof RelayMK1Tile)
			{
				continue;
			}
			double provide = ((IEmcProvider) this).provideEMC(entry.getKey().getOpposite(), emcPer);
			double remain = provide - ((IEmcAcceptor) entry.getValue()).acceptEMC(entry.getKey(), provide);
			this.addEMC(remain);
		}
	}

	class StackHandler extends ItemStackHandler
	{
		private final List<Predicate<ItemStack>> inputValidators;

		private StackHandler(int size, List<Predicate<ItemStack>> inputValidators)
		{
			super(size);
			this.inputValidators = inputValidators;
		}

		StackHandler(int size)
		{
			this(size, ImmutableList.of());
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (inputValidators.stream().allMatch(p -> p.test(stack)))
			{
				return super.insertItem(slot, stack, simulate);
			}
			else
			{
				return stack;
			}
		}

		@Override
		public void onContentsChanged(int slot)
		{
			TileEmc.this.markDirty();
		}
	}

	static class StackHandlerBuilder
	{
		private int size;
		private final List<Predicate<ItemStack>> inputValidators = new ArrayList<>();

		protected StackHandlerBuilder size(int size)
		{
			this.size = size;
			return this;
		}

		protected StackHandlerBuilder inputValidator(Predicate<ItemStack> pred)
		{
			inputValidators.add(pred);
			return this;
		}

		protected StackHandler build(TileEmc tile)
		{
			return tile.new StackHandler(size, inputValidators);
		}
	}
}
