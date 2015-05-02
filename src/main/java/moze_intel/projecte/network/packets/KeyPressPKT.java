package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IItemCharge;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.armor.GemArmor;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.utils.NovaExplosion;
import moze_intel.projecte.utils.PEKeyBind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class KeyPressPKT implements IMessage, IMessageHandler<KeyPressPKT, IMessage>
{
	private PEKeyBind key;
	
	public KeyPressPKT() {}
	
	public KeyPressPKT(PEKeyBind key)
	{
		this.key = key;
	}

	@Override
	public IMessage onMessage(KeyPressPKT message, MessageContext ctx) 
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		ItemStack stack = player.getHeldItem();
		
		if (message.key == PEKeyBind.ARMOR_TOGGLE)
		{
			if (player.isSneaking())
			{
				ItemStack helm = player.inventory.armorItemInSlot(3);
				
				if (helm != null && helm.getItem() == ObjHandler.gemHelmet)
				{
					GemHelmet.toggleNightVision(helm, player);
				}
			}
			else
			{
				ItemStack boots = player.inventory.armorItemInSlot(0);
			
				if (boots != null && boots.getItem() == ObjHandler.gemFeet)
				{
					GemFeet.toggleStepAssist(boots, player);
				}
			}
		}
		
		if (stack == null || !(stack.getItem() instanceof ItemPE))
		{
			ItemStack[] armor = player.inventory.armorInventory;
			if (armor[2] != null && armor[2].getItem() == ObjHandler.gemChest && message.key == PEKeyBind.EXTRA_FUNCTION)
			{
				NovaExplosion explosion = new NovaExplosion(player.worldObj, player, player.posX, player.posY, player.posZ, 9.0F);
				explosion.isFlaming = true;
				explosion.isSmoking = true;
				explosion.doExplosionA();
				explosion.doExplosionB(true);
			}
			if (armor[3] != null && armor[3].getItem() == ObjHandler.gemHelmet && message.key == PEKeyBind.FIRE_PROJECTILE)
			{
				//Todo: Shoot lightning where the player is looking. Whoever implements the "where am I looking code" could also do the void ring as well.
			}



			return null;
		}
		
		Item item = stack.getItem();
		
		if (message.key == PEKeyBind.CHARGE && item instanceof IItemCharge)
		{
			((IItemCharge) item).changeCharge(player, stack);
		}
		else if (message.key == PEKeyBind.MODE && item instanceof IModeChanger)
		{
			((IModeChanger) item).changeMode(player, stack);
		}
		else if (message.key == PEKeyBind.FIRE_PROJECTILE && item instanceof IProjectileShooter)
		{
			if (((IProjectileShooter) item).shootProjectile(player, stack))
			{
				PlayerHelper.swingItem((player));
			}
		}
		else if (message.key == PEKeyBind.EXTRA_FUNCTION && item instanceof IExtraFunction)
		{
			((IExtraFunction) item).doExtraFunction(stack, player);
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		key = PEKeyBind.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(key.ordinal());
	}
}
