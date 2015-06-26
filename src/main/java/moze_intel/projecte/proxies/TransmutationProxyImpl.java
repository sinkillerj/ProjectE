package moze_intel.projecte.proxies;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
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
        EntityPlayer player = findOnlinePlayer(playerUUID);
        if (player != null)
        {
            Transmutation.removeKnowledge(stack, player);
            Transmutation.sync(player);
        }
    }

    @SuppressWarnings("unchecked")
    private EntityPlayer findOnlinePlayer(UUID playerUUID)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            for (EntityPlayer player : (List<EntityPlayer>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
            {
                if (player.getGameProfile().getId().equals(playerUUID))
                {
                    return player;
                }
            }
        }
        return null;
    }
}
