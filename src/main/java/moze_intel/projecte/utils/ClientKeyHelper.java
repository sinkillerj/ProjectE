package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import moze_intel.projecte.PECore;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_server.KeyPressPKT;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
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
		addKeyBinding(builder, PEKeybind.HELMET_TOGGLE, KeyModifier.SHIFT, GLFW.GLFW_KEY_X);
		addKeyBinding(builder, PEKeybind.BOOTS_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_X);
		addKeyBinding(builder, PEKeybind.CHARGE, KeyModifier.NONE, GLFW.GLFW_KEY_V);
		addKeyBinding(builder, PEKeybind.EXTRA_FUNCTION, KeyModifier.NONE, GLFW.GLFW_KEY_C);
		addKeyBinding(builder, PEKeybind.FIRE_PROJECTILE, KeyModifier.NONE, GLFW.GLFW_KEY_R);
		addKeyBinding(builder, PEKeybind.MODE, KeyModifier.NONE, GLFW.GLFW_KEY_G);
		mcToPe = builder.build();
		peToMc = mcToPe.inverse();
		for (KeyBinding k : mcToPe.keySet()) {
			ClientRegistry.registerKeyBinding(k);
		}
	}

	private static void addKeyBinding(ImmutableBiMap.Builder<KeyBinding, PEKeybind> builder, PEKeybind keyBind, KeyModifier modifier, int keyCode) {
		builder.put(new KeyBinding(keyBind.getTranslationKey(), KeyConflictContext.IN_GAME, modifier, InputMappings.Type.KEYSYM, keyCode,
						PELang.PROJECTE.getTranslationKey()), keyBind);
	}

	public static ITextComponent getKeyName(PEKeybind k) {
		if (peToMc.containsKey(k)) {
			return peToMc.get(k).func_238171_j_();
		}
		//Fallback to the translation key of the key's function
		return new TranslationTextComponent(k.getTranslationKey());
	}
}