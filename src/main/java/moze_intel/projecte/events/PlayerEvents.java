package moze_intel.projecte.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.container.inventory.AlchBagInventory;
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
			ItemStack[] invBag;
			ItemStack[] deepCopy;
			for (int i = 0; i < 104; i++)
			{
				// Grab a new shallow copy of the state
				if (player.openContainer instanceof AlchBagContainer)
				{
					// Grab shallow copy from the container when it was opened
					AlchBagInventory abi = ((AlchBagContainer) player.openContainer).inventory;
					abi.refresh();
					invBag = ((AlchBagContainer) player.openContainer).inventory.getInventory();
				}
				else
				{
					invBag = AlchemicalBags.get(player.getCommandSenderName(), ((byte) bag.getItemDamage()));
				}

				if (invBag[i] != null && invBag[i].getItem() instanceof IAlchBagItem)
				{
					deepCopy = Utils.deepCopyItemStackArr(invBag);
					// pass deep copy of state to bag to modify. This prevents weirdness when nulling stuff
					if (((IAlchBagItem) invBag[i].getItem()).onPickUp(player, deepCopy, event.item))
					{
						event.setCanceled(true);
						// write the modified deep copy
						AlchemicalBags.set(player.getCommandSenderName(), ((byte) bag.getItemDamage()), deepCopy);
						AlchemicalBags.sync(player);
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
