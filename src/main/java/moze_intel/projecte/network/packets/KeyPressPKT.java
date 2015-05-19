package moze_intel.projecte.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IItemCharge;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemArmor;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KeyPressPKT implements IMessage, IMessageHandler<KeyPressPKT, IMessage>
{
	//Not actually the key code, but the index of the keybind in the array!
	private int key;
	
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
		
		if (message.key == 0 && item instanceof IItemCharge)
		{
			((IItemCharge) item).changeCharge(player, stack);
		}
		else if (message.key == 1 && item instanceof IModeChanger)
		{
			((IModeChanger) item).changeMode(player, stack);
		}
		else if (message.key == 2 && item instanceof IProjectileShooter)
		{
			if (((IProjectileShooter) item).shootProjectile(player, stack))
			{
				PlayerHelper.swingItem((player));
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
