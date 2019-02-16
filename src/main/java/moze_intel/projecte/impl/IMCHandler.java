package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.CustomConversionRegistration;
import moze_intel.projecte.api.imc.CustomEMCRegistration;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import moze_intel.projecte.emc.mappers.APICustomConversionMapper;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class IMCHandler
{
    public static void handleMessages()
    {
        Set<EntityType<?>> interd = InterModComms.getMessages(PECore.MODID, "blacklist_interdiction"::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof EntityType)
                .map(msg -> (EntityType<?>) msg.getMessageSupplier().get())
                .collect(Collectors.toSet());
        WorldHelper.setInterdictionBlacklist(interd);

        Set<EntityType<?>> swrg = InterModComms.getMessages(PECore.MODID, "blacklist_swrg"::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof EntityType)
                .map(msg -> (EntityType<?>) msg.getMessageSupplier().get())
                .collect(Collectors.toSet());
        WorldHelper.setSwrgBlacklist(swrg);

        Set<TileEntityType<?>> timeWatch = InterModComms.getMessages(PECore.MODID, "blacklist_timewatch"::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof TileEntityType)
                .map(msg -> (TileEntityType<?>) msg.getMessageSupplier().get())
                .collect(Collectors.toSet());
        TimeWatch.setInternalBlacklist(timeWatch);

        List<WorldTransmutationEntry> entries = InterModComms.getMessages(PECore.MODID, "register_world_transmutation"::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof WorldTransmutationEntry)
                .map(msg -> (WorldTransmutationEntry) msg.getMessageSupplier().get())
                .collect(Collectors.toList());
        WorldTransmutations.setWorldTransmutation(entries);

        InterModComms.getMessages(PECore.MODID, "register_custom_emc"::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof CustomEMCRegistration)
                .map(msg -> (CustomEMCRegistration) msg.getMessageSupplier().get())
                .forEach(r -> APICustomEMCMapper.instance.registerCustomEMC("unknown", r.getThing(), r.getValue()));
        // todo 1.13 can we get sender?

        InterModComms.getMessages(PECore.MODID, "register_custom_conversion"::equals)
                .filter(msg -> msg.getMessageSupplier().get() instanceof CustomConversionRegistration)
                .map(msg -> (CustomConversionRegistration) msg.getMessageSupplier().get())
                .forEach(r -> APICustomConversionMapper.instance.addConversion("unknown", r.getAmount(), r.getOutput(), r.getInput()));
        // todo 1.13 can we get sender?
    }
}
