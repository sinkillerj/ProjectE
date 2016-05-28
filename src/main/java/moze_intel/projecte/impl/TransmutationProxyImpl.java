package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.playerData.TransmutationOffline;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.UUID;

public class TransmutationProxyImpl implements ITransmutationProxy
{
    public static final ITransmutationProxy instance = new TransmutationProxyImpl();

    private TransmutationProxyImpl() {}

    @Override
    public boolean registerWorldTransmutation(IBlockState origin, IBlockState result1, IBlockState result2)
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

    @Override
    public boolean hasKnowledgeFor(UUID playerUUID, ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            Preconditions.checkState(PECore.proxy.getClientPlayer() != null, "Client player doesn't exist!");
            return PECore.proxy.getClientTransmutationProps().hasKnowledge(stack);
        }
        else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query knowledge!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).hasKnowledge(stack);
            }
            else
            {
                return TransmutationOffline.hasKnowledgeForStack(stack, playerUUID);
            }
        }
    }
    
    @Override
	public List<ItemStack> getKnowledge(UUID playerUUID)
    {
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            Preconditions.checkState(PECore.proxy.getClientPlayer() != null, "Client player doesn't exist!");
            return PECore.proxy.getClientTransmutationProps().getKnowledge();
        }
        else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query knowledge!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).getKnowledge();
            }
            else
            {
                return TransmutationOffline.getKnowledge(playerUUID);
            }
        }
	}

    @Override
    public boolean hasFullKnowledge(UUID playerUUID)
    {
        return false;
    }

    @Override
    public void addKnowledge(UUID playerUUID, ItemStack stack)
    {
        Preconditions.checkNotNull(playerUUID);
        Preconditions.checkNotNull(stack);
        Preconditions.checkState(FMLCommonHandler.instance().getEffectiveSide().isServer(), "Cannot modify knowledge clientside!");
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).addKnowledge(stack);
            player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).sync(((EntityPlayerMP) player));
        }
    }

    @Override
    public void removeKnowledge(UUID playerUUID, ItemStack stack)
    {
        Preconditions.checkNotNull(playerUUID);
        Preconditions.checkNotNull(stack);
        Preconditions.checkState(FMLCommonHandler.instance().getEffectiveSide().isServer(), "Cannot modify knowledge clientside!");
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify knowledge!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).removeKnowledge(stack);
            player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).sync(((EntityPlayerMP) player));
        }
    }

    @Override
    public void setEMC(UUID playerUUID, double emc)
    {
        Preconditions.checkNotNull(playerUUID);
        Preconditions.checkState(FMLCommonHandler.instance().getEffectiveSide().isServer(), "Cannot modify EMC clientside!");
        Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to modify player EMC!");
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).setEmc(emc);
            player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).sync(((EntityPlayerMP) player));
        }
    }

    @Override
    public double getEMC(UUID playerUUID)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            Preconditions.checkState(PECore.proxy.getClientPlayer() != null, "Client player doesn't exist!");
            return PECore.proxy.getClientTransmutationProps().getEmc();
        } else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query player EMC!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).getEmc();
            }
            else
            {
                return TransmutationOffline.getEmc(playerUUID);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private EntityPlayer findOnlinePlayer(UUID playerUUID)
    {
        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList())
        {
            if (player.getUniqueID().equals(playerUUID))
            {
                return player;
            }
        }
        return null;
    }
}
