package moze_intel.projecte.utils;

import org.lwjgl.input.Keyboard;

/**
 * Enumeration of ProjectE keybinds and their DEFAULT key code. Use ClientKeyHelper to get runtime key code from MC Keybind.
 * To add keybinds, simply add them here and handle them in KeyPressPKT. The rest should happen automagically(tm).
 */
public enum PEKeybind
{
	ARMOR_TOGGLE("pe.key.armor_toggle", Keyboard.KEY_X),
	CHARGE("pe.key.charge", Keyboard.KEY_V),
	EXTRA_FUNCTION("pe.key.extra_function", Keyboard.KEY_C),
	FIRE_PROJECTILE("pe.key.fire_projectile", Keyboard.KEY_R),
	MODE("pe.key.mode", Keyboard.KEY_G);


	public final String keyName;
	public final int defaultKeyCode;

	PEKeybind(String keyName, int defaultKeyCode)
	{
		this.keyName = keyName;
		this.defaultKeyCode = defaultKeyCode;
	}
}
