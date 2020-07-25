package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PEKeybind;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class GemFeet extends GemArmorBase implements IFlightProvider, IStepAssister {

	private static final UUID MODIFIER = UUID.fromString("A4334312-DFF8-4582-9F4F-62AD0C070475");

	private final Multimap<Attribute, AttributeModifier> attributes;

	public GemFeet(Properties props) {
		super(EquipmentSlotType.FEET, props);
		Builder<Attribute, AttributeModifier> attributesBuilder = ImmutableMultimap.builder();
		attributesBuilder.putAll(getAttributeModifiers(EquipmentSlotType.FEET));
		attributesBuilder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MODIFIER, "Armor modifier", 1.0, Operation.MULTIPLY_TOTAL));
		this.attributes = attributesBuilder.build();
	}

	public static boolean isStepAssistEnabled(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().contains(Constants.NBT_KEY_STEP_ASSIST) && stack.getTag().getBoolean(Constants.NBT_KEY_STEP_ASSIST);
	}

	public void toggleStepAssist(ItemStack boots, PlayerEntity player) {
		boolean value;
		CompoundNBT bootsTag = boots.getOrCreateTag();
		if (bootsTag.contains(Constants.NBT_KEY_STEP_ASSIST)) {
			bootsTag.putBoolean(Constants.NBT_KEY_STEP_ASSIST, !bootsTag.getBoolean(Constants.NBT_KEY_STEP_ASSIST));
			value = bootsTag.getBoolean(Constants.NBT_KEY_STEP_ASSIST);
		} else {
			//If we don't have the tag count that as it already being "false"
			bootsTag.putBoolean(Constants.NBT_KEY_STEP_ASSIST, true);
			value = true;
		}
		player.sendMessage(new TranslationTextComponent("pe.gem.stepassist_tooltip").appendString(" ")
				.append(new TranslationTextComponent(value ? "pe.gem.enabled" : "pe.gem.disabled").mergeStyle(value ? TextFormatting.GREEN : TextFormatting.RED)), Util.DUMMY_UUID);
	}

	private static boolean isJumpPressed() {
		return DistExecutor.runForDist(() -> () -> Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown(), () -> () -> false);
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isRemote) {
			ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
			playerMP.fallDistance = 0;
		} else {
			if (!player.abilities.isFlying && isJumpPressed()) {
				player.setMotion(player.getMotion().add(0, 0.1, 0));
			}
			if (!player.isOnGround()) {
				if (player.getMotion().getY() <= 0) {
					player.setMotion(player.getMotion().mul(1, 0.9, 1));
				}
				if (!player.abilities.isFlying) {
					if (player.moveForward < 0) {
						player.setMotion(player.getMotion().mul(0.9, 1, 0.9));
					} else if (player.moveForward > 0 && player.getMotion().lengthSquared() < 3) {
						player.setMotion(player.getMotion().mul(1.1, 1, 1.1));
					}
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltips, ITooltipFlag flags) {
		tooltips.add(new TranslationTextComponent("pe.gem.feet.lorename"));
		tooltips.add(new TranslationTextComponent("pe.gem.stepassist.prompt", ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)));

		boolean enabled = isStepAssistEnabled(stack);
		tooltips.add(new TranslationTextComponent("pe.gem.stepassist_tooltip").appendString(" ")
				.append(new TranslationTextComponent(enabled ? "pe.gem.enabled" : "pe.gem.disabled").mergeStyle(enabled ? TextFormatting.GREEN : TextFormatting.RED)));
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return slot == EquipmentSlotType.FEET ? attributes : super.getAttributeModifiers(slot, stack);
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayerEntity player) {
		return player.getItemStackFromSlot(EquipmentSlotType.FEET) == stack;
	}

	@Override
	public boolean canAssistStep(ItemStack stack, ServerPlayerEntity player) {
		return player.getItemStackFromSlot(EquipmentSlotType.FEET) == stack && isStepAssistEnabled(stack);
	}
}