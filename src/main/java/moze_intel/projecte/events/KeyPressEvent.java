package moze_intel.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KeyPressPKT;
import moze_intel.projecte.utils.PEKeyBind;

@SideOnly(Side.CLIENT)
public class KeyPressEvent 
{
	@SubscribeEvent
	public void keyPress(KeyInputEvent event)
	{
		for (PEKeyBind k : PEKeyBind.values())
		{
			if (k.mcKeyBinding.isPressed())
			{
				PacketHandler.sendToServer(new KeyPressPKT(k));
			}
		}
	}
}
