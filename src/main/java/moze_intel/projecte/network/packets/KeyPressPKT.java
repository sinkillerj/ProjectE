package moze_intel.projecte.network.packets;

import java.util.Optional;
import java.util.function.Supplier;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.armor.GemArmorBase;
import moze_intel.projecte.gameObjs.items.armor.GemChest;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.handlers.InternalAbilities;
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
import net.minecraftforge.fml.network.NetworkEvent;

public class KeyPressPKT {

	private final PEKeybind key;

	public KeyPressPKT(PEKeybind key) {
		this.key = key;
	}

	public static void encode(KeyPressPKT pkt, PacketBuffer buf) {
		buf.writeVarInt(pkt.key.ordinal());
	}

	public static KeyPressPKT decode(PacketBuffer buf) {
		return new KeyPressPKT(PEKeybind.values()[buf.readVarInt()]);
	}

	public static class Handler {

		public static void handle(final KeyPressPKT message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity player = ctx.get().getSender();
				if (player == null) {
					return;
				}
				Optional<InternalAbilities> cap = player.getCapability(InternalAbilities.CAPABILITY).resolve();
				if (!cap.isPresent()) {
					return;
				}
				InternalAbilities internalAbilities = cap.get();
				if (message.key == PEKeybind.ARMOR_TOGGLE) {
					if (player.isSneaking()) {
						ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
						if (!helm.isEmpty() && helm.getItem() instanceof GemHelmet) {
							GemHelmet.toggleNightVision(helm, player);
						}
					} else {
						ItemStack boots = player.getItemStackFromSlot(EquipmentSlotType.FEET);
						if (!boots.isEmpty() && boots.getItem() instanceof GemFeet) {
							((GemFeet) boots.getItem()).toggleStepAssist(boots, player);
						}
					}
					return;
				}

				for (Hand hand : Hand.values()) {
					ItemStack stack = player.getHeldItem(hand);
					switch (message.key) {
						case CHARGE:
							if (!stack.isEmpty()) {
								Optional<IItemCharge> chargeCapability = stack.getCapability(ProjectEAPI.CHARGE_ITEM_CAPABILITY).resolve();
								if (chargeCapability.isPresent() && chargeCapability.get().changeCharge(player, stack, hand)) {
									return;
								}
							}
							if (hand == Hand.MAIN_HAND && (ProjectEConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty()) && GemArmorBase.hasAnyPiece(player)) {
								internalAbilities.setGemState(!internalAbilities.getGemState());
								ILangEntry langEntry = internalAbilities.getGemState() ? PELang.GEM_ACTIVATE : PELang.GEM_DEACTIVATE;
								player.sendMessage(langEntry.translate(), Util.DUMMY_UUID);
								return;
							}
							break;
						case EXTRA_FUNCTION:
							if (!stack.isEmpty()) {
								Optional<IExtraFunction> extraFunctionCapability = stack.getCapability(ProjectEAPI.EXTRA_FUNCTION_ITEM_CAPABILITY).resolve();
								if (extraFunctionCapability.isPresent() && extraFunctionCapability.get().doExtraFunction(stack, player, hand)) {
									return;
								}
							}
							if (hand == Hand.MAIN_HAND && (ProjectEConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty()) && internalAbilities.getGemState()) {
								ItemStack chestplate = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
								if (!chestplate.isEmpty() && chestplate.getItem() instanceof GemChest && internalAbilities.getGemCooldown() == 0) {
									((GemChest) chestplate.getItem()).doExplode(player);
									internalAbilities.resetGemCooldown();
									return;
								}
							}
							break;
						case FIRE_PROJECTILE:
							if (!stack.isEmpty() && internalAbilities.getProjectileCooldown() == 0) {
								Optional<IProjectileShooter> projectileShooterCapability = stack.getCapability(ProjectEAPI.PROJECTILE_SHOOTER_ITEM_CAPABILITY).resolve();
								if (projectileShooterCapability.isPresent() && projectileShooterCapability.get().shootProjectile(player, stack, hand)) {
									PlayerHelper.swingItem(player, hand);
									internalAbilities.resetProjectileCooldown();
									return;
								}
							}
							if (hand == Hand.MAIN_HAND && (ProjectEConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty()) && internalAbilities.getGemState()) {
								ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
								if (!helmet.isEmpty() && helmet.getItem() instanceof GemHelmet) {
									((GemHelmet) helmet.getItem()).doZap(player);
									return;
								}
							}
							break;
						case MODE:
							if (!stack.isEmpty()) {
								Optional<IModeChanger> modeChangerCapability = stack.getCapability(ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY).resolve();
								if (modeChangerCapability.isPresent() && modeChangerCapability.get().changeMode(player, stack, hand)) {
									return;
								}
							}
							break;
					}
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}