package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.DistExecutor;

public class GemFeet extends GemArmorBase implements IFlightProvider, IStepAssister {

	private static final UUID MODIFIER = UUID.fromString("A4334312-DFF8-4582-9F4F-62AD0C070475");

	private final Multimap<Attribute, AttributeModifier> attributes;

	public GemFeet(Properties props) {
		super(EquipmentSlotType.FEET, props);
		Builder<Attribute, AttributeModifier> attributesBuilder = ImmutableMultimap.builder();
		attributesBuilder.putAll(getDefaultAttributeModifiers(EquipmentSlotType.FEET));
		attributesBuilder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MODIFIER, "Armor modifier", 1.0, Operation.MULTIPLY_TOTAL));
		this.attributes = attributesBuilder.build();
	}

	public void toggleStepAssist(ItemStack boots, PlayerEntity player) {
		boolean value;
		CompoundNBT bootsTag = boots.getOrCreateTag();
		if (bootsTag.contains(Constants.NBT_KEY_STEP_ASSIST, NBT.TAG_BYTE)) {
			value = !bootsTag.getBoolean(Constants.NBT_KEY_STEP_ASSIST);
			bootsTag.putBoolean(Constants.NBT_KEY_STEP_ASSIST, value);
		} else {
			//If we don't have the tag count that as it already being "false"
			bootsTag.putBoolean(Constants.NBT_KEY_STEP_ASSIST, true);
			value = true;
		}
		if (value) {
			player.sendMessage(PELang.STEP_ASSIST.translate(TextFormatting.GREEN, PELang.GEM_ENABLED), Util.NIL_UUID);
		} else {
			player.sendMessage(PELang.STEP_ASSIST.translate(TextFormatting.RED, PELang.GEM_DISABLED), Util.NIL_UUID);
		}
	}

	private static boolean isJumpPressed() {
		return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().options.keyJump.isDown(), () -> () -> false);
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClientSide) {
			ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
			playerMP.fallDistance = 0;
		} else {
			if (!player.abilities.flying && isJumpPressed()) {
				player.setDeltaMovement(player.getDeltaMovement().add(0, 0.1, 0));
			}
			if (!player.isOnGround()) {
				if (player.getDeltaMovement().y() <= 0) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0.9, 1));
				}
				if (!player.abilities.flying) {
					if (player.zza < 0) {
						player.setDeltaMovement(player.getDeltaMovement().multiply(0.9, 1, 0.9));
					} else if (player.zza > 0 && player.getDeltaMovement().lengthSqr() < 3) {
						player.setDeltaMovement(player.getDeltaMovement().multiply(1.1, 1, 1.1));
					}
				}
			}
		}
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(PELang.GEM_LORE_FEET.translate());
		tooltips.add(PELang.STEP_ASSIST_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.BOOTS_TOGGLE)));
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_STEP_ASSIST)) {
			tooltips.add(PELang.STEP_ASSIST.translate(TextFormatting.GREEN, PELang.GEM_ENABLED));
		} else {
			tooltips.add(PELang.STEP_ASSIST.translate(TextFormatting.RED, PELang.GEM_DISABLED));
		}
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return slot == EquipmentSlotType.FEET ? attributes : super.getAttributeModifiers(slot, stack);
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayerEntity player) {
		return player.getItemBySlot(EquipmentSlotType.FEET) == stack;
	}

	@Override
	public boolean canAssistStep(ItemStack stack, ServerPlayerEntity player) {
		return player.getItemBySlot(EquipmentSlotType.FEET) == stack && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_STEP_ASSIST);
	}
}