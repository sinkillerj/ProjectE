package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemArmorBase;
import moze_intel.projecte.gameObjs.items.armor.GemChest;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

public class KeyPressPKT implements IMessage, IMessageHandler<KeyPressPKT, IMessage>
{
	private PEKeybind key;
	
	public KeyPressPKT() {}
	
	public KeyPressPKT(PEKeybind key)
	{
		this.key = key;
	}

	@Override
	public IMessage onMessage(KeyPressPKT message, MessageContext ctx) 
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		ItemStack stack = player.getHeldItem();
		
		if (message.key == PEKeybind.ARMOR_TOGGLE)
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
		
		if (stack == null)
		{
			if (message.key == PEKeybind.CHARGE && GemArmorBase.hasAnyPiece(player))
			{
				PlayerChecks.setGemState(player, !PlayerChecks.getGemState(player));
				player.addChatMessage(new ChatComponentTranslation(PlayerChecks.getGemState(player) ? "pe.gem.activate" : "pe.gem.deactivate"));
				return null;
			}

			if (PlayerChecks.getGemState(player)) {
				ItemStack[] armor = player.inventory.armorInventory;
				if (armor[2] != null && armor[2].getItem() == ObjHandler.gemChest && message.key == PEKeybind.EXTRA_FUNCTION)
                {
                    GemChest.doExplode(player);
                }
				if (armor[3] != null && armor[3].getItem() == ObjHandler.gemHelmet && message.key == PEKeybind.FIRE_PROJECTILE)
                {
                    GemHelmet.doZap(player);
                }
			}

			return null;
		}
		
		Item item = stack.getItem();
		
		if (message.key == PEKeybind.CHARGE && item instanceof IItemCharge)
		{
			((IItemCharge) item).changeCharge(player, stack);
		}
		else if (message.key == PEKeybind.MODE && item instanceof IModeChanger)
		{
			((IModeChanger) item).changeMode(player, stack);
		}
		else if (message.key == PEKeybind.FIRE_PROJECTILE && item instanceof IProjectileShooter)
		{
			if (((IProjectileShooter) item).shootProjectile(player, stack))
			{
				PlayerHelper.swingItem((player));
			}
		}
		else if (message.key == PEKeybind.EXTRA_FUNCTION && item instanceof IExtraFunction)
		{
			((IExtraFunction) item).doExtraFunction(stack, player);
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		key = PEKeybind.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(key.ordinal());
	}
}
