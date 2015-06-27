package moze_intel.projecte.proxies;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import moze_intel.projecte.api.proxy.IExtraProxy;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ExtraProxyImpl implements IExtraProxy
{
    public static final IExtraProxy instance = new ExtraProxyImpl();

    private ExtraProxyImpl() {}

    @Override
    public void registerInterdictionBlacklist(Class<? extends Entity> clazz)
    {
        Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Mod %s registering interdiction blacklist at incorrect time!", Loader.instance().activeModContainer().getModId());
        WorldHelper.blacklistInterdiction(clazz);
        PELogger.logInfo(String.format("Mod %s blacklisted entity %s for interdiction torch.", Loader.instance().activeModContainer().getModId(), clazz.getName()));
    }

    @Override
    public void whitelistNBT(ItemStack stack)
    {
        Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Mod %s registering NBT whitelist at incorrect time!", Loader.instance().activeModContainer().getModId());
        NBTWhitelist.register(stack);
        PELogger.logInfo(String.format("Mod %s whitelisted stack %s for NBT duplication.", Loader.instance().activeModContainer().getModId(), stack.toString()));
    }
}
