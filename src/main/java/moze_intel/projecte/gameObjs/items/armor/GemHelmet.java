package moze_intel.projecte.gameObjs.items.armor;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class GemHelmet extends GemArmorBase {

	public GemHelmet(Properties props) {
		super(EquipmentSlot.HEAD, props);
	}

	public static void toggleNightVision(ItemStack helm, Player player) {
		boolean value;
		CompoundTag helmetTag = helm.getOrCreateTag();
		if (helmetTag.contains(Constants.NBT_KEY_NIGHT_VISION, Tag.TAG_BYTE)) {
			value = !helmetTag.getBoolean(Constants.NBT_KEY_NIGHT_VISION);
			helmetTag.putBoolean(Constants.NBT_KEY_NIGHT_VISION, value);
		} else {
			//If we don't have the tag count that as it already being "false"
			helmetTag.putBoolean(Constants.NBT_KEY_NIGHT_VISION, true);
			value = true;
		}
		if (value) {
			player.sendMessage(PELang.NIGHT_VISION.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED), Util.NIL_UUID);
		} else {
			player.sendMessage(PELang.NIGHT_VISION.translate(ChatFormatting.RED, PELang.GEM_DISABLED), Util.NIL_UUID);
		}
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.GEM_LORE_HELM.translate());
		tooltips.add(PELang.NIGHT_VISION_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.HELMET_TOGGLE)));
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_NIGHT_VISION)) {
			tooltips.add(PELang.NIGHT_VISION.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED));
		} else {
			tooltips.add(PELang.NIGHT_VISION.translate(ChatFormatting.RED, PELang.GEM_DISABLED));
		}
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide) {
			player.getCapability(InternalTimers.CAPABILITY).ifPresent(handler -> {
				handler.activateHeal();
				if (player.getHealth() < player.getMaxHealth() && handler.canHeal()) {
					player.heal(2.0F);
				}
			});

			if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_NIGHT_VISION)) {
				player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
			} else {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
		}
	}

	public void doZap(Player player) {
		if (ProjectEConfig.server.difficulty.offensiveAbilities.get()) {
			BlockHitResult strikeResult = PlayerHelper.getBlockLookingAt(player, 120.0F);
			if (strikeResult.getType() != Type.MISS) {
				BlockPos strikePos = strikeResult.getBlockPos();
				Level level = player.getCommandSenderWorld();
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
				if (lightning != null) {
					lightning.moveTo(Vec3.atCenterOf(strikePos));
					lightning.setCause((ServerPlayer) player);
					level.addFreshEntity(lightning);
				}
			}
		}
	}
}