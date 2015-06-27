package moze_intel.projecte.proxies;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.UUID;

public class TransmutationProxyImpl implements ITransmutationProxy
{
    public static final ITransmutationProxy instance = new TransmutationProxyImpl();

    private TransmutationProxyImpl() {}

    @Override
    public boolean hasKnowledgeFor(UUID playerUUID, ItemStack stack)
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
    public void addKnowledge(UUID playerUUID, ItemStack stack)
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
    public void removeKnowledge(UUID playerUUID, ItemStack stack)
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
    public void setEMC(UUID playerUUID, double emc)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            Transmutation.setEmc(player, emc);
            Transmutation.sync(player);
        }
    }

    @Override
    public double getEMC(UUID playerUUID)
    {
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            return Transmutation.getEmc(player);
        }
        return Double.NaN;
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
