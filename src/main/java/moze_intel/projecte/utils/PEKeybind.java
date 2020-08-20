package moze_intel.projecte.utils;

import java.util.Locale;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public enum PEKeybind implements IHasTranslationKey {
	ARMOR_TOGGLE,
	CHARGE,
	EXTRA_FUNCTION,
	FIRE_PROJECTILE,
	MODE;

	private final String translationKey;

	PEKeybind() {
		this.translationKey = Util.makeTranslationKey("key", new ResourceLocation(PECore.MODID, name().toLowerCase(Locale.ROOT)));
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}