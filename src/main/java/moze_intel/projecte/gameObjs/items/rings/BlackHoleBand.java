package moze_intel.projecte.gameObjs.items.rings;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
				//noinspection OptionalIsPresent - Capturing lambda
				if (sound.isPresent()) {
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
				}
				return WorldHelper.sidedSuccess(level);
			}
		}
		return InteractionResult.PASS;
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		InteractionResult result = tryPickupFluid(level, player, stack);
		if (!result.consumesAction() && changeMode(player, stack, hand)) {
			result = WorldHelper.sidedSuccess(level);
		}
		return ItemHelper.actionResultFromType(result, stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (entity instanceof Player player && stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(7))) {
				if (ItemHelper.simulateFit(ItemHelper.getInventoryStacks(player.getInventory()), item.getItem()) < item.getItem().getCount()) {
					WorldHelper.gravitateEntityTowards(item, player.position());
				}
			}
		}
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		Vec3 target = Vec3.atCenterOf(pos);
		Map<Direction, ResourceHandler<ItemResource>> nearbyHandlers = new EnumMap<>(Direction.class);
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, pedestal.getEffectBounds(), ent -> !ent.isSpectator() && ent.isAlive())) {
			WorldHelper.gravitateEntityTowards(item, target);
			if (!level.isClientSide() && item.distanceToSqr(target) < 1.21) {
				for (Direction dir : Constants.DIRECTIONS) {
					//Cache the item handlers in various spots so that we only query each neighboring position once
					ResourceHandler<ItemResource> inv = nearbyHandlers.get(dir);
					if (inv == null) {
						inv = WorldHelper.getItemHandler(level, pos.relative(dir), dir);
						nearbyHandlers.put(dir, inv);
					}
					ItemStack result = PEItemStacksHandler.insertStackedRemainder(inv, item.getItem(), false);
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
		return List.of(
				PELang.PEDESTAL_BLACK_HOLE_BAND_1.translateColored(ChatFormatting.BLUE),
				PELang.PEDESTAL_BLACK_HOLE_BAND_2.translateColored(ChatFormatting.BLUE)
		);
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		if (stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			ResourceHandler<ItemResource> handler = WorldHelper.getItemHandler(level, pos, null);
			if (handler != null) {
				AABB aabb = new AABB(pos).inflate(5);
				Vec3 center = aabb.getCenter();
				for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, aabb, ent -> !ent.isSpectator() && ent.isAlive())) {
					WorldHelper.gravitateEntityTowards(e, center);
					if (!level.isClientSide() && e.distanceToSqr(center) < 1.21) {
						ItemStack result = PEItemStacksHandler.insertStackedRemainder(handler, e.getItem(), false);
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
	public boolean updateInAlchBag(@NotNull PEItemStacksHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		if (stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
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
