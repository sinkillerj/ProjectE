package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.impl.KnowledgeImpl;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.CheckUpdatePKT;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class PlayerEvents
{
	// Handles playerData props from being wiped on death
	@SubscribeEvent
	public void cloneEvent(PlayerEvent.Clone evt)
	{
		NBTTagCompound bags = evt.getOriginal().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null).serializeNBT();
		evt.getEntityPlayer().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null).deserializeNBT(bags);

		NBTTagCompound knowledge = evt.getOriginal().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).serializeNBT();
		evt.getEntityPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).deserializeNBT(knowledge);

		PELogger.logDebug("Reapplied bag and knowledge on player respawning");
	}

	@SubscribeEvent
	public void attachCaps(AttachCapabilitiesEvent.Entity evt)
	{
		if (evt.getEntity() instanceof EntityPlayer)
		{
			evt.addCapability(AlchBagImpl.Provider.NAME, new AlchBagImpl.Provider());
			evt.addCapability(KnowledgeImpl.Provider.NAME, new KnowledgeImpl.Provider());

			if (evt.getEntity() instanceof EntityPlayerMP)
			{
				evt.addCapability(InternalTimers.NAME, new InternalTimers.Provider());
				evt.addCapability(InternalAbilities.NAME, new InternalAbilities.Provider((EntityPlayerMP) evt.getEntity()));
			}
		}
	}

	@SubscribeEvent
	public void playerConnect(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event)
	{
		EntityPlayerMP player = (EntityPlayerMP) event.player;

		PacketHandler.sendFragmentedEmcPacket(player);
		PacketHandler.sendTo(new CheckUpdatePKT(), player);

		player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).sync(player);
		player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null).sync(null, player);
		PELogger.logInfo("Sent knowledge and bag data to %s", player.getName());
	}

	@SubscribeEvent
	public void onConstruct(EntityEvent.EntityConstructing evt)
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER // No world to check yet
			&& evt.getEntity() instanceof EntityPlayer && !(evt.getEntity() instanceof FakePlayer))
		{
			TransmutationOffline.clear(evt.getEntity().getUniqueID());
			PELogger.logDebug("Clearing offline data cache in preparation to load online data");
		}
	}

	@SubscribeEvent
	public void onHighAlchemistJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent evt)
	{
		if (PECore.uuids.contains((evt.player.getUniqueID().toString())))
		{
			ITextComponent prior = ChatHelper.modifyColor(new TextComponentTranslation("pe.server.high_alchemist"), TextFormatting.BLUE);
			ITextComponent playername = ChatHelper.modifyColor(new TextComponentString(" " + evt.player.getName() + " "), TextFormatting.GOLD);
			ITextComponent latter = ChatHelper.modifyColor(new TextComponentTranslation("pe.server.has_joined"), TextFormatting.BLUE);
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendChatMsg(prior.appendSibling(playername).appendSibling(latter)); // Sends to all everywhere, not just same world like before.
		}
	}

	@SubscribeEvent
	public void playerChangeDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event)
	{
		event.player.getCapability(InternalAbilities.CAPABILITY, null).onDimensionChange();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void pickupItem(EntityItemPickupEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = player.worldObj;
		
		if (world.isRemote)
		{
			return;
		}

		ItemStack bag = AlchemicalBag.getFirstBagWithSuctionItem(player, player.inventory.mainInventory);

		if (bag == null)
		{
			return;
		}

		IItemHandler handler = player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null)
				.getBag(EnumDyeColor.byMetadata(bag.getItemDamage()));
		ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, event.getItem().getEntityItem(), false);

		if (remainder == null)
		{
			event.getItem().setDead();
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			((EntityPlayerMP) player).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), player.getEntityId()));
		}
		else
		{
			event.getItem().setEntityItemStack(remainder);
		}

		event.setCanceled(true);
	}
}
