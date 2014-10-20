package moze_intel.projecte.events;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class ItemPickupEvent 
{
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
