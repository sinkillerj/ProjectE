package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = PECore.MODID)
public final class SoundHandler
{

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> evt)
    {
        registerSound(evt.getRegistry(), "item.pewindmagic");
        registerSound(evt.getRegistry(), "item.pewatermagic");
        registerSound(evt.getRegistry(), "item.pepower");
        registerSound(evt.getRegistry(), "item.peheal");
        registerSound(evt.getRegistry(), "item.pedestruct");
        registerSound(evt.getRegistry(), "item.pecharge");
        registerSound(evt.getRegistry(), "item.peuncharge");
        registerSound(evt.getRegistry(), "item.petransmute");
    }

    private static void registerSound(IForgeRegistry<SoundEvent> registry, String soundName)
    {
        ResourceLocation name = new ResourceLocation(PECore.MODID, soundName);
        registry.register(new SoundEvent(name).setRegistryName(name));
    }

    private SoundHandler() {}
}
