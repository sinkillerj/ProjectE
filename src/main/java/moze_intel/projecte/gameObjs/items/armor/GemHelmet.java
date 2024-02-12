package moze_intel.projecte.gameObjs.items.armor;

import java.util.List;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemHelmet extends GemArmorBase {

	public GemHelmet(Properties props) {
		super(ArmorItem.Type.HELMET, props);
	}

	public static void toggleNightVision(ItemStack helm, Player player) {
		boolean oldValue = helm.getData(PEAttachmentTypes.NIGHT_VISION);
		helm.setData(PEAttachmentTypes.NIGHT_VISION, !oldValue);
		if (oldValue) {
			player.sendSystemMessage(PELang.NIGHT_VISION.translate(ChatFormatting.RED, PELang.GEM_DISABLED));
		} else {
			player.sendSystemMessage(PELang.NIGHT_VISION.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED));
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.GEM_LORE_HELM.translate());
		tooltips.add(PELang.NIGHT_VISION_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.HELMET_TOGGLE)));
		if (stack.getData(PEAttachmentTypes.NIGHT_VISION)) {
			tooltips.add(PELang.NIGHT_VISION.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED));
		} else {
			tooltips.add(PELang.NIGHT_VISION.translate(ChatFormatting.RED, PELang.GEM_DISABLED));
		}
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (isArmorSlot(slot) && !level.isClientSide && entity instanceof Player player) {
			InternalTimers timers = player.getData(PEAttachmentTypes.INTERNAL_TIMERS);
			timers.activateHeal();
			if (player.getHealth() < player.getMaxHealth() && timers.canHeal()) {
				player.heal(2.0F);
			}

			if (stack.getData(PEAttachmentTypes.NIGHT_VISION)) {
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 11 * SharedConstants.TICKS_PER_SECOND, 0, true, false));
			} else {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
		}
	}

	public void doZap(Player player) {
		if (ProjectEConfig.server.difficulty.offensiveAbilities.get()) {
			BlockHitResult strikeResult = PlayerHelper.getBlockLookingAt(player, 120.0F);
			if (strikeResult.getType() != HitResult.Type.MISS) {
				BlockPos strikePos = strikeResult.getBlockPos();
				Level level = player.level();
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
				if (lightning != null) {
					lightning.moveTo(strikePos.getCenter());
					lightning.setCause((ServerPlayer) player);
					level.addFreshEntity(lightning);
				}
			}
		}
	}
}