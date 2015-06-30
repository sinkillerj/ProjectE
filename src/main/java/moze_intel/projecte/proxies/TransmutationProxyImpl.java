package moze_intel.projecte.proxies;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.MetaBlock;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TransmutationProxyImpl implements ITransmutationProxy
{
    public static final ITransmutationProxy instance = new TransmutationProxyImpl();

    private TransmutationProxyImpl() {}

    @Override
    public boolean registerWorldTransmutation(@Nonnull Block origin, int originMeta, @Nonnull Block result1, int result1Meta, @Nullable Block result2, int result2meta)
    {
        boolean flag = Loader.instance().isInState(LoaderState.PREINITIALIZATION) || Loader.instance().isInState(LoaderState.INITIALIZATION) || Loader.instance().isInState(LoaderState.POSTINITIALIZATION);
        Preconditions.checkState(flag, String.format("Mod %s tried to register world transmutation at an invalid time!", Loader.instance().activeModContainer().getModId()));
        if (WorldTransmutations.getWorldTransmutation(new MetaBlock(origin, originMeta), false) != null)
        {
            return false;
        }
        else
        {
            WorldTransmutations.register(new MetaBlock(origin, originMeta), new MetaBlock(result1, result1Meta), result2 == null ? null : new MetaBlock(result2, result2meta));
            return true;
        }
    }

    @Override
    public boolean hasKnowledgeFor(@Nonnull UUID playerUUID, @Nonnull ItemStack stack)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            return Transmutation.hasKnowledgeForStack(stack, player);
        }
        else
        {
            // todo offline
            return false;
        }
    }

    @Override
    public void addKnowledge(@Nonnull UUID playerUUID, @Nonnull ItemStack stack)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            Transmutation.addKnowledge(stack, player);
            Transmutation.sync(player);
        }
    }

    @Override
    public void removeKnowledge(@Nonnull UUID playerUUID, @Nonnull ItemStack stack)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            Transmutation.removeKnowledge(stack, player);
            Transmutation.sync(player);
        }
    }

    @Override
    public void setEMC(@Nonnull UUID playerUUID, double emc)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify player EMC!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            Transmutation.setEmc(player, emc);
            Transmutation.sync(player);
        }
    }

    @Override
    public double getEMC(@Nonnull UUID playerUUID)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query player EMC!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            return Transmutation.getEmc(player);
        }
        else
        {
            // Offline -> No-op
            return Double.NaN;
        }
    }

    @SuppressWarnings("unchecked")
    private EntityPlayer findOnlinePlayer(UUID playerUUID)
    {
        for (EntityPlayer player : (List<EntityPlayer>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            if (player.getUniqueID().equals(playerUUID))
            {
                return player;
            }
        }
        return null;
    }
}
