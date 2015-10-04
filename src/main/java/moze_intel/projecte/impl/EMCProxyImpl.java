package moze_intel.projecte.impl;

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
    public void registerCustomEMC(ItemStack stack, int value)
    {
        Preconditions.checkNotNull(stack);
        boolean flag = Loader.instance().isInState(LoaderState.PREINITIALIZATION) || Loader.instance().isInState(LoaderState.INITIALIZATION) || Loader.instance().isInState(LoaderState.POSTINITIALIZATION);
        Preconditions.checkState(flag, String.format("Mod %s tried to register EMC at an invalid time!", Loader.instance().activeModContainer().getModId()));
        APICustomEMCMapper.instance.registerCustomEMC(stack, value);
        PELogger.logInfo("Mod %s registered emc value %d for itemstack %s", Loader.instance().activeModContainer().getModId(), value, stack.toString());
    }

    @Override
    public void registerCustomEMC(Object o, int value)
    {
        Preconditions.checkNotNull(o);
        boolean flag = Loader.instance().isInState(LoaderState.PREINITIALIZATION) || Loader.instance().isInState(LoaderState.INITIALIZATION) || Loader.instance().isInState(LoaderState.POSTINITIALIZATION);
        Preconditions.checkState(flag, String.format("Mod %s tried to register EMC at an invalid time!", Loader.instance().activeModContainer().getModId()));
        APICustomEMCMapper.instance.registerCustomEMC(o, value);
        PELogger.logInfo("Mod %s registered emc value %d for Object %s", Loader.instance().activeModContainer().getModId(), value, o);
    }

    @Override
    public boolean hasValue(Block block)
    {
        Preconditions.checkNotNull(block);
        return EMCHelper.hasEmcValueForCreation(new ItemStack(block));
    }

    @Override
    public boolean hasValue(Item item)
    {
        Preconditions.checkNotNull(item);
        return EMCHelper.hasEmcValueForCreation(new ItemStack(item));
    }

    @Override
    public boolean hasValue(ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return hasValue(stack.getItem());
    }

    @Override
    public int getValue(Block block)
    {
        Preconditions.checkNotNull(block);
        return EMCHelper.getEmcValueForCreation(new ItemStack(block));
    }

    @Override
    public int getValue(Item item)
    {
        Preconditions.checkNotNull(item);        
        return EMCHelper.getEmcValueForCreation(new ItemStack(item));
    }

    @Override
    public int getValue(ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.getEmcValueForCreation(stack);
    }
}
