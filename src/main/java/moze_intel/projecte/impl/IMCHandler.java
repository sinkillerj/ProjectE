package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;

import java.util.Locale;

public class IMCHandler
{
    public static void handleMessages()
    {
        InterModComms.getMessages(PECore.MODID, "interdictionblacklist"::equals)
                .map(msg -> (EntityType) msg.getMessageSupplier().get())
                .forEach(BlacklistProxyImpl.instance::blacklistInterdiction);

        InterModComms.getMessages(PECore.MODID, "swrgblacklist"::equals)
                .map(msg -> (EntityType) msg.getMessageSupplier().get())
                .forEach(BlacklistProxyImpl.instance::blacklistSwiftwolf);

        InterModComms.getMessages(PECore.MODID, "timewatchblacklist"::equals)
                .map(msg -> (TileEntityType) msg.getMessageSupplier().get())
                .forEach(BlacklistProxyImpl.instance::blacklistTimeWatch);
    }
}
