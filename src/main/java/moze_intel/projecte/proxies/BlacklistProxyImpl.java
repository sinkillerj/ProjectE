package moze_intel.projecte.proxies;

import com.google.common.base.Preconditions;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.WorldHelper;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class BlacklistProxyImpl implements IBlacklistProxy
{
    public static final IBlacklistProxy instance = new BlacklistProxyImpl();

    private BlacklistProxyImpl() {}

    @Override
    public void blacklistInterdiction(@Nonnull Class<? extends Entity> clazz)
    {
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering interdiction blacklist at incorrect time!", Loader.instance().activeModContainer().getModId());
        WorldHelper.blacklistInterdiction(clazz);
        PELogger.logInfo(String.format("Mod %s blacklisted entity %s for interdiction torch.", Loader.instance().activeModContainer().getModId(), clazz.getName()));
    }

    @Override
    public void blacklistTimeWatch(@Nonnull Class<? extends TileEntity> clazz)
    {
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering TimeWatch blacklist at incorrect time!", Loader.instance().activeModContainer().getModId());
        TimeWatch.blacklistPublic(clazz.getName());
    }

    @Override
    public void whitelistNBT(@Nonnull ItemStack stack)
    {
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), "Mod %s registering NBT whitelist at incorrect time!", Loader.instance().activeModContainer().getModId());
        NBTWhitelist.register(stack);
        PELogger.logInfo(String.format("Mod %s whitelisted stack %s for NBT duplication.", Loader.instance().activeModContainer().getModId(), stack.toString()));
    }
}
