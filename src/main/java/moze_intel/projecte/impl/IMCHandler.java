package moze_intel.projecte.impl;

import java.util.Objects;
import java.util.stream.Stream;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.CustomEMCRegistration;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

public class IMCHandler {

	public static void handleMessages(InterModProcessEvent event) {
		WorldTransmutations.setWorldTransmutation(getMessages(event, IMCMethods.REGISTER_WORLD_TRANSMUTATION, WorldTransmutationEntry.class).map(msg -> {
			WorldTransmutationEntry transmutationEntry = msg.message();
			if (transmutationEntry.altResult() == null) {
				PECore.debugLog("Mod: '{}' registered World Transmutation from: '{}', to: '{}'", msg.sender(), transmutationEntry.origin(),
						transmutationEntry.result());
			} else {
				PECore.debugLog("Mod: '{}' registered World Transmutation from: '{}', to: '{}', with sneak output of: '{}'", msg.sender(),
						transmutationEntry.origin(), transmutationEntry.result(), transmutationEntry.altResult());
			}
			return transmutationEntry;
		}));

		getMessages(event, IMCMethods.REGISTER_CUSTOM_EMC, CustomEMCRegistration.class)
				.forEach(msg -> APICustomEMCMapper.INSTANCE.registerCustomEMC(msg.sender(), msg.message()));
	}

	private record TypedIMCMessage<OBJ>(String sender, OBJ message) {
	}

	private static <OBJ> Stream<TypedIMCMessage<OBJ>> getMessages(InterModProcessEvent event, String methodName, Class<OBJ> clazz) {
		return event.getIMCStream(methodName::equals).map(msg -> {
			Object obj = msg.messageSupplier().get();
			if (clazz.isInstance(obj)) {
				return new TypedIMCMessage<>(msg.senderModId(), clazz.cast(obj));
			}
			return null;
		}).filter(Objects::nonNull);
	}
}