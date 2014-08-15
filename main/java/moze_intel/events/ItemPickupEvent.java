package moze_intel.events;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.container.inventory.AlchBagInventory;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemPickupEvent 
{
	@SubscribeEvent
	public void pickupItem(EntityItemPickupEvent event)
	{
		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;
		
		if (world.isRemote)	return;
		ItemStack bag = getAlchemyBag(player.inventory.mainInventory);
		
		if (bag == null)
		{
			return;
		}
		
		AlchBagInventory inv = new AlchBagInventory(bag);
		
		if (Utils.hasSpace(inv, event.item.getEntityItem()))
		{
			ItemStack remain = Utils.pushStackInInv(inv, event.item.getEntityItem());
			if (remain == null)
			{
				event.item.delayBeforeCanPickup = 10;
				event.item.setDead();
				world.playSoundAtEntity(player, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			}
			else event.item.setEntityItemStack(remain);
		}
	}
	
	private ItemStack getAlchemyBag(ItemStack[] inventory)
	{
		for (ItemStack stack : inventory)
		{
			if (stack == null) 
			{
				continue;
			}
			
			if (stack.getItem() == ObjHandler.alchBag && Utils.invContainsItem(new AlchBagInventory(stack), new ItemStack(ObjHandler.blackHole, 1, 1)))
			{
				return stack;
			}
		}
		return null;
	}
}
