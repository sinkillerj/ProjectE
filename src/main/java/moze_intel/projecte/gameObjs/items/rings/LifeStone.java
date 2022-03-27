package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class LifeStone extends PEToggleItem implements IPedestalItem {

	public LifeStone(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean held) {
		if (level.isClientSide || slot >= Inventory.getSelectionSize() || !(entity instanceof Player player)) {
			return;
		}
		super.inventoryTick(stack, level, entity, slot, held);
		CompoundTag nbt = stack.getOrCreateTag();
		if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
			if (!consumeFuel(player, stack, 2 * 64, false)) {
				nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
			} else {
				player.getCapability(InternalTimers.CAPABILITY, null).ifPresent(timers -> {
					timers.activateFeed();
					if (player.getFoodData().needsFood() && timers.canFeed()) {
						level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 1, 1);
						player.getFoodData().eat(2, 10);
						level.gameEvent(player, GameEvent.EAT, player.eyeBlockPosition());
						player.gameEvent(GameEvent.EAT);
						removeEmc(stack, 64);
					}
					timers.activateHeal();
					if (player.getHealth() < player.getMaxHealth() && timers.canHeal()) {
						level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 1, 1);
						player.heal(2.0F);
						removeEmc(stack, 64);
					}
				});
			}
		}
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.life.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, pedestal.getEffectBounds())) {
					if (player.getHealth() < player.getMaxHealth()) {
						level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.BLOCKS, 1, 1);
						player.heal(1.0F); // 1/2 heart
					}
					if (player.getFoodData().needsFood()) {
						level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.BLOCKS, 1, 1);
						player.getFoodData().eat(1, 1); // 1/2 shank
						level.gameEvent(player, GameEvent.EAT, player.eyeBlockPosition());
						player.gameEvent(GameEvent.EAT);
					}
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.life.get());
			} else {
				pedestal.decrementActivityCooldown();
			}
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.life.get() != -1) {
			list.add(PELang.PEDESTAL_LIFE_STONE_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_LIFE_STONE_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.life.get())));
		}
		return list;
	}
}