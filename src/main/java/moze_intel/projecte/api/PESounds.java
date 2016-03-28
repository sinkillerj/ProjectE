package moze_intel.projecte.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public final class PESounds
{

    public static final SoundEvent WIND = getSound("projecte:item.pewindmagic");
    public static final SoundEvent WATER = getSound("projecte:item.pewatermagic");
    public static final SoundEvent POWER = getSound("projecte:item.pepower");
    public static final SoundEvent HEAL = getSound("projecte:item.peheal");
    public static final SoundEvent DESTRUCT = getSound("projecte:item.pedestruct");
    public static final SoundEvent CHARGE = getSound("projecte:item.pecharge");
    public static final SoundEvent UNCHARGE = getSound("projecte:item.peuncharge");
    public static final SoundEvent TRANSMUTE = getSound("projecte:item.petransmute");

    private static SoundEvent getSound(String name)
    {
        return SoundEvent.soundEventRegistry.getObject(new ResourceLocation(name));
    }

    private PESounds() {}

}
