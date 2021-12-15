package moze_intel.projecte.gameObjs.blocks;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class Collector extends BlockDirection {

	private final EnumCollectorTier tier;

	public Collector(EnumCollectorTier tier, Properties props) {
		super(props);
		this.tier = tier;
	}

	public EnumCollectorTier getTier() {
		return tier;
	}

	@Nonnull
	@Override
	@Deprecated
	public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
		if (!world.isClientSide) {
			CollectorMK1Tile te = WorldHelper.getTileEntity(CollectorMK1Tile.class, world, pos, true);
			if (te != null) {
				NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Nullable
	@Override
	@Deprecated
	public INamedContainerProvider getMenuProvider(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
		return WorldHelper.getTileEntity(CollectorMK1Tile.class, world, pos, true);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		switch (tier) {
			case MK1:
				return new CollectorMK1Tile();
			case MK2:
				return new CollectorMK2Tile();
			case MK3:
				return new CollectorMK3Tile();
			default:
				return null;
		}
	}

	@Override
	@Deprecated
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
		CollectorMK1Tile tile = WorldHelper.getTileEntity(CollectorMK1Tile.class, world, pos, true);
		if (tile == null) {
			//If something went wrong fallback to default implementation
			return super.getAnalogOutputSignal(state, world, pos);
		}
		Optional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).resolve();
		if (!cap.isPresent()) {
			//If something went wrong fallback to default implementation
			return super.getAnalogOutputSignal(state, world, pos);
		}
		ItemStack charging = cap.get().getStackInSlot(CollectorMK1Tile.UPGRADING_SLOT);
		if (!charging.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = charging.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				return MathUtils.scaleToRedstone(emcHolder.getStoredEmc(charging), emcHolder.getMaximumEmc(charging));
			}
			return MathUtils.scaleToRedstone(tile.getStoredEmc(), tile.getEmcToNextGoal());
		}
		return MathUtils.scaleToRedstone(tile.getStoredEmc(), tile.getMaximumEmc());
	}

	@Override
	@Deprecated
	public void onRemove(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity ent = WorldHelper.getTileEntity(world, pos);
			if (ent != null) {
				ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(handler -> {
					for (int i = 0; i < handler.getSlots(); i++) {
						if (i != CollectorMK1Tile.LOCK_SLOT && !handler.getStackInSlot(i).isEmpty()) {
							InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
						}
					}
				});
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}
}