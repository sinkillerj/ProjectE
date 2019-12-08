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
import moze_intel.projecte.utils.LazyOptionalHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

	@Override
	@Deprecated
	public boolean onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof CollectorMK1Tile) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (CollectorMK1Tile) te, pos);
			}
		}
		return true;
	}

	@Nullable
	@Override
	@Deprecated
	public INamedContainerProvider getContainer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CollectorMK1Tile) {
			return (CollectorMK1Tile) te;
		}
		return null;
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
	public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(@Nonnull BlockState state, World world, @Nonnull BlockPos pos) {
		CollectorMK1Tile tile = (CollectorMK1Tile) world.getTileEntity(pos);
		Optional<IItemHandler> cap = LazyOptionalHelper.toOptional(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP));
		if (!cap.isPresent()) {
			//If something went wrong fallback to default implementation
			return super.getComparatorInputOverride(state, world, pos);
		}
		ItemStack charging = cap.get().getStackInSlot(CollectorMK1Tile.UPGRADING_SLOT);
		if (!charging.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(charging.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
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
	public void onReplaced(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent != null) {
			ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(handler -> {
				for (int i = 0; i < handler.getSlots(); i++) {
					if (i != CollectorMK1Tile.LOCK_SLOT && !handler.getStackInSlot(i).isEmpty()) {
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
					}
				}
			});
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}