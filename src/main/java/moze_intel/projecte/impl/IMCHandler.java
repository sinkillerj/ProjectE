package moze_intel.projecte.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.CustomEMCRegistration;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.NSSCreatorInfo;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import moze_intel.projecte.api.nss.NSSCreator;
import moze_intel.projecte.emc.json.NSSSerializer;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraftforge.fml.InterModComms;

public class IMCHandler {

	public static void handleMessages() {
		List<WorldTransmutationEntry> entries = new ArrayList<>();
		InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_WORLD_TRANSMUTATION::equals)
				.filter(msg -> msg.messageSupplier().get() instanceof WorldTransmutationEntry)
				.forEach(msg -> {
					WorldTransmutationEntry transmutationEntry = (WorldTransmutationEntry) msg.messageSupplier().get();
					entries.add(transmutationEntry);
					if (transmutationEntry.getAltResult() == null) {
						PECore.debugLog("Mod: '{}' registered World Transmutation from: '{}', to: '{}'", msg.senderModId(),
								transmutationEntry.getOrigin(), transmutationEntry.getResult());
					} else {
						PECore.debugLog("Mod: '{}' registered World Transmutation from: '{}', to: '{}', with sneak output of: '{}'", msg.senderModId(),
								transmutationEntry.getOrigin(), transmutationEntry.getResult(), transmutationEntry.getAltResult());
					}
				});
		WorldTransmutations.setWorldTransmutation(entries);

		InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_CUSTOM_EMC::equals)
				.filter(msg -> msg.messageSupplier().get() instanceof CustomEMCRegistration)
				.forEach(msg -> APICustomEMCMapper.INSTANCE.registerCustomEMC(msg.senderModId(), (CustomEMCRegistration) msg.messageSupplier().get()));

		//Note: It is first come first serve. If we already received a value for it, we don't try to overwrite it, but we do log a warning
		Map<String, NSSCreator> creators = new HashMap<>();
		InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_NSS_SERIALIZER::equals)
				.filter(msg -> msg.messageSupplier().get() instanceof NSSCreatorInfo)
				.forEach(msg -> {
					NSSCreatorInfo creatorInfo = (NSSCreatorInfo) msg.messageSupplier().get();
					String key = creatorInfo.getKey();
					if (creators.containsKey(key)) {
						PECore.LOGGER.warn("Mod: '{}' tried to register NSS creator with key: '{}', but another mod already registered that key.", msg.senderModId(), key);
					} else {
						creators.put(key, creatorInfo.getCreator());
						PECore.debugLog("Mod: '{}' registered NSS creator with key: '{}'", msg.senderModId(), key);
					}
				});
		NSSSerializer.INSTANCE.setCreators(creators);
	}
}