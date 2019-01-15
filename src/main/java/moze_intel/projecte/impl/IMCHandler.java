package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;

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
    }
}
