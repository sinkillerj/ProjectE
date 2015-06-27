package moze_intel.projecte.proxies;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.Loader;
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
        APICustomEMCMapper.instance.registerCustomEMC(stack, value);
        PELogger.logInfo(String.format("Mod %s registered emc value %d for itemstack %s", Loader.instance().activeModContainer().getModId(), value, stack.toString()));
    }

    @Override
    public boolean hasValue(Object obj)
    {
        Preconditions.checkArgument(obj instanceof Block || obj instanceof Item || obj instanceof ItemStack, "What are you giving me?! >:(");
        if (obj instanceof Item)
        {
            return EMCHelper.doesItemHaveEmc(((Item) obj));
        } else if (obj instanceof Block)
        {
            return EMCHelper.doesBlockHaveEmc(((Block) obj));
        } else if (obj instanceof ItemStack)
        {
            return EMCHelper.doesItemHaveEmc(((ItemStack) obj));
        }
        return false;
    }

    @Override
    public int getValue(Object obj)
    {
        Preconditions.checkArgument(obj instanceof Block || obj instanceof Item || obj instanceof ItemStack, "What are you giving me?! >:(");
        if (obj instanceof Item)
        {
            return EMCHelper.getEmcValue(((Item) obj));
        } else if (obj instanceof Block)
        {
            return EMCHelper.getEmcValue(((Block) obj));
        } else if (obj instanceof ItemStack)
        {
            return EMCHelper.getEmcValue(((ItemStack) obj));
        }
        return 0;
    }
}
