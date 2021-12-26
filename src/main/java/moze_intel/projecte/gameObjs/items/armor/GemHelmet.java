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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class GemHelmet extends GemArmorBase {

	public GemHelmet(Properties props) {
		super(EquipmentSlotType.HEAD, props);
	}

	public static void toggleNightVision(ItemStack helm, PlayerEntity player) {
		boolean value;
		CompoundNBT helmetTag = helm.getOrCreateTag();
		if (helmetTag.contains(Constants.NBT_KEY_NIGHT_VISION, NBT.TAG_BYTE)) {
			value = !helmetTag.getBoolean(Constants.NBT_KEY_NIGHT_VISION);
			helmetTag.putBoolean(Constants.NBT_KEY_NIGHT_VISION, value);
		} else {
			//If we don't have the tag count that as it already being "false"
			helmetTag.putBoolean(Constants.NBT_KEY_NIGHT_VISION, true);
			value = true;
		}
		if (value) {
			player.sendMessage(PELang.NIGHT_VISION.translate(TextFormatting.GREEN, PELang.GEM_ENABLED), Util.NIL_UUID);
		} else {
			player.sendMessage(PELang.NIGHT_VISION.translate(TextFormatting.RED, PELang.GEM_DISABLED), Util.NIL_UUID);
		}
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(PELang.GEM_LORE_HELM.translate());
		tooltips.add(PELang.NIGHT_VISION_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.HELMET_TOGGLE)));
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_NIGHT_VISION)) {
			tooltips.add(PELang.NIGHT_VISION.translate(TextFormatting.GREEN, PELang.GEM_ENABLED));
		} else {
			tooltips.add(PELang.NIGHT_VISION.translate(TextFormatting.RED, PELang.GEM_DISABLED));
		}
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClientSide) {
			player.getCapability(InternalTimers.CAPABILITY).ifPresent(handler -> {
				handler.activateHeal();
				if (player.getHealth() < player.getMaxHealth() && handler.canHeal()) {
					player.heal(2.0F);
				}
			});

			if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_NIGHT_VISION)) {
				player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, true, false));
			} else {
				player.removeEffect(Effects.NIGHT_VISION);
			}
		}
	}

	public void doZap(PlayerEntity player) {
		if (ProjectEConfig.server.difficulty.offensiveAbilities.get()) {
			BlockRayTraceResult strikeResult = PlayerHelper.getBlockLookingAt(player, 120.0F);
			if (strikeResult.getType() != Type.MISS) {
				BlockPos strikePos = strikeResult.getBlockPos();
				World world = player.getCommandSenderWorld();
				LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
				if (lightning != null) {
					lightning.moveTo(Vector3d.atCenterOf(strikePos));
					lightning.setCause((ServerPlayerEntity) player);
					world.addFreshEntity(lightning);
				}
			}
		}
	}
}