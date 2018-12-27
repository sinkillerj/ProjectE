package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.InterModComms;

import java.util.Locale;

// TODO 1.13 change to use te/entity ids instead of class names
public class IMCHandler
{
    public static void handleIMC(InterModComms.IMCMessage msg)
    {
        String messageKey = msg.getMethod().toLowerCase(Locale.ROOT);
        Object thing = msg.getMessageSupplier().get();
        if ("interdictionblacklist".equals(messageKey) && thing instanceof String) {
            blacklist(false, (String) thing);
        } else if ("swrgblacklist".equals(messageKey) && thing instanceof String) {
            blacklist(true, (String) thing);
        } else if ("nbtwhitelist".equals(messageKey) && thing instanceof ItemStack) {
            whitelistNBT((ItemStack) thing);
        } else if ("timewatchblacklist".equals(messageKey) && thing instanceof String) {
            blacklistWatch((String) thing);
        } else {
            // TODO sender
            PECore.LOGGER.warn("Received unknown message \"{}\" from mod {}, ignoring.", messageKey, "unknown");
        }
    }

    private static void blacklist(boolean isSWRG, String msg)
    {
        Class<? extends Entity> clazz = loadAndCheckSubclass(msg, Entity.class);
        if (clazz != null)
        {
            // TODO 1.13 sender
            if (isSWRG)
            {
                ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doBlacklistSwiftwolf(clazz, "unknown");
            }
            else
            {
                ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doBlacklistInterdiction(clazz, "unknown");
            }
        }

    }

    private static void blacklistWatch(String msg)
    {
        Class<? extends TileEntity> clazz = loadAndCheckSubclass(msg, TileEntity.class);
        if (clazz != null)
        {
            // TODO 1.13 sender
            ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doBlacklistTimewatch(clazz, "unknown");
        }
    }

    private static void whitelistNBT(ItemStack msg)
    {
        if (!msg.isEmpty())
        {
            // TODO 1.13 move to tag
            // ((BlacklistProxyImpl) BlacklistProxyImpl.instance).doWhitelistNBT(s, msg.getSender());
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
