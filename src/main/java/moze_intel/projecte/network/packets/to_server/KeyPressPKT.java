package moze_intel.projecte.network.packets.to_server;

import java.util.Optional;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.armor.GemArmorBase;
import moze_intel.projecte.gameObjs.items.armor.GemChest;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.network.packets.IPEPacket;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KeyPressPKT implements IPEPacket {

	private final PEKeybind key;

	public KeyPressPKT(PEKeybind key) {
		this.key = key;
	}

	@Override
	public void handle(Context context) {
		ServerPlayerEntity player = context.getSender();
		if (player == null) {
			return;
		}
		if (key == PEKeybind.HELMET_TOGGLE) {
			ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
			if (!helm.isEmpty() && helm.getItem() instanceof GemHelmet) {
				GemHelmet.toggleNightVision(helm, player);
			}
			return;
		} else if (key == PEKeybind.BOOTS_TOGGLE) {
			ItemStack boots = player.getItemStackFromSlot(EquipmentSlotType.FEET);
			if (!boots.isEmpty() && boots.getItem() instanceof GemFeet) {
				((GemFeet) boots.getItem()).toggleStepAssist(boots, player);
			}
			return;
		}
		Optional<InternalAbilities> cap = player.getCapability(InternalAbilities.CAPABILITY).resolve();
		if (!cap.isPresent()) {
			return;
		}
		InternalAbilities internalAbilities = cap.get();
		for (Hand hand : Hand.values()) {
			ItemStack stack = player.getHeldItem(hand);
			switch (key) {
				case CHARGE:
					if (tryPerformCapability(stack, ProjectEAPI.CHARGE_ITEM_CAPABILITY, capability -> capability.changeCharge(player, stack, hand))) {
						return;
					} else if (hand == Hand.MAIN_HAND && isSafe(stack) && GemArmorBase.hasAnyPiece(player)) {
						internalAbilities.setGemState(!internalAbilities.getGemState());
						ILangEntry langEntry = internalAbilities.getGemState() ? PELang.GEM_ACTIVATE : PELang.GEM_DEACTIVATE;
						player.sendMessage(langEntry.translate(), Util.DUMMY_UUID);
						return;
					}
					break;
				case EXTRA_FUNCTION:
					if (tryPerformCapability(stack, ProjectEAPI.EXTRA_FUNCTION_ITEM_CAPABILITY, capability -> capability.doExtraFunction(stack, player, hand))) {
						return;
					} else if (hand == Hand.MAIN_HAND && isSafe(stack) && internalAbilities.getGemState()) {
						ItemStack chestplate = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
						if (!chestplate.isEmpty() && chestplate.getItem() instanceof GemChest && internalAbilities.getGemCooldown() == 0) {
							((GemChest) chestplate.getItem()).doExplode(player);
							internalAbilities.resetGemCooldown();
							return;
						}
					}
					break;
				case FIRE_PROJECTILE:
					if (!stack.isEmpty() && internalAbilities.getProjectileCooldown() == 0 &&
						tryPerformCapability(stack, ProjectEAPI.PROJECTILE_SHOOTER_ITEM_CAPABILITY, capability -> capability.shootProjectile(player, stack, hand))) {
						PlayerHelper.swingItem(player, hand);
						internalAbilities.resetProjectileCooldown();
					}
					if (hand == Hand.MAIN_HAND && isSafe(stack) && internalAbilities.getGemState()) {
						ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
						if (!helmet.isEmpty() && helmet.getItem() instanceof GemHelmet) {
							((GemHelmet) helmet.getItem()).doZap(player);
							return;
						}
					}
					break;
				case MODE:
					if (tryPerformCapability(stack, ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY, capability -> capability.changeMode(player, stack, hand))) {
						return;
					}
					break;
			}
		}
	}

	private <CAPABILITY> boolean tryPerformCapability(ItemStack stack, Capability<CAPABILITY> capability, NonNullPredicate<CAPABILITY> perform) {
		return !stack.isEmpty() && stack.getCapability(capability).filter(perform).isPresent();
	}

	private static boolean isSafe(ItemStack stack) {
		return ProjectEConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeEnumValue(key);
	}

	public static KeyPressPKT decode(PacketBuffer buf) {
		return new KeyPressPKT(buf.readEnumValue(PEKeybind.class));
	}
}