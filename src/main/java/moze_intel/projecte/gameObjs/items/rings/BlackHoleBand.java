package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class BlackHoleBand extends PEToggleItem implements IAlchBagItem, IAlchChestItem, IPedestalItem, ICapabilityAware {

	public BlackHoleBand(Properties props) {
		super(props);
	}

	private InteractionResult tryPickupFluid(Level level, Player player, ItemStack stack) {
		BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (result.getType() != Type.BLOCK) {
			return InteractionResult.PASS;
		}
		BlockPos fluidPos = result.getBlockPos();
		BlockState state = level.getBlockState(fluidPos);
		if (level.mayInteract(player, fluidPos) && player.mayUseItemAt(fluidPos, result.getDirection(), stack) && state.getBlock() instanceof BucketPickup pickup) {
			Optional<SoundEvent> sound = pickup.getPickupSound(state);
			ItemStack itemStack = pickup.pickupBlock(player, level, fluidPos, state);
			if (!itemStack.isEmpty()) {
				sound.ifPresent(soundEvent -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent,
						SoundSource.PLAYERS, 1.0F, 1.0F));
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
		return InteractionResult.PASS;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		InteractionResult result = tryPickupFluid(level, player, stack);
		if (!result.consumesAction() && changeMode(player, stack, hand)) {
			result = InteractionResult.sidedSuccess(level.isClientSide);
		}
		return ItemHelper.actionResultFromType(result, stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean held) {
		if (entity instanceof Player player && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(7))) {
				if (ItemHelper.simulateFit(player.getInventory().items, item.getItem()) < item.getItem().getCount()) {
					WorldHelper.gravitateEntityTowards(item, player.position());
				}
			}
		}
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		Vec3 target = pos.getCenter();
		Map<Direction, IItemHandler> nearbyHandlers = new EnumMap<>(Direction.class);
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, pedestal.getEffectBounds(), ent -> !ent.isSpectator() && ent.isAlive())) {
			WorldHelper.gravitateEntityTowards(item, target);
			if (!level.isClientSide && item.distanceToSqr(target) < 1.21) {
				for (Direction dir : Direction.values()) {
					//Cache the item handlers in various spots so that we only query each neighboring position once
					IItemHandler inv = nearbyHandlers.computeIfAbsent(dir, direction -> WorldHelper.getCapability(level, ItemHandler.BLOCK, pos.relative(dir), dir));
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

	@NotNull
	@Override
	public List<Component> getPedestalDescription(float tickRate) {
		return Lists.newArrayList(PELang.PEDESTAL_BLACK_HOLE_BAND_1.translateColored(ChatFormatting.BLUE),
				PELang.PEDESTAL_BLACK_HOLE_BAND_2.translateColored(ChatFormatting.BLUE));
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			IItemHandler handler = WorldHelper.getCapability(level, ItemHandler.BLOCK, pos, null);
			if (handler != null) {
				AABB aabb = new AABB(pos).inflate(5);
				Vec3 center = aabb.getCenter();
				for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, aabb, ent -> !ent.isSpectator() && ent.isAlive())) {
					WorldHelper.gravitateEntityTowards(e, center);
					if (!level.isClientSide && e.distanceToSqr(center) < 1.21) {
						ItemStack result = ItemHandlerHelper.insertItemStacked(handler, e.getItem(), false);
						if (!result.isEmpty()) {
							e.setItem(result);
						} else {
							e.discard();
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean updateInAlchBag(@NotNull IItemHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			for (ItemEntity e : player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(5))) {
				WorldHelper.gravitateEntityTowards(e, player.position());
			}
		}
		return false;
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}
}