package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.tile.IDMPedestal;
import moze_intel.projecte.capability.AlchBagItemCapabilityWrapper;
import moze_intel.projecte.capability.AlchChestItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.block_entities.AlchChestTile;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
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

	private InteractionResult tryPickupFluid(Level world, Player player, ItemStack stack) {
		BlockHitResult result = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
		if (result.getType() != Type.BLOCK) {
			return InteractionResult.PASS;
		}
		BlockPos fluidPos = result.getBlockPos();
		BlockState state = world.getBlockState(fluidPos);
		if (world.mayInteract(player, fluidPos) && player.mayUseItemAt(fluidPos, result.getDirection(), stack) && state.getBlock() instanceof BucketPickup pickup) {
			Optional<SoundEvent> sound = pickup.getPickupSound();
			ItemStack itemStack = pickup.pickupBlock(world, fluidPos, state);
			if (!itemStack.isEmpty()) {
				sound.ifPresent(soundEvent -> player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent,
						SoundSource.PLAYERS, 1.0F, 1.0F));
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
		if (tryPickupFluid(world, player, player.getItemInHand(hand)) != InteractionResult.SUCCESS) {
			changeMode(player, player.getItemInHand(hand), hand);
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity entity, int slot, boolean held) {
		if (entity instanceof Player player && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			for (ItemEntity item : world.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(7))) {
				if (ItemHelper.simulateFit(player.getInventory().items, item.getItem()) < item.getItem().getCount()) {
					WorldHelper.gravitateEntityTowards(item, player.getX(), player.getY(), player.getZ());
				}
			}
		}
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockPos pos,
			@Nonnull PEDESTAL pedestal) {
		Map<Direction, IItemHandler> nearbyHandlers = new EnumMap<>(Direction.class);
		for (ItemEntity item : world.getEntitiesOfClass(ItemEntity.class, pedestal.getEffectBounds(), ent -> !ent.isSpectator() && ent.isAlive())) {
			WorldHelper.gravitateEntityTowards(item, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			if (!world.isClientSide && item.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21) {
				for (Direction dir : Direction.values()) {
					//Cache the item handlers in various spots so that we only query each neighboring position once
					IItemHandler inv = nearbyHandlers.computeIfAbsent(dir, direction -> {
						BlockEntity candidate = WorldHelper.getTileEntity(world, pos.relative(dir));
						if (candidate == null) {
							return null;
						}
						return WorldHelper.getItemHandler(candidate, dir);
					});
					ItemStack result = ItemHandlerHelper.insertItemStacked(inv, item.getItem(), false);
					if (result.isEmpty()) {
						item.discard();
						break;
					}
					item.setItem(result);
				}
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		return Lists.newArrayList(PELang.PEDESTAL_BLACK_HOLE_BAND_1.translateColored(ChatFormatting.BLUE),
				PELang.PEDESTAL_BLACK_HOLE_BAND_2.translateColored(ChatFormatting.BLUE));
	}

	@Override
	public boolean updateInAlchChest(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			AlchChestTile tile = WorldHelper.getTileEntity(AlchChestTile.class, world, pos, true);
			if (tile != null) {
				int tileX = pos.getX();
				int tileY = pos.getY();
				int tileZ = pos.getZ();
				AABB aabb = new AABB(tileX - 5, tileY - 5, tileZ - 5, tileX + 5, tileY + 5, tileZ + 5);
				double centeredX = tileX + 0.5;
				double centeredY = tileY + 0.5;
				double centeredZ = tileZ + 0.5;
				for (ItemEntity e : world.getEntitiesOfClass(ItemEntity.class, aabb, ent -> !ent.isSpectator() && ent.isAlive())) {
					WorldHelper.gravitateEntityTowards(e, centeredX, centeredY, centeredZ);
					if (!world.isClientSide && e.distanceToSqr(centeredX, centeredY, centeredZ) < 1.21) {
						tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
							ItemStack result = ItemHandlerHelper.insertItemStacked(inv, e.getItem(), false);
							if (!result.isEmpty()) {
								e.setItem(result);
							} else {
								e.discard();
							}
						});
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull Player player, @Nonnull ItemStack stack) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			for (ItemEntity e : player.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(5))) {
				WorldHelper.gravitateEntityTowards(e, player.getX(), player.getY(), player.getZ());
			}
		}
		return false;
	}
}