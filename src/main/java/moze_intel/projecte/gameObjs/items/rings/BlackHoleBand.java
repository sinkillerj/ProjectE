package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.AlchBagItemCapabilityWrapper;
import moze_intel.projecte.capability.AlchChestItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.IntegrationHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlackHoleBand extends PEToggleItem implements IAlchBagItem, IAlchChestItem, IPedestalItem {

	public BlackHoleBand(Properties props) {
		super(props);
		addItemCapability(new AlchBagItemCapabilityWrapper());
		addItemCapability(new AlchChestItemCapabilityWrapper());
		addItemCapability(new PedestalItemCapabilityWrapper());
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	private ActionResultType tryPickupFluid(World world, PlayerEntity player, ItemStack stack) {
		RayTraceResult rtr = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);

		if (!(rtr instanceof BlockRayTraceResult)) {
			return ActionResultType.PASS;
		}

		BlockRayTraceResult brtr = (BlockRayTraceResult) rtr;
		BlockPos fluidPos = brtr.getPos();
		BlockState state = world.getBlockState(fluidPos);

		if (world.isBlockModifiable(player, fluidPos) && player.canPlayerEdit(fluidPos, brtr.getFace(), stack)
			&& state.getBlock() instanceof IBucketPickupHandler) {
			Fluid fluid = ((IBucketPickupHandler) state.getBlock()).pickupFluid(world, fluidPos, state);
			if (fluid != Fluids.EMPTY) {
				player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ,
						fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
		if (tryPickupFluid(world, player, player.getHeldItem(hand)) != ActionResultType.SUCCESS) {
			changeMode(player, player.getHeldItem(hand), hand);
		}

		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void inventoryTick(ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean held) {
		if (!stack.getOrCreateTag().getBoolean(TAG_ACTIVE) || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;
		AxisAlignedBB bBox = player.getBoundingBox().grow(7);
		List<ItemEntity> itemList = world.getEntitiesWithinAABB(ItemEntity.class, bBox);

		for (ItemEntity item : itemList) {
			if (ItemHelper.hasSpace(player.inventory.mainInventory, item.getItem())) {
				WorldHelper.gravitateEntityTowards(item, player.posX, player.posY, player.posZ);
			}
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
		if (tile != null) {
			List<ItemEntity> list = world.getEntitiesWithinAABB(ItemEntity.class, tile.getEffectBounds());
			for (ItemEntity item : list) {
				WorldHelper.gravitateEntityTowards(item, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
				if (!world.isRemote && item.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21 && item.isAlive()) {
					suckDumpItem(item, tile);
				}
			}
		}
	}

	private void suckDumpItem(ItemEntity item, DMPedestalTile tile) {
		Map<Direction, TileEntity> map = WorldHelper.getAdjacentTileEntitiesMapped(tile.getWorld(), tile);
		for (Map.Entry<Direction, TileEntity> e : map.entrySet()) {
			IItemHandler inv = WorldHelper.getItemHandler(e.getValue(), e.getKey());
			ItemStack result = ItemHandlerHelper.insertItemStacked(inv, item.getItem(), false);
			if (result.isEmpty()) {
				item.remove();
				return;
			} else {
				item.setItem(result);
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		return Lists.newArrayList(
				new TranslationTextComponent("pe.bhb.pedestal1").applyTextStyle(TextFormatting.BLUE),
				new TranslationTextComponent("pe.bhb.pedestal2").applyTextStyle(TextFormatting.BLUE)
		);
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof AlchChestTile)) {
			return;
		}
		AlchChestTile tile = (AlchChestTile) te;
		if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE)) {
			AxisAlignedBB aabb = new AxisAlignedBB(tile.getPos().getX() - 5, tile.getPos().getY() - 5, tile.getPos().getZ() - 5,
					tile.getPos().getX() + 5, tile.getPos().getY() + 5, tile.getPos().getZ() + 5);
			double centeredX = tile.getPos().getX() + 0.5;
			double centeredY = tile.getPos().getY() + 0.5;
			double centeredZ = tile.getPos().getZ() + 0.5;

			for (ItemEntity e : tile.getWorld().getEntitiesWithinAABB(ItemEntity.class, aabb)) {
				WorldHelper.gravitateEntityTowards(e, centeredX, centeredY, centeredZ);
				if (!e.getEntityWorld().isRemote && e.isAlive() && e.getDistanceSq(centeredX, centeredY, centeredZ) < 1.21) {
					tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
						ItemStack result = ItemHandlerHelper.insertItemStacked(inv, e.getItem(), false);
						if (!result.isEmpty()) {
							e.setItem(result);
						} else {
							e.remove();
						}
					});
				}
			}
		}
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull PlayerEntity player, @Nonnull ItemStack stack) {
		if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE)) {
			for (ItemEntity e : player.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, player.getBoundingBox().grow(5))) {
				WorldHelper.gravitateEntityTowards(e, player.posX, player.posY, player.posZ);
			}
		}
		return false;
	}
}