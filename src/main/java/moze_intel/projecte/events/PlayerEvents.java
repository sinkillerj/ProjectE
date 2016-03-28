package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.playerData.AlchBagProps;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.playerData.TransmutationOffline;
import moze_intel.projecte.playerData.TransmutationProps;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerEvents
{
	// Handles playerData props from being wiped on death
	@SubscribeEvent
	public void cloneEvent(PlayerEvent.Clone evt)
	{
		if (!evt.isWasDeath())
		{
			return; // Vanilla handles it for us.
		}

		NBTTagCompound bag = new NBTTagCompound();
		NBTTagCompound transmute = new NBTTagCompound();

		AlchBagProps.getDataFor(evt.getOriginal()).saveNBTData(bag); // Cache old
		TransmutationProps.getDataFor(evt.getOriginal()).saveNBTData(transmute);

		AlchBagProps.getDataFor(evt.getEntityPlayer()).loadNBTData(bag); // Reapply on new
		TransmutationProps.getDataFor(evt.getEntityPlayer()).loadNBTData(transmute);

		PELogger.logDebug("Reapplied bag and knowledge on player respawning");
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = ((EntityPlayerMP) event.getEntity());
			Transmutation.sync(player);
			AlchemicalBags.syncFull(player);
		}
	}

	@SubscribeEvent
	public void onConstruct(EntityEvent.EntityConstructing evt)
	{
		if (evt.getEntity() instanceof EntityPlayer && !(evt.getEntity() instanceof FakePlayer))
		{
			TransmutationOffline.clear(evt.getEntity().getUniqueID());
			PELogger.logDebug("Clearing offline data cache in preparation to load online data");

			TransmutationProps.register(((EntityPlayer) evt.getEntity()));
			AlchBagProps.register(((EntityPlayer) evt.getEntity()));
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
		PlayerChecks.onPlayerChangeDimension((EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void pickupItem(EntityItemPickupEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = player.worldObj;
		
		if (world.isRemote)
		{
			return;
		}
		
		if (player.openContainer instanceof AlchBagContainer)
		{
			IInventory inv = ((AlchBagContainer) player.openContainer).inventory;
			
			if (ItemHelper.invContainsItem(inv, new ItemStack(ObjHandler.blackHole, 1, 1)) || ItemHelper.invContainsItem(inv, new ItemStack(ObjHandler.voidRing, 1, 1))
					&& ItemHelper.hasSpace(inv, event.getItem().getEntityItem()))
			{
				ItemStack remain = ItemHelper.pushStackInInv(inv, event.getItem().getEntityItem());
				
				if (remain == null)
				{
					event.getItem().setPickupDelay(10);
					event.getItem().setDead();
					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.entity_item_pickup, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				}
				else 
				{
					event.getItem().setEntityItemStack(remain);
				}
				
				event.setCanceled(true);
			}
		}
		else
		{
			ItemStack bag = AlchemicalBag.getFirstBagWithSuctionItem(player, player.inventory.mainInventory);
			
			if (bag == null)
			{
				return;
			}
			
			ItemStack[] inv = AlchemicalBags.get(player, (byte) bag.getItemDamage());
			
			if (ItemHelper.hasSpace(inv, event.getItem().getEntityItem()))
			{
				ItemStack remain = ItemHelper.pushStackInInv(inv, event.getItem().getEntityItem());
				
				if (remain == null)
				{
					event.getItem().setPickupDelay(10);
					event.getItem().setDead();
					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.entity_item_pickup, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				}
				else 
				{
					event.getItem().setEntityItemStack(remain);
				}
				
				AlchemicalBags.set(player, (byte) bag.getItemDamage(), inv);
				AlchemicalBags.syncPartial(player, bag.getItemDamage());
				
				event.setCanceled(true);
			}
		}
	}
}
