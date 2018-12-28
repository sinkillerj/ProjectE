package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TransmutationProxyImpl implements ITransmutationProxy
{
    public static final ITransmutationProxy instance = new TransmutationProxyImpl();

    private TransmutationProxyImpl() {}

    @Override
    public boolean registerWorldTransmutation(@Nonnull IBlockState origin, @Nonnull IBlockState result1, IBlockState result2)
    {
        Preconditions.checkNotNull(origin);
        Preconditions.checkNotNull(result1);
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), String.format("Mod %s tried to register world transmutation at an invalid time!", Loader.instance().activeModContainer().getModId()));
        if (WorldTransmutations.getWorldTransmutation(origin, false) != null)
        {
            return false;
        }
        else
        {
            WorldTransmutations.register(origin, result1, result2);
            return true;
        }
    }

    @Nonnull
    @Override
    public IKnowledgeProvider getKnowledgeProviderFor(@Nonnull UUID playerUUID)
    {
        if (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER)
        {
            Preconditions.checkState(PECore.proxy.getClientPlayer() != null, "Client player doesn't exist!");
            return PECore.proxy.getClientTransmutationProps();
        }
        else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkNotNull(ServerLifecycleHooks.getCurrentServer(), "Server must be running to query knowledge!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).orElseThrow(NullPointerException::new);
            }
            else
            {
                return TransmutationOffline.forPlayer(playerUUID);
            }
        }
    }

    private EntityPlayer findOnlinePlayer(UUID playerUUID)
    {
        for (EntityPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            if (player.getUniqueID().equals(playerUUID))
            {
                return player;
            }
        }
        return null;
    }
}
