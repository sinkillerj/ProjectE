package moze_intel.projecte.gameObjs.items.rings;

import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VoidRing extends GemEternalDensity implements IPedestalItem, IExtraFunction {

	public VoidRing(Properties props) {
		super(props);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		PEItems.BLACK_HOLE_BAND.get().inventoryTick(stack, level, entity, slot);
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		return PEItems.BLACK_HOLE_BAND.get().updateInPedestal(stack, level, pos, pedestal);
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription(float tickRate) {
		return PEItems.BLACK_HOLE_BAND.get().getPedestalDescription(tickRate);
	}

	@Override
	public boolean doExtraFunction(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		ItemCooldowns cooldowns = player.getCooldowns();
		if (cooldowns.isOnCooldown(stack)) {
			return false;
		}
		BlockHitResult lookingAt = PlayerHelper.getBlockLookingAt(player, 64);
		BlockPos c;
		if (lookingAt.getType() == Type.MISS) {
			c = BlockPos.containing(PlayerHelper.getLookTarget(player, 32));
		} else {
			c = lookingAt.getBlockPos();
		}
		if (!(player.level() instanceof ServerLevel serverLevel)) {
			return false;
		}
		EntityTeleportEvent event = new EntityTeleportEvent(player, serverLevel, c.getX(), c.getY(), c.getZ());
		if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
			if (player.isPassenger()) {
				player.stopRiding();
			}
			player.resetFallDistance();
			player.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1);
			cooldowns.addCooldown(stack, SharedConstants.TICKS_PER_SECOND / 2);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateInAlchBag(@NotNull PEItemStacksHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		// super is Gem of Eternal Density
		return super.updateInAlchBag(inv, player, stack) | PEItems.BLACK_HOLE_BAND.get().updateInAlchBag(inv, player, stack);
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		// super is Gem of Eternal Density
		return super.updateInAlchChest(level, pos, stack) | PEItems.BLACK_HOLE_BAND.get().updateInAlchChest(level, pos, stack);
	}
}
