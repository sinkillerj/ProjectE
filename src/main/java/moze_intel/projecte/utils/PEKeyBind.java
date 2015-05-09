package moze_intel.projecte.utils;

import org.lwjgl.input.Keyboard;

public enum PEKeyBind
{
	CHARGE("Charge", Keyboard.KEY_V),
	MODE("Mode", Keyboard.KEY_G),
	FIRE_PROJECTILE("Fire Projectile", Keyboard.KEY_R),
	EXTRA_FUNCTION("Extra Function", Keyboard.KEY_C),
	ARMOR_TOGGLE("Armor effects", Keyboard.KEY_F);

	public final String keyName;
	public final int keyCode;

	PEKeyBind(String keyName, int keyCode)
	{
		this.keyName = keyName;
		this.keyCode = keyCode;
	}

	public static PEKeyBind getFromName(String name)
	{
		for (PEKeyBind k : values())
		{
			if (k.keyName.equals(name))
			{
				return k;
			}
		}
		return null;
	}
}
