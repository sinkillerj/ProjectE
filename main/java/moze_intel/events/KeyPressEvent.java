package moze_intel.events;

import moze_intel.MozeCore;
import moze_intel.network.packets.KeyPressPKT;
import moze_intel.utils.KeyBinds;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyPressEvent 
{
	private boolean firstJump = false;
	long lastJump;
	
	@SubscribeEvent
	public void keyPress(KeyInputEvent event)
	{
		for (int i = 0; i < KeyBinds.array.length; i++)
		{
			if (KeyBinds.isPressed(i))
			{
				MozeCore.pktHandler.sendToServer(new KeyPressPKT(i));
			}
		}
	}
}
