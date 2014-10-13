package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.events.PlayerChecksEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.IExtraFunction;
import moze_intel.projecte.gameObjs.items.IItemModeChanger;
import moze_intel.projecte.gameObjs.items.IProjectileShooter;
import moze_intel.projecte.gameObjs.items.ItemCharge;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.gameObjs.items.armor.GemArmor;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class KeyPressPKT implements IMessage, IMessageHandler<KeyPressPKT, IMessage>
{
	//Not actually the key code, but the index of the keybind in the array!
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
		
		if (message.key == 4)
		{
			if (player.isSneaking())
			{
				ItemStack helm = player.inventory.armorItemInSlot(3);
				
				if (helm != null && helm.getItem() == ObjHandler.gemHelmet)
				{
					GemArmor.toggleNightVision(helm, player);
				}
			}
			else
			{
				ItemStack boots = player.inventory.armorItemInSlot(0);
			
				if (boots != null && boots.getItem() == ObjHandler.gemFeet)
				{
					GemArmor.toggleStepAssist(boots, player);
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
				PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
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
