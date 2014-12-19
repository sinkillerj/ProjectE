package moze_intel.projecte.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncTableEMCPKT;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.playerData.IOHandler;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

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
		
		if (player.openContainer instanceof AlchBagContainer)
		{
			IInventory inv = ((AlchBagContainer) player.openContainer).inventory;
			
			if (Utils.invContainsItem(inv, new ItemStack(ObjHandler.blackHole, 1, 1)) && Utils.hasSpace(inv, event.item.getEntityItem()))
			{
				ItemStack remain = Utils.pushStackInInv(inv, event.item.getEntityItem());
				
				if (remain == null)
				{
					event.item.delayBeforeCanPickup = 10;
					event.item.setDead();
					world.playSoundAtEntity(player, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				}
				else 
				{
					event.item.setEntityItemStack(remain);
				}
				
				event.setCanceled(true);
			}
		}
		else
		{
			ItemStack bag = getAlchemyBag(player, player.inventory.mainInventory);
			
			if (bag == null)
			{
				return;
			}
			
			ItemStack[] inv = AlchemicalBags.get(player.getCommandSenderName(), (byte) bag.getItemDamage());
			
			if (Utils.hasSpace(inv, event.item.getEntityItem()))
			{
				ItemStack remain = Utils.pushStackInInv(inv, event.item.getEntityItem());
				
				if (remain == null)
				{
					event.item.delayBeforeCanPickup = 10;
					event.item.setDead();
					world.playSoundAtEntity(player, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				}
				else 
				{
					event.item.setEntityItemStack(remain);
				}
				
				AlchemicalBags.set(player.getCommandSenderName(), (byte) bag.getItemDamage(), inv);
				AlchemicalBags.sync(player);
				
				event.setCanceled(true);
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
	
	private ItemStack getAlchemyBag(EntityPlayer player, ItemStack[] inventory)
	{
		for (ItemStack stack : inventory)
		{
			if (stack == null) 
			{
				continue;
			}
			
			if (stack.getItem() == ObjHandler.alchBag && Utils.invContainsItem(AlchemicalBags.get(player.getCommandSenderName(), (byte) stack.getItemDamage()), new ItemStack(ObjHandler.blackHole, 1, 1)))
			{
				return stack;
			}
		}
		
		return null;
	}
}
