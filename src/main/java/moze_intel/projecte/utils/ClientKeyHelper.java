package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import moze_intel.projecte.PECore;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KeyPressPKT;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ClientKeyHelper
{
    private static ImmutableBiMap<KeyBinding, PEKeybind> mcToPe;
    private static ImmutableBiMap<PEKeybind, KeyBinding> peToMc;

    @SubscribeEvent
	public static void keyPress(TickEvent.ClientTickEvent event)
	{
		for (KeyBinding k : ClientKeyHelper.mcToPe.keySet())
		{
			while (k.isPressed())
			{
				PacketHandler.sendToServer(new KeyPressPKT(ClientKeyHelper.mcToPe.get(k)));
			}
		}
	}

    public static void registerKeyBindings()
    {
        ImmutableBiMap.Builder<KeyBinding, PEKeybind> builder = ImmutableBiMap.builder();
        builder.put(new KeyBinding("key.projecte.armor_toggle", GLFW.GLFW_KEY_X, PECore.MODID), PEKeybind.ARMOR_TOGGLE);
        builder.put(new KeyBinding("key.projecte.charge", GLFW.GLFW_KEY_V, PECore.MODID), PEKeybind.CHARGE);
        builder.put(new KeyBinding("key.projecte.extra_function", GLFW.GLFW_KEY_C, PECore.MODID), PEKeybind.EXTRA_FUNCTION);
        builder.put(new KeyBinding("key.projecte.fire_projectile", GLFW.GLFW_KEY_R, PECore.MODID), PEKeybind.FIRE_PROJECTILE);
        builder.put(new KeyBinding("key.projecte.mode", GLFW.GLFW_KEY_G, PECore.MODID), PEKeybind.MODE);
        mcToPe = builder.build();
        peToMc = mcToPe.inverse();
        for (KeyBinding k : mcToPe.keySet()) {
            ClientRegistry.registerKeyBinding(k);
        }
    }

    public static String getKeyName(PEKeybind k)
    {
        return getKeyName(peToMc.get(k));
    }

    public static String getKeyName(KeyBinding k)
    {
        return k.getTranslationKey();
    }
}
