package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlacklistProxyImpl implements IBlacklistProxy
{
    public static final BlacklistProxyImpl instance = new BlacklistProxyImpl();
    private final Set<EntityType<?>> interdictionBlacklistStaging = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<EntityType<?>> swrgBlacklistStaging = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<TileEntityType<?>> timeWatchBlacklistStaging = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private BlacklistProxyImpl() {}

    @Override
    public void blacklistInterdiction(@Nonnull EntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        interdictionBlacklistStaging.add(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        ResourceLocation id = type.getRegistryName();
        PECore.debugLog("Mod {} blacklisted {} for interdiction torch", modid, id);
    }

    @Override
    public void blacklistSwiftwolf(@Nonnull EntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        swrgBlacklistStaging.add(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        ResourceLocation id = type.getRegistryName();
        PECore.debugLog("Mod {} blacklisted {} for SWRG repel", modid, id);
    }

    @Override
    public void blacklistTimeWatch(@Nonnull TileEntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        timeWatchBlacklistStaging.add(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        ResourceLocation id = type.getRegistryName();
        PECore.debugLog("Mod {} blacklisted {} for Time Watch acceleration", modid, id);
    }

    public Set<EntityType<?>> getInterdictionBlacklist()
    {
        return interdictionBlacklistStaging;
    }

    public Set<EntityType<?>> getSwrgBlacklist()
    {
        return swrgBlacklistStaging;
    }

    public Set<TileEntityType<?>> getTimeWatchBlacklist()
    {
        return timeWatchBlacklistStaging;
    }

}
