package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import java.util.Locale;
import moze_intel.projecte.PECore;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KeyPressPKT;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ClientKeyHelper {

	private static ImmutableBiMap<KeyBinding, PEKeybind> mcToPe = ImmutableBiMap.of();
	private static ImmutableBiMap<PEKeybind, KeyBinding> peToMc = ImmutableBiMap.of();

	@SubscribeEvent
	public static void keyPress(TickEvent.ClientTickEvent event) {
		for (KeyBinding k : mcToPe.keySet()) {
			while (k.isPressed()) {
				PacketHandler.sendToServer(new KeyPressPKT(mcToPe.get(k)));
			}
		}
	}

	public static void registerKeyBindings() {
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

	public static String getKeyTranslationKey(PEKeybind k) {
		return "key.projecte." + k.name().toLowerCase(Locale.ROOT);
	}

	public static ITextComponent getKeyName(PEKeybind k) {
		if (peToMc.containsKey(k)) {
			return peToMc.get(k).func_238171_j_();
		}
		//Fallback to the translation key of the key's function
		return new TranslationTextComponent(getKeyTranslationKey(k));
	}
}