package moze_intel.projecte.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PESounds {

	public static RegistryObject<SoundEvent> WIND = get("windmagic");
	public static RegistryObject<SoundEvent> WATER = get("watermagic");
	public static RegistryObject<SoundEvent> POWER = get("power");
	public static RegistryObject<SoundEvent> HEAL = get("heal");
	public static RegistryObject<SoundEvent> DESTRUCT = get("destruct");
	public static RegistryObject<SoundEvent> CHARGE = get("charge");
	public static RegistryObject<SoundEvent> UNCHARGE = get("uncharge");
	public static RegistryObject<SoundEvent> TRANSMUTE = get("transmute");

	private PESounds() {
	}

	private static RegistryObject<SoundEvent> get(String name) {
		return RegistryObject.create(new ResourceLocation(ProjectEAPI.PROJECTE_MODID, name), ForgeRegistries.SOUND_EVENTS);
	}
}