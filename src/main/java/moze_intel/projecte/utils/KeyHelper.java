package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

public final class KeyHelper
{
	public static KeyBinding[] array = new KeyBinding[] 
	{
		new KeyBinding("Charge", Keyboard.KEY_V, PECore.MODNAME),
		new KeyBinding("Mode", Keyboard.KEY_G, PECore.MODNAME),
		new KeyBinding("Fire Projectile", Keyboard.KEY_R, PECore.MODNAME),
		new KeyBinding("Extra Function", Keyboard.KEY_C, PECore.MODNAME),
		new KeyBinding("Armor effects", Keyboard.KEY_F, PECore.MODNAME)
	};
	
	public static int getChargeKeyCode()
	{
		return array[0].getKeyCode();
	}
	
	public static int getModeKeyCode()
	{
		return array[1].getKeyCode();
	}
	
	public static int getProjectileKeyCode()
	{
		return array[2].getKeyCode();
	}
	
	public static int getExtraFuncKeyCode()
	{
		return array[3].getKeyCode();
	}
	
	public static int getArmorEffectsKeyCode()
	{
		return array[4].getKeyCode();
	}
	
	public static boolean isPressed(int index)
	{
		return array[index].isPressed();
	}
	
	public static int getKeyCode(int index)
	{
		return array[index].getKeyCode();
	}
}
