package moze_intel.projecte.handlers;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class IMCHandler
{
    @SubscribeEvent
    public void handle(FMLInterModComms.IMCEvent event)
    {
        for (FMLInterModComms.IMCMessage msg : event.getMessages())
        {
            String messageKey = msg.key.toLowerCase();
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
                PELogger.logWarn("Received unknown message \"%s\" from mod %s", messageKey, msg.getSender());
            }
        }
    }

    private void blacklist(boolean isSWRG, FMLInterModComms.IMCMessage msg)
    {
        Class<? extends Entity> clazz = loadAndCheckSubclass(msg.getStringValue(), Entity.class);
        if (clazz != null)
        {
            if (isSWRG)
            {
                ProjectEAPI.getBlacklistProxy().blacklistSwiftwolf(clazz);
            }
            else
            {
                ProjectEAPI.getBlacklistProxy().blacklistInterdiction(clazz);
            }
            PELogger.logInfo("Mod %s blacklisted class %s for %s", msg.getSender(), clazz.getCanonicalName(), isSWRG ? "SWRG" : "interdiction torch");
        }

    }

    private void blacklistWatch(FMLInterModComms.IMCMessage msg)
    {
        Class<? extends TileEntity> clazz = loadAndCheckSubclass(msg.getStringValue(), TileEntity.class);
        if (clazz != null)
        {
            ProjectEAPI.getBlacklistProxy().blacklistTimeWatch(clazz);
            PELogger.logInfo("Mod %s blacklisted tile entity %s for Watch of Flowing Time");
        }
    }

    private void whitelistNBT(FMLInterModComms.IMCMessage msg)
    {
        ItemStack s = msg.getItemStackValue();
        if (s == null)
        {
            PELogger.logWarn("Mod %s sent a null stack for nbt whitelist!", msg.getSender());
            return;
        }

        ProjectEAPI.getBlacklistProxy().whitelistNBT(s);
    }

    private <T> Class<? extends T> loadAndCheckSubclass(String name, Class<T> toCheck)
    {
        try
        {
            Class<?> clazz = Class.forName(name);
            if (toCheck.isAssignableFrom(clazz))
            {
                return (Class<? extends T>) clazz;
            }
        } catch (ClassNotFoundException ex) {
            PELogger.logWarn("IMC tried to identify a class that couldn't be found: %s", name);
        }
        return null;
    }
}
