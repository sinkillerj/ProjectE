package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import javax.annotation.Nonnull;

public class BlacklistProxyImpl implements IBlacklistProxy
{
    public static final IBlacklistProxy instance = new BlacklistProxyImpl();

    private BlacklistProxyImpl() {}

    @Override
    public void blacklistInterdiction(@Nonnull Class<? extends Entity> clazz)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering interdiction blacklist at incorrect time!", Loader.instance().activeModContainer().getModId());
        doBlacklistInterdiction(clazz, Loader.instance().activeModContainer().getModId());
    }

    @Override
    public void blacklistSwiftwolf(@Nonnull Class<? extends Entity> clazz)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering SWRG repel at incorrect time!", Loader.instance().activeModContainer().getModId());
        doBlacklistSwiftwolf(clazz, Loader.instance().activeModContainer().getModId());
    }

    @Override
    public void blacklistTimeWatch(@Nonnull Class<? extends TileEntity> clazz)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering TimeWatch blacklist at incorrect time!", Loader.instance().activeModContainer().getModId());
        doBlacklistTimewatch(clazz, Loader.instance().activeModContainer().getModId());
    }

    @Override
    public void whitelistNBT(@Nonnull ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering NBT whitelist at incorrect time!", Loader.instance().activeModContainer().getModId());
        doWhitelistNBT(stack, Loader.instance().activeModContainer().getModId());
    }

    /**
     * Split actual doing of whitelisting/blacklisting apart in order to log it properly from IMC
     */

    protected void doBlacklistInterdiction(Class<? extends Entity> clazz, String modName)
    {
        WorldHelper.blacklistInterdiction(clazz);
        PECore.debugLog("Mod {} blacklisted {} for interdiction torch", modName, clazz.getCanonicalName());
    }

    protected void doBlacklistSwiftwolf(Class<? extends Entity> clazz, String modName)
    {
        WorldHelper.blacklistSwrg(clazz);
        PECore.debugLog("Mod {} blacklisted {} for SWRG repel", modName, clazz.getCanonicalName());
    }

    protected void doBlacklistTimewatch(Class<? extends TileEntity> clazz, String modName)
    {
        TimeWatch.blacklist(clazz);
        PECore.debugLog("Mod {} blacklisted {} for Time Watch acceleration", modName, clazz.getCanonicalName());
    }

    protected void doWhitelistNBT(ItemStack s, String modName)
    {
        NBTWhitelist.register(s);
        PECore.debugLog("Mod {} whitelisted {} for NBT duping", modName, s.toString());
    }
}
