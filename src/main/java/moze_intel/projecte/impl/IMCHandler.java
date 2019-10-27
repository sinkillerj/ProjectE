package moze_intel.projecte.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.CustomEMCRegistration;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.NSSCreatorInfo;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import moze_intel.projecte.api.nss.NSSCreator;
import moze_intel.projecte.emc.json.NSSSerializer;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.gameObjs.items.rings.TimeWatch;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.InterModComms;

public class IMCHandler {

	public static void handleMessages() {
		Set<EntityType<?>> interd = InterModComms.getMessages(PECore.MODID, IMCMethods.BLACKLIST_INTERDICTION::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof EntityType)
				.map(msg -> (EntityType<?>) msg.getMessageSupplier().get())
				.collect(Collectors.toSet());
		WorldHelper.setInterdictionBlacklist(interd);

		Set<EntityType<?>> swrg = InterModComms.getMessages(PECore.MODID, IMCMethods.BLACKLIST_SWRG::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof EntityType)
				.map(msg -> (EntityType<?>) msg.getMessageSupplier().get())
				.collect(Collectors.toSet());
		WorldHelper.setSwrgBlacklist(swrg);

		Set<TileEntityType<?>> timeWatch = InterModComms.getMessages(PECore.MODID, IMCMethods.BLACKLIST_TIMEWATCH::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof TileEntityType)
				.map(msg -> (TileEntityType<?>) msg.getMessageSupplier().get())
				.collect(Collectors.toSet());
		TimeWatch.setInternalBlacklist(timeWatch);

		EntityRandomizerHelper.setDefaultPeacefulRandomizers(getRandomizerEntities(true));
		EntityRandomizerHelper.setDefaultHostileRandomizers(getRandomizerEntities(false));

		List<WorldTransmutationEntry> entries = InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_WORLD_TRANSMUTATION::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof WorldTransmutationEntry)
				.map(msg -> (WorldTransmutationEntry) msg.getMessageSupplier().get())
				.collect(Collectors.toList());
		WorldTransmutations.setWorldTransmutation(entries);

		InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_CUSTOM_EMC::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof CustomEMCRegistration)
				.forEach(msg -> APICustomEMCMapper.instance.registerCustomEMC(msg.getSenderModId(), (CustomEMCRegistration) msg.getMessageSupplier().get()));

		//Note: It is first come first serve. If we already received a value for it we don't try to overwrite it, but we do log a warning
		Map<String, NSSCreator> creators = new HashMap<>();
		InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_NSS_SERIALIZER::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof NSSCreatorInfo)
				.forEach(msg -> {
					NSSCreatorInfo creatorInfo = (NSSCreatorInfo) msg.getMessageSupplier().get();
					String key = creatorInfo.getKey();
					if (creators.containsKey(key)) {
						PECore.LOGGER.warn("Mod: '{}' tried to register NSS creator with key: '{}', but another mod already registered that key.", msg.getSenderModId(), key);
					} else {
						creators.put(key, creatorInfo.getCreator());
						PECore.debugLog("Mod: '{}' registered NSS creator with key: '{}'", msg.getSenderModId(), key);
					}
				});
		NSSSerializer.INSTANCE.setCreators(creators);
	}

	private static List<EntityType<? extends MobEntity>> getRandomizerEntities(boolean peaceful) {
		List<EntityType<? extends MobEntity>> entities = new ArrayList<>();
		String imcMethod = peaceful ? IMCMethods.ENTITY_RANDOMIZATION_PEACEFUL : IMCMethods.ENTITY_RANDOMIZATION_HOSTILE;
		String type = peaceful ? "peaceful" : "hostile";
		InterModComms.getMessages(PECore.MODID, imcMethod::equals)
				.filter(msg -> msg.getMessageSupplier().get() instanceof EntityType<?>)
				.forEach(msg -> {
					EntityType<?> genericEntityType = (EntityType<?>) msg.getMessageSupplier().get();
					EntityType<? extends MobEntity> entityType = EntityRandomizerHelper.getEntityIfMob(genericEntityType);
					if (entityType == null) {
						PECore.LOGGER.warn("Mod: '{}' tried to register a {} entity randomizer for the entity: '{}', but that entity is not a MobEntity.",
								msg.getSenderModId(), type, genericEntityType.getRegistryName());
					} else {
						entities.add(entityType);
						PECore.debugLog("Mod: '{}' registered a {} entity randomizer for the entity: '{}'", msg.getSenderModId(), type, entityType.getRegistryName());
					}
				});
		return entities;
	}
}