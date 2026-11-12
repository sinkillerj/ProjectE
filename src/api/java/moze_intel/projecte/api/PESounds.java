package moze_intel.projecte.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class PESounds {

	public static final Holder<SoundEvent> WIND = get("windmagic");
	public static final Holder<SoundEvent> WATER = get("watermagic");
	public static final Holder<SoundEvent> POWER = get("power");
	public static final Holder<SoundEvent> HEAL = get("heal");
	public static final Holder<SoundEvent> DESTRUCT = get("destruct");
	public static final Holder<SoundEvent> CHARGE = get("charge");
	public static final Holder<SoundEvent> UNCHARGE = get("uncharge");
	public static final Holder<SoundEvent> TRANSMUTE = get("transmute");

	private PESounds() {
	}

	private static Holder<SoundEvent> get(String name) {
		return DeferredHolder.create(Registries.SOUND_EVENT, Identifier.fromNamespaceAndPath(ProjectEAPI.PROJECTE_MODID, name));
	}
}
