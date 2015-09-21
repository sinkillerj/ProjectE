package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

import moze_intel.projecte.api.exception.NoCreationEmcValueException;
import moze_intel.projecte.api.exception.NoDestructionEmcValueException;
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

    @Deprecated
    @Override
    public boolean hasValue(Block block)
    {
        Preconditions.checkNotNull(block);
        return hasValue(new ItemStack(block));
    }

    @Deprecated
    @Override
    public boolean hasValue(Item item)
    {
        Preconditions.checkNotNull(item);
        return hasValue(new ItemStack(item));
    }

    @Deprecated
    @Override
    public boolean hasValue(ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.hasEmcValueForDestruction(stack);
    }

    @Deprecated
    @Override
    public int getValue(Block block)
    {
        Preconditions.checkNotNull(block);
        return getValue(new ItemStack(block));
    }

    @Deprecated
    @Override
    public int getValue(Item item)
    {
        Preconditions.checkNotNull(item);        
        return getValue(new ItemStack(item));
    }

    @Deprecated
    @Override
    public int getValue(ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        if (!hasValue(stack)) return 0;
        return EMCHelper.getEmcValueForDestructionWithDamageAndBonuses(stack);
    }

    @Override
    public boolean canBeCreatedWithEmc(ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.hasEmcValueForCreation(stack);
    }

    @Override
    public int getCreationEmcCost(ItemStack stack) throws NoCreationEmcValueException
    {
        if (!EMCHelper.hasEmcValueForCreation(stack))
        {
            throw new NoCreationEmcValueException();
        }
        return EMCHelper.getEmcValueForCreation(stack);
    }

    @Override
    public boolean canBeTurnedIntoEmc(ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.hasEmcValueForDestruction(stack);
    }

    @Override
    public int getDestructionEmc(ItemStack stack) throws NoDestructionEmcValueException
    {
        if (!EMCHelper.hasEmcValueForDestruction(stack))
        {
            throw new NoDestructionEmcValueException();
        }
        return EMCHelper.getEmcValueForDestructionWithDamageAndBonuses(stack);
    }
}
