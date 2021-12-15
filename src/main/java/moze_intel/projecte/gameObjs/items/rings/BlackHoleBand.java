package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import java.util.EnumMap;
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
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
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
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BlackHoleBand extends PEToggleItem implements IAlchBagItem, IAlchChestItem, IPedestalItem {

	public BlackHoleBand(Properties props) {
		super(props);
		addItemCapability(AlchBagItemCapabilityWrapper::new);
		addItemCapability(AlchChestItemCapabilityWrapper::new);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	private ActionResultType tryPickupFluid(World world, PlayerEntity player, ItemStack stack) {
		BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
		if (result.getType() != Type.BLOCK) {
			return ActionResultType.PASS;
		}
		BlockPos fluidPos = result.getBlockPos();
		BlockState state = world.getBlockState(fluidPos);
		if (world.mayInteract(player, fluidPos) && player.mayUseItemAt(fluidPos, result.getDirection(), stack) && state.getBlock() instanceof IBucketPickupHandler) {
			Fluid fluid = ((IBucketPickupHandler) state.getBlock()).takeLiquid(world, fluidPos, state);
			if (fluid != Fluids.EMPTY) {
				player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
						fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> use(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		if (tryPickupFluid(world, player, player.getItemInHand(hand)) != ActionResultType.SUCCESS) {
			changeMode(player, player.getItemInHand(hand), hand);
		}
		return ActionResult.success(player.getItemInHand(hand));
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean held) {
		if (entity instanceof PlayerEntity && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			PlayerEntity player = (PlayerEntity) entity;
			AxisAlignedBB bBox = player.getBoundingBox().inflate(7);
			List<ItemEntity> itemList = world.getEntitiesOfClass(ItemEntity.class, bBox);
			for (ItemEntity item : itemList) {
				if (ItemHelper.simulateFit(player.inventory.items, item.getItem()) < item.getItem().getCount()) {
					WorldHelper.gravitateEntityTowards(item, player.getX(), player.getY(), player.getZ());
				}
			}
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isClientSide) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				Map<Direction, IItemHandler> nearbyHandlers = new EnumMap<>(Direction.class);
				for (ItemEntity item : world.getEntitiesOfClass(ItemEntity.class, tile.getEffectBounds())) {
					if (item.isAlive()) {
						WorldHelper.gravitateEntityTowards(item, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
						if (item.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21) {
							for (Direction dir : Direction.values()) {
								//Cache the item handlers in various spots so that we only query each neighboring position once
								IItemHandler inv = nearbyHandlers.computeIfAbsent(dir, direction -> {
									TileEntity candidate = WorldHelper.getTileEntity(world, pos.relative(dir));
									if (candidate == null) {
										return null;
									}
									return WorldHelper.getItemHandler(candidate, dir);
								});
								ItemStack result = ItemHandlerHelper.insertItemStacked(inv, item.getItem(), false);
								if (result.isEmpty()) {
									item.remove();
									break;
								}
								item.setItem(result);
							}
						}
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		return Lists.newArrayList(PELang.PEDESTAL_BLACK_HOLE_BAND_1.translateColored(TextFormatting.BLUE),
				PELang.PEDESTAL_BLACK_HOLE_BAND_2.translateColored(TextFormatting.BLUE));
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
		AlchChestTile tile = WorldHelper.getTileEntity(AlchChestTile.class, world, pos, true);
		if (tile != null && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			int tileX = pos.getX();
			int tileY = pos.getY();
			int tileZ = pos.getZ();
			AxisAlignedBB aabb = new AxisAlignedBB(tileX - 5, tileY - 5, tileZ - 5, tileX + 5, tileY + 5, tileZ + 5);
			double centeredX = tileX + 0.5;
			double centeredY = tileY + 0.5;
			double centeredZ = tileZ + 0.5;
			for (ItemEntity e : world.getEntitiesOfClass(ItemEntity.class, aabb)) {
				WorldHelper.gravitateEntityTowards(e, centeredX, centeredY, centeredZ);
				if (!e.getCommandSenderWorld().isClientSide && e.isAlive() && e.distanceToSqr(centeredX, centeredY, centeredZ) < 1.21) {
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
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			for (ItemEntity e : player.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(5))) {
				WorldHelper.gravitateEntityTowards(e, player.getX(), player.getY(), player.getZ());
			}
		}
		return false;
	}
}