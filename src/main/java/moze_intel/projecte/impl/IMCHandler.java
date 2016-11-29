package moze_intel.projecte.impl;

import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.Locale;

public class IMCHandler
{
    public static void handleIMC(FMLInterModComms.IMCMessage msg)
    {
        String messageKey = msg.key.toLowerCase(Locale.ROOT);
        if ("registeremc".equals(messageKey)) {
            PELogger.logWarn("Mod %s is using a deprecated version of the ProjectE API, their EMC registrations have been ignored", msg.getSender());
        } else if ("interdictionblacklist".equals(messageKey) && msg.isStringMessage()) {
            blacklist(false, msg);
        } else if ("swrgblacklist".equals(messageKey) && msg.isStringMessage()) {
            blacklist(true, msg);
        } else if ("nbtwhitelist".equals(messageKey) && msg.isItemStackMessage()) {
            whitelistNBT(msg);
        } else if ("timewatchblacklist".equals(messageKey) && msg.isStringMessage()) {
            blacklistWatch(msg);
        } else {
            PELogger.logWarn("Received unknown message \"%s\" from mod %s, ignoring.", messageKey, msg.getSender());
        }
    }

    private static void blacklist(boolean isSWRG, FMLInterModComms.IMCMessage msg)
    {
        Class<? extends Entity> clazz = loadAndCheckSubclass(msg.getStringValue(), Entity.class);
        if (clazz != null)
        {
            if (isSWRG)
            {
                ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doBlacklistSwiftwolf(clazz, msg.getSender());
            }
            else
            {
                ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doBlacklistInterdiction(clazz, msg.getSender());
            }
        }

    }

    private static void blacklistWatch(FMLInterModComms.IMCMessage msg)
    {
        Class<? extends TileEntity> clazz = loadAndCheckSubclass(msg.getStringValue(), TileEntity.class);
        if (clazz != null)
        {
            ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doBlacklistTimewatch(clazz, msg.getSender());
        }
    }

    private static void whitelistNBT(FMLInterModComms.IMCMessage msg)
    {
        ItemStack s = msg.getItemStackValue();
        if (s != null)
        {
            ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doWhitelistNBT(s, msg.getSender());
        }
    }

    private static <T, U extends T> Class<U> loadAndCheckSubclass(String name, Class<T> toCheck)
    {
        try
        {
            Class<?> clazz = Class.forName(name);
            if (toCheck.isAssignableFrom(clazz))
            {
                return (Class<U>) clazz;
            }
        } catch (ClassNotFoundException ex) {
            PELogger.logWarn("IMC tried to identify a class that couldn't be found: %s", name);
        }
        return null;
    }
}
