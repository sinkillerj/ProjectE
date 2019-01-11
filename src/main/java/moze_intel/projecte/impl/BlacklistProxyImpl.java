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

public class BlacklistProxyImpl implements IBlacklistProxy
{
    public static final IBlacklistProxy instance = new BlacklistProxyImpl();

    private BlacklistProxyImpl() {}

    @Override
    public void blacklistInterdiction(@Nonnull EntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        ResourceLocation id = type.getRegistryName();
        WorldHelper.blacklistInterdiction(id);
        PECore.debugLog("Mod {} blacklisted {} for interdiction torch", modid, id);
    }

    @Override
    public void blacklistSwiftwolf(@Nonnull EntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        ResourceLocation id = type.getRegistryName();
        WorldHelper.blacklistSwrg(id);
        PECore.debugLog("Mod {} blacklisted {} for SWRG repel", modid, id);
    }

    @Override
    public void blacklistTimeWatch(@Nonnull TileEntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        ResourceLocation id = type.getRegistryName();
        TimeWatch.blacklist(id);
        PECore.debugLog("Mod {} blacklisted {} for Time Watch acceleration", modid, id);
    }

}
