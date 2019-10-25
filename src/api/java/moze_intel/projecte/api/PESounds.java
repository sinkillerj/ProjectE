package moze_intel.projecte.api;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ProjectEAPI.PROJECTE_MODID)
public final class PESounds
{
    @ObjectHolder("windmagic")
    public static SoundEvent WIND = null;
    @ObjectHolder("watermagic")
    public static SoundEvent WATER = null;
    @ObjectHolder("power")
    public static SoundEvent POWER = null;
    @ObjectHolder("heal")
    public static SoundEvent HEAL = null;
    @ObjectHolder("destruct")
    public static SoundEvent DESTRUCT = null;
    @ObjectHolder("charge")
    public static SoundEvent CHARGE = null;
    @ObjectHolder("uncharge")
    public static SoundEvent UNCHARGE = null;
    @ObjectHolder("transmute")
    public static SoundEvent TRANSMUTE = null;

    private PESounds() {}
}
