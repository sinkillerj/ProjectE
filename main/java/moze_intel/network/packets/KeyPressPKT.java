package moze_intel.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.MozeCore;
import moze_intel.events.PlayerChecksEvent;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.IExtraFunction;
import moze_intel.gameObjs.items.IItemModeChanger;
import moze_intel.gameObjs.items.IProjectileShooter;
import moze_intel.gameObjs.items.ItemCharge;
import moze_intel.gameObjs.items.ItemMode;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class KeyPressPKT implements IMessage, IMessageHandler<KeyPressPKT, IMessage>
{
	//Not actually the key code, but the number of the keybind in the array!
	private int key;
	
	//Needs to have an empty constructor
	public KeyPressPKT() {}
	
	public KeyPressPKT(int key)
	{
		this.key = key;
	}

	@Override
	public IMessage onMessage(KeyPressPKT message, MessageContext ctx) 
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		ItemStack stack = player.getHeldItem();
		
		if (message.key == 1)
		{
			ItemStack armour = player.inventory.armorInventory[0];
		
			if (armour != null && armour.getItem() == ObjHandler.gemFeet)
			{
				if (PlayerChecksEvent.isStepAssistDisabled((EntityPlayerMP) player))
				{
					PlayerChecksEvent.reEnableStepAssists((EntityPlayerMP) player);
				}
				else
				{
					PlayerChecksEvent.disablePlayerStep((EntityPlayerMP) player);
				}
			}
		}
		
		if (stack == null)
		{
			return null;
		}
		
		Item item = stack.getItem();
		
		if (message.key == 0 && item instanceof ItemCharge)
		{
			((ItemCharge) item).changeCharge(player);
		}
		else if (message.key == 1)
		{
			if (item instanceof ItemMode)
			{
				((ItemMode) item).changeMode(stack, player);
			}
			else if (item instanceof IItemModeChanger)
			{
				((IItemModeChanger) item).changeMode(player, stack);
			}
		}
		else if (message.key == 2 && item instanceof IProjectileShooter)
		{
			if (((IProjectileShooter) item).shootProjectile(player, stack))
			{
				MozeCore.pktHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			}
		}
		else if (message.key == 3 && item instanceof IExtraFunction)
		{
			((IExtraFunction) item).doExtraFunction(stack, player);
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		key = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(key);
	}
}
