package moze_intel.projecte.events;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KeyPressPKT;
import moze_intel.projecte.utils.KeyBinds;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyPressEvent 
{
	@SubscribeEvent
	public void keyPress(KeyInputEvent event)
	{
		if (Loader.isModLoaded("notenoughkeys")) return;
		checkKeys();
	}

	private void checkKeys() {
		for (int i = 0; i < KeyBinds.array.length; i++)
		{
			if (KeyBinds.isPressed(i))
			{
				PacketHandler.sendToServer(new KeyPressPKT(i));
			}
		}
	}

	@Optional.Method(modid = "notenoughkeys")
	@SubscribeEvent
	public void keyEventSpecial(KeyBindingPressedEvent event) {
		checkKeys();
	}
}
