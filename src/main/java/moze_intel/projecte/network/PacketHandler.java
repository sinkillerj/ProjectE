package moze_intel.projecte.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.network.packets.*;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Map;

public final class PacketHandler
{
	private static final int MAX_PKT_SIZE = 256;
	private static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("projecte");
	
	public static void register()
	{
		int disc = 0;
		HANDLER.registerMessage(SyncEmcPKT.Handler.class, SyncEmcPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(KeyPressPKT.Handler.class, KeyPressPKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(StepHeightPKT.Handler.class, StepHeightPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(SetFlyPKT.Handler.class, SetFlyPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(KnowledgeSyncPKT.Handler.class, KnowledgeSyncPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(CheckUpdatePKT.Handler.class, CheckUpdatePKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(SyncBagDataPKT.Handler.class, SyncBagDataPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(SearchUpdatePKT.Handler.class, SearchUpdatePKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(KnowledgeClearPKT.Handler.class, KnowledgeClearPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(UpdateGemModePKT.Handler.class, UpdateGemModePKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(UpdateWindowIntPKT.Handler.class, UpdateWindowIntPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(CooldownResetPKT.Handler.class, CooldownResetPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(LeftClickArchangelPKT.Handler.class, LeftClickArchangelPKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(SyncCovalencePKT.Handler.class, SyncCovalencePKT.class, disc++, Side.CLIENT);
	}

	public static void sendProgressBarUpdateInt(IContainerListener listener, Container container, int propId, int propVal)
	{
		if (listener instanceof EntityPlayerMP)
		{
			sendTo(new UpdateWindowIntPKT((short) container.windowId, (short) propId, propVal), (EntityPlayerMP) listener);
		}
	}

	public static void sendFragmentedEmcPacket(EntityPlayerMP player)
	{
		ArrayList<Integer[]> list = Lists.newArrayList();
		int counter = 0;

		for (Map.Entry<SimpleStack, Integer> entry : Maps.newLinkedHashMap(EMCMapper.emc).entrySet()) // Copy constructor to prevent race condition CME in SP
		{
			SimpleStack stack = entry.getKey();

			if (stack == null)
			{
				continue;
			}

			int id = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(stack.id));

			Integer[] data = new Integer[] {id, stack.damage, entry.getValue()};
			list.add(data);

			if (list.size() >= MAX_PKT_SIZE)
			{
				PacketHandler.sendTo(new SyncEmcPKT(counter, list), player);
				list.clear();
				counter++;
			}
		}

		if (list.size() > 0)
		{
			PacketHandler.sendTo(new SyncEmcPKT(-1, list), player);
			list.clear();
			counter++;
		}

		PELogger.logInfo("Sent EMC data packets to: " + player.getName());
		PELogger.logDebug("Total packets: " + counter);
	}

	public static void sendFragmentedEmcPacketToAll()
	{
		ArrayList<Integer[]> list = Lists.newArrayList();
		int counter = 0;

		for (Map.Entry<SimpleStack, Integer> entry : Maps.newLinkedHashMap(EMCMapper.emc).entrySet()) // Copy constructor to prevent race condition CME in SP
		{
			SimpleStack stack = entry.getKey();

			if (stack == null)
			{
				continue;
			}

			int id = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(stack.id));

			Integer[] data = new Integer[] {id, stack.damage, entry.getValue()};
			list.add(data);

			if (list.size() >= MAX_PKT_SIZE)
			{
				PacketHandler.sendToAll(new SyncEmcPKT(counter, list));
				list.clear();
				counter++;
			}
		}

		if (list.size() > 0)
		{
			PacketHandler.sendToAll(new SyncEmcPKT(-1, list));
			list.clear();
			counter++;
		}

		PELogger.logInfo("Sent EMC data packets to all players.");
		PELogger.logDebug("Total packets per player: " + counter);
	}

	/**
	 * Sends a packet to the server.<br>
	 * Must be called Client side. 
	 */
	public static void sendToServer(IMessage msg)
	{
		HANDLER.sendToServer(msg);
	}
	
	/**
	 * Sends a packet to all the clients.<br>
	 * Must be called Server side.
	 */
	public static void sendToAll(IMessage msg)
	{
		HANDLER.sendToAll(msg);
	}
	
	/**
	 * Send a packet to all players around a specific point.<br>
	 * Must be called Server side. 
	 */
	public static void sendToAllAround(IMessage msg, TargetPoint point)
	{
		HANDLER.sendToAllAround(msg, point);
	}
	
	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side. 
	 */
	public static void sendTo(IMessage msg, EntityPlayerMP player)
	{
		if (!(player instanceof FakePlayer))
		{
			HANDLER.sendTo(msg, player);
		}
	}
	
	/**
	 * Send a packet to all the players in the specified dimension.<br>
	 *  Must be called Server side.
	 */
	public static void sendToDimension(IMessage msg, int dimension)
	{
		HANDLER.sendToDimension(msg, dimension);
	}
}
