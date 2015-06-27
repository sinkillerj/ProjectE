package moze_intel.projecte.proxies;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EMCProxyImpl implements IEMCProxy
{
    public static final IEMCProxy instance = new EMCProxyImpl();

    private EMCProxyImpl() {}

    @Override
    public void registerCustomEmc(ItemStack stack, int value)
    {
        boolean flag = Loader.instance().isInState(LoaderState.PREINITIALIZATION) || Loader.instance().isInState(LoaderState.INITIALIZATION) || Loader.instance().isInState(LoaderState.POSTINITIALIZATION);
        Preconditions.checkState(flag, String.format("Mod %s tried to register EMC at an invalid time!", Loader.instance().activeModContainer().getModId()));
        APICustomEMCMapper.instance.registerCustomEMC(stack, value);
        PELogger.logInfo(String.format("Mod %s registered emc value %d for itemstack %s", Loader.instance().activeModContainer().getModId(), value, stack.toString()));
    }

    @Override
    public boolean hasValue(Block block)
    {
        return EMCHelper.doesBlockHaveEmc(block);
    }

    @Override
    public boolean hasValue(Item item)
    {
        return EMCHelper.doesItemHaveEmc(item);
    }

    @Override
    public boolean hasValue(ItemStack stack)
    {
        return EMCHelper.doesItemHaveEmc(stack);
    }

    @Override
    public int getValue(Block block)
    {
        return EMCHelper.getEmcValue(block);
    }

    @Override
    public int getValue(Item item)
    {
        return EMCHelper.getEmcValue(item);
    }

    @Override
    public int getValue(ItemStack stack)
    {
        return EMCHelper.getEmcValue(stack);
    }
}
