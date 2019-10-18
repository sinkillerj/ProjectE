package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.CustomConversionRegistration;
import moze_intel.projecte.api.imc.CustomEMCRegistration;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import moze_intel.projecte.emc.mappers.APICustomConversionMapper;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.InterModComms;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IMCHandler
{
    public static void handleMessages()
    {
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

        List<WorldTransmutationEntry> entries = InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_WORLD_TRANSMUTATION::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof WorldTransmutationEntry)
                .map(msg -> (WorldTransmutationEntry) msg.getMessageSupplier().get())
                .collect(Collectors.toList());
        WorldTransmutations.setWorldTransmutation(entries);

        InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_CUSTOM_EMC::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof CustomEMCRegistration)
                .forEach(msg -> {
                    CustomEMCRegistration registration = (CustomEMCRegistration) msg.getMessageSupplier().get();
                    APICustomEMCMapper.instance.registerCustomEMC(msg.getSenderModId(), registration.getThing(), registration.getValue());
                });

        InterModComms.getMessages(PECore.MODID, IMCMethods.REGISTER_CUSTOM_CONVERSION::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof CustomConversionRegistration)
                .forEach(msg -> {
                    CustomConversionRegistration registration = (CustomConversionRegistration) msg.getMessageSupplier().get();
                    APICustomConversionMapper.instance.addConversion(msg.getSenderModId(), registration.getAmount(), registration.getOutput(), registration.getInput());
                });
    }
}
