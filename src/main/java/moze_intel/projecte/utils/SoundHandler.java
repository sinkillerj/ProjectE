package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SoundHandler
{

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> evt)
    {
        registerSound(evt.getRegistry(), "windmagic");
        registerSound(evt.getRegistry(), "watermagic");
        registerSound(evt.getRegistry(), "power");
        registerSound(evt.getRegistry(), "heal");
        registerSound(evt.getRegistry(), "destruct");
        registerSound(evt.getRegistry(), "charge");
        registerSound(evt.getRegistry(), "uncharge");
        registerSound(evt.getRegistry(), "transmute");
    }

    private static void registerSound(IForgeRegistry<SoundEvent> registry, String soundName)
    {
        ResourceLocation name = new ResourceLocation(PECore.MODID, soundName);
        registry.register(new SoundEvent(name).setRegistryName(name));
    }

    private SoundHandler() {}
}
