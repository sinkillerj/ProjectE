package moze_intel.projecte.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncTableEMCPKT;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.playerData.IOHandler;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class PlayerEvents
{
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			Transmutation.sync((EntityPlayer) event.entity);
			AlchemicalBags.sync((EntityPlayer) event.entity);
			PacketHandler.sendTo(new ClientSyncTableEMCPKT(Transmutation.getStoredEmc(event.entity.getCommandSenderName())), (EntityPlayerMP) event.entity);

			if (PECore.uuids.contains(((EntityPlayer)event.entity).getUniqueID().toString()))
			{
				ChatComponentText joinMsg = new ChatComponentText(EnumChatFormatting.BLUE + "High alchemist " + EnumChatFormatting.GOLD + ((EntityPlayer)event.entity).getDisplayName() + EnumChatFormatting.BLUE + " has joined the server." + EnumChatFormatting.RESET);

				for (EntityPlayer player : (List<EntityPlayer>) event.entity.worldObj.playerEntities)
				{
					player.addChatComponentMessage(joinMsg);
				}
			}
		}
	}

	@SubscribeEvent
	public void playerChangeDimension(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide());

		PlayerChecks.onPlayerChangeDimension((EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void pickupItem(EntityItemPickupEvent event)
	{
		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;
		
		if (world.isRemote)
		{
			return;
		}

		ItemStack bag = getFirstAlchemyBag(player, player.inventory.mainInventory);
		if (bag != null)
		{
			ItemStack[] invBag = AlchemicalBags.get(player.getCommandSenderName(), ((byte) bag.getItemDamage()));
			for (ItemStack stack : invBag)
			{
				if (stack != null && stack.getItem() instanceof IAlchBagItem)
				{
					if (((IAlchBagItem) stack.getItem()).onPickUp(player, stack, event.item))
					{
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerSaveData(PlayerEvent.SaveToFile event)
	{
		if (IOHandler.markedDirty)
		{
			IOHandler.saveData();
			PELogger.logInfo("Saved transmutation and alchemical bag data.");
			
			IOHandler.markedDirty = false;
		}
	}

	private ItemStack getFirstAlchemyBag(EntityPlayer player, ItemStack[] inventory)
	{
		for (ItemStack stack : inventory)
		{
			if (stack == null)
			{
				continue;
			}

			if (stack.getItem() == ObjHandler.alchBag)
			{
				return stack;
			}
		}

		return null;
	}
}
