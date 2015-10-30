package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.relauncher.Side;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.playerData.TransmutationOffline;
import moze_intel.projecte.utils.MetaBlock;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TransmutationProxyImpl implements ITransmutationProxy
{
    public static final ITransmutationProxy instance = new TransmutationProxyImpl();

    private TransmutationProxyImpl() {}

    @Override
    public boolean registerWorldTransmutation(Block origin, int originMeta, Block result1, int result1Meta, @Nullable Block result2, int result2meta)
    {
        Preconditions.checkNotNull(origin);
        Preconditions.checkNotNull(result1);
        Preconditions.checkState(Loader.instance().isInState(LoaderState.POSTINITIALIZATION), String.format("Mod %s tried to register world transmutation at an invalid time!", Loader.instance().activeModContainer().getModId()));
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
    public boolean hasKnowledgeFor(UUID playerUUID, ItemStack stack)
    {
        Preconditions.checkNotNull(stack);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            Preconditions.checkState(PECore.proxy.getClientPlayer() != null, "Client player doesn't exist!");
            return Transmutation.hasKnowledgeForStack(stack, PECore.proxy.getClientPlayer());
        }
        else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query knowledge!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return Transmutation.hasKnowledgeForStack(stack, player);
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
            return Transmutation.getKnowledge(PECore.proxy.getClientPlayer());
        }
        else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query knowledge!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return Transmutation.getKnowledge(player);
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
            Transmutation.addKnowledge(stack, player);
            Transmutation.sync(player);
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
            Transmutation.removeKnowledge(stack, player);
            Transmutation.sync(player);
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
            Transmutation.setEmc(player, emc);
            Transmutation.sync(player);
        }
    }

    @Override
    public double getEMC(UUID playerUUID)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            Preconditions.checkState(PECore.proxy.getClientPlayer() != null, "Client player doesn't exist!");
            return Transmutation.getEmc(PECore.proxy.getClientPlayer());
        } else
        {
            Preconditions.checkNotNull(playerUUID);
            Preconditions.checkState(Loader.instance().hasReachedState(LoaderState.SERVER_STARTED), "Server must be running to query player EMC!");
            EntityPlayer player = findOnlinePlayer(playerUUID);
            if (player != null)
            {
                return Transmutation.getEmc(player);
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
