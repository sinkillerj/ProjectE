package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class SoundHandler
{

    public static void init()
    {
        registerSound("item.pewindmagic");
        registerSound("item.pewatermagic");
        registerSound("item.pepower");
        registerSound("item.peheal");
        registerSound("item.pedestruct");
        registerSound("item.pecharge");
        registerSound("item.peuncharge");
        registerSound("item.petransmute");
    }

    private static void registerSound(String soundName)
    {
        ResourceLocation name = new ResourceLocation(PECore.MODID, soundName);
        GameRegistry.register(new SoundEvent(name), name);
    }

    private SoundHandler() {}

}
