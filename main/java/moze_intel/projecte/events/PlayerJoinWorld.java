package moze_intel.projecte.events;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.BlockDirection;
import moze_intel.projecte.gameObjs.tiles.TileEmcDirection;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientCheckUpdatePKT;
import moze_intel.projecte.network.packets.ClientSyncPKT;
import moze_intel.projecte.playerData.AlchemicalBagData;
import moze_intel.projecte.playerData.TransmutationKnowledge;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class PlayerJoinWorld
{
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			TransmutationKnowledge.sync((EntityPlayer) event.entity);
			AlchemicalBagData.sync((EntityPlayer) event.entity);
		}
	}
}
