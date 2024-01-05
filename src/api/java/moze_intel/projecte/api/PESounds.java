package moze_intel.projecte.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class PESounds {

	public static Holder<SoundEvent> WIND = get("windmagic");
	public static Holder<SoundEvent> WATER = get("watermagic");
	public static Holder<SoundEvent> POWER = get("power");
	public static Holder<SoundEvent> HEAL = get("heal");
	public static Holder<SoundEvent> DESTRUCT = get("destruct");
	public static Holder<SoundEvent> CHARGE = get("charge");
	public static Holder<SoundEvent> UNCHARGE = get("uncharge");
	public static Holder<SoundEvent> TRANSMUTE = get("transmute");

	private PESounds() {
	}

	private static Holder<SoundEvent> get(String name) {
		return DeferredHolder.create(Registries.SOUND_EVENT, new ResourceLocation(ProjectEAPI.PROJECTE_MODID, name));
	}
}