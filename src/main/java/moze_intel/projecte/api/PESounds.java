package moze_intel.projecte.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class PESounds
{

    public static final SoundEvent WIND = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.pewindmagic"));
    public static final SoundEvent WATER = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.pewatermagic"));
    public static final SoundEvent POWER = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.pepower"));
    public static final SoundEvent HEAL = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.peheal"));
    public static final SoundEvent DESTRUCT = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.pedestruct"));
    public static final SoundEvent CHARGE = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.pecharge"));
    public static final SoundEvent UNCHARGE = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.peuncharge"));
    public static final SoundEvent TRANSMUTE = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("projecte", "item.petransmute"));

    private PESounds() {}

}
