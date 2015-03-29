package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

public final class KeyBinds 
{
	public static String[] nekKeyDescArray = new String[] {
		"Charge", "Mode", "Fire Projectile", "Extra Function", "Armor effects"
	};
	public static KeyBinding[] array = new KeyBinding[] 
	{
		new KeyBinding(nekKeyDescArray[0], Keyboard.KEY_V, PECore.MODNAME),
		new KeyBinding(nekKeyDescArray[1], Keyboard.KEY_G, PECore.MODNAME),
		new KeyBinding(nekKeyDescArray[2], Keyboard.KEY_R, PECore.MODNAME),
		new KeyBinding(nekKeyDescArray[3], Keyboard.KEY_C, PECore.MODNAME),
		new KeyBinding(nekKeyDescArray[4], Keyboard.KEY_F, PECore.MODNAME)
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
