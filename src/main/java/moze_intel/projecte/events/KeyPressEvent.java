package moze_intel.projecte.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KeyPressPKT;
import moze_intel.projecte.utils.KeyHelper;

@SideOnly(Side.CLIENT)
public class KeyPressEvent 
{
	@SubscribeEvent
	public void keyPress(KeyInputEvent event)
	{
		for (int i = 0; i < KeyHelper.array.length; i++)
		{
			if (KeyHelper.isPressed(i))
			{
				PacketHandler.sendToServer(new KeyPressPKT(i));
			}
		}
	}
}
