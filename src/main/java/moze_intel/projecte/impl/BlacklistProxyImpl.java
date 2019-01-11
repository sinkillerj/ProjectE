package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.utils.NBTWhitelist;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingStage;
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
        doBlacklistInterdiction(type.getRegistryName(), modid);
    }

    @Override
    public void blacklistSwiftwolf(@Nonnull EntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        doBlacklistSwiftwolf(type.getRegistryName(), modid);
    }

    @Override
    public void blacklistTimeWatch(@Nonnull TileEntityType<?> type)
    {
        Preconditions.checkNotNull(type);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        doBlacklistTimewatch(type.getRegistryName(), modid);
    }

    @Override
    public void whitelistNBT(@Nonnull ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        String modid = FMLModLoadingContext.get().getActiveContainer().getModId();
        doWhitelistNBT(stack, modid);
    }

    /**
     * Split actual doing of whitelisting/blacklisting apart in order to log it properly from IMC
     */

    protected void doBlacklistInterdiction(ResourceLocation id, String modName)
    {
        WorldHelper.blacklistInterdiction(id);
        PECore.debugLog("Mod {} blacklisted {} for interdiction torch", modName, id);
    }

    protected void doBlacklistSwiftwolf(ResourceLocation id, String modName)
    {
        WorldHelper.blacklistSwrg(id);
        PECore.debugLog("Mod {} blacklisted {} for SWRG repel", modName, id);
    }

    protected void doBlacklistTimewatch(ResourceLocation id, String modName)
    {
        TimeWatch.blacklist(id);
        PECore.debugLog("Mod {} blacklisted {} for Time Watch acceleration", modName, id);
    }

    protected void doWhitelistNBT(ItemStack s, String modName)
    {
        NBTWhitelist.register(s);
        PECore.debugLog("Mod {} whitelisted {} for NBT duping", modName, s.toString());
    }
}
