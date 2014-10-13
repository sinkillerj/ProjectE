package moze_intel.projecte.utils;

import moze_intel.projecte.MozeCore;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

public class KeyBinds 
{
	public static KeyBinding[] array = new KeyBinding[] 
	{
		new KeyBinding("Charge", Keyboard.KEY_V, MozeCore.MODNAME),
		new KeyBinding("Mode", Keyboard.KEY_G, MozeCore.MODNAME),
		new KeyBinding("Fire Projectile", Keyboard.KEY_R, MozeCore.MODNAME),
		new KeyBinding("Extra Function", Keyboard.KEY_C, MozeCore.MODNAME),
		new KeyBinding("Armor effects", Keyboard.KEY_F, MozeCore.MODNAME)
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
