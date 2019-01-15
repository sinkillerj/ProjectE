package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EMCProxyImpl implements IEMCProxy
{
    public static final EMCProxyImpl instance = new EMCProxyImpl();
    private final List<Triple<String, Object, Long>> customEmcStaging = Collections.synchronizedList(new ArrayList<>());

    private EMCProxyImpl() {}

    @Override
    public void registerCustomEMC(@Nonnull ItemStack stack, long value)
    {
        Preconditions.checkNotNull(stack);
        if (stack.isEmpty())
        {
            return;
        }
        registerCustomEMC((Object) stack.copy(), value);
    }

    @Override
    public void registerCustomEMC(@Nonnull Object o, long value)
    {
        Preconditions.checkNotNull(o);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        customEmcStaging.add(Triple.of(modid, o, value));
        PECore.debugLog("Mod {} registered emc value {} for {}", modid, value, o);
    }

    @Override
    public boolean hasValue(@Nonnull Block block)
    {
        Preconditions.checkNotNull(block);
        return EMCHelper.doesItemHaveEmc(block);
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
    public long getValue(@Nonnull Block block)
    {
        Preconditions.checkNotNull(block);
        return EMCHelper.getEmcValue(block);
    }

    @Override
    public long getValue(@Nonnull Item item)
    {
        Preconditions.checkNotNull(item);        
        return EMCHelper.getEmcValue(item);
    }

    @Override
    public long getValue(@Nonnull ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        return EMCHelper.getEmcValue(stack);
    }

    public List<Triple<String, Object, Long>> getCustomEmcStaging()
    {
        return customEmcStaging;
    }
}
