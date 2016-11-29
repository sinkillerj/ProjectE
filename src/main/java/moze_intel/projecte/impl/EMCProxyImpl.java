package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import javax.annotation.Nonnull;

public class EMCProxyImpl implements IEMCProxy
{
    public static final IEMCProxy instance = new EMCProxyImpl();

    private EMCProxyImpl() {}

    @Override
    public void registerCustomEMC(@Nonnull ItemStack stack, int value)
    {
        Preconditions.checkNotNull(stack);
        boolean flag = Loader.instance().isInState(LoaderState.PREINITIALIZATION) || Loader.instance().isInState(LoaderState.INITIALIZATION) || Loader.instance().isInState(LoaderState.POSTINITIALIZATION);
        Preconditions.checkState(flag, String.format("Mod %s tried to register EMC at an invalid time!", Loader.instance().activeModContainer().getModId()));
        APICustomEMCMapper.instance.registerCustomEMC(stack, value);
        PELogger.logInfo("Mod %s registered emc value %d for itemstack %s", Loader.instance().activeModContainer().getModId(), value, stack.toString());
    }

    @Override
    public void registerCustomEMC(@Nonnull Object o, int value)
    {
        Preconditions.checkNotNull(o);
        boolean flag = Loader.instance().isInState(LoaderState.PREINITIALIZATION) || Loader.instance().isInState(LoaderState.INITIALIZATION) || Loader.instance().isInState(LoaderState.POSTINITIALIZATION);
        Preconditions.checkState(flag, String.format("Mod %s tried to register EMC at an invalid time!", Loader.instance().activeModContainer().getModId()));
        APICustomEMCMapper.instance.registerCustomEMC(o, value);
        PELogger.logInfo("Mod %s registered emc value %d for Object %s", Loader.instance().activeModContainer().getModId(), value, o);
    }

    @Override
    public boolean hasValue(@Nonnull Block block)
    {
        Preconditions.checkNotNull(block);
        return EMCHelper.doesBlockHaveEmc(block);
    }

    @Override
    public boolean hasValue(@Nonnull Item item)
    {
        Preconditions.checkNotNull(item);
        return EMCHelper.doesItemHaveEmc(item);
    }

    @Override
    public boolean hasValue(@Nonnull ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.doesItemHaveEmc(stack);
    }

    @Override
    public int getValue(@Nonnull Block block)
    {
        Preconditions.checkNotNull(block);
        return EMCHelper.getEmcValue(block);
    }

    @Override
    public int getValue(@Nonnull Item item)
    {
        Preconditions.checkNotNull(item);        
        return EMCHelper.getEmcValue(item);
    }

    @Override
    public int getValue(@Nonnull ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.getEmcValue(stack);
    }
}
