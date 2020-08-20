package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
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
		builder.put(new KeyBinding(PEKeybind.ARMOR_TOGGLE.getTranslationKey(), GLFW.GLFW_KEY_X, PECore.MODID), PEKeybind.ARMOR_TOGGLE);
		builder.put(new KeyBinding(PEKeybind.CHARGE.getTranslationKey(), GLFW.GLFW_KEY_V, PECore.MODID), PEKeybind.CHARGE);
		builder.put(new KeyBinding(PEKeybind.EXTRA_FUNCTION.getTranslationKey(), GLFW.GLFW_KEY_C, PECore.MODID), PEKeybind.EXTRA_FUNCTION);
		builder.put(new KeyBinding(PEKeybind.FIRE_PROJECTILE.getTranslationKey(), GLFW.GLFW_KEY_R, PECore.MODID), PEKeybind.FIRE_PROJECTILE);
		builder.put(new KeyBinding(PEKeybind.MODE.getTranslationKey(), GLFW.GLFW_KEY_G, PECore.MODID), PEKeybind.MODE);
		mcToPe = builder.build();
		peToMc = mcToPe.inverse();
		for (KeyBinding k : mcToPe.keySet()) {
			ClientRegistry.registerKeyBinding(k);
		}
	}

	public static ITextComponent getKeyName(PEKeybind k) {
		//TODO - 1.16: Transition over to passing the PEKeybind directly to the translate method and have that then call this. Ensure doing so doesn't mess up servers
		if (peToMc.containsKey(k)) {
			return peToMc.get(k).func_238171_j_();
		}
		//Fallback to the translation key of the key's function
		return new TranslationTextComponent(k.getTranslationKey());
	}
}