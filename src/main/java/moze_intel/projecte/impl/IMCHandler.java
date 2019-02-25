package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.Locale;

// TODO 1.13 change to use te/entity ids instead of class names
public class IMCHandler
{
    public static void handleIMC(FMLInterModComms.IMCMessage msg)
    {
        String messageKey = msg.key.toLowerCase(Locale.ROOT);
        if ("interdictionblacklist".equals(messageKey) && msg.isStringMessage()) {
            blacklist(false, msg);
        } else if ("swrgblacklist".equals(messageKey) && msg.isStringMessage()) {
            blacklist(true, msg);
        } else if ("nbtwhitelist".equals(messageKey) && msg.isItemStackMessage()) {
            whitelistNBT(msg);
        } else if ("timewatchblacklist".equals(messageKey) && msg.isStringMessage()) {
            blacklistWatch(msg);
        } else {
            PECore.LOGGER.warn("Received unknown message \"{}\" from mod {}, ignoring.", messageKey, msg.getSender());
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
        if (!s.isEmpty())
        {
            ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doWhitelistNBT(s, msg.getSender());
        }
    }

    @SuppressWarnings("unchecked")
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
            PECore.LOGGER.warn("IMC tried to identify a class that couldn't be found: {}", name);
        }
        return null;
    }
}
