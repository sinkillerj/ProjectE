package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.blaze3d.platform.InputConstants;
import moze_intel.projecte.network.packets.to_server.KeyPressPKT;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class ClientKeyHelper {

	private static ImmutableBiMap<PEKeybind, KeyMapping> peToMc = ImmutableBiMap.of();

	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		ImmutableBiMap.Builder<PEKeybind, KeyMapping> builder = ImmutableBiMap.builder();
		addKeyBinding(event, builder, PEKeybind.HELMET_TOGGLE, KeyModifier.SHIFT, GLFW.GLFW_KEY_X);
		addKeyBinding(event, builder, PEKeybind.BOOTS_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_X);
		addKeyBinding(event, builder, PEKeybind.CHARGE, KeyModifier.NONE, GLFW.GLFW_KEY_V);
		addKeyBinding(event, builder, PEKeybind.EXTRA_FUNCTION, KeyModifier.NONE, GLFW.GLFW_KEY_C);
		addKeyBinding(event, builder, PEKeybind.FIRE_PROJECTILE, KeyModifier.NONE, GLFW.GLFW_KEY_R);
		addKeyBinding(event, builder, PEKeybind.MODE, KeyModifier.NONE, GLFW.GLFW_KEY_G);
		addKeyBinding(event, builder, PEKeybind.TRANSMUTATION_TABLET, KeyModifier.NONE, GLFW.GLFW_KEY_K);
		peToMc = builder.build();
	}

	private static void addKeyBinding(RegisterKeyMappingsEvent event, ImmutableBiMap.Builder<PEKeybind, KeyMapping> builder, PEKeybind keyBind, KeyModifier modifier, int keyCode) {
		KeyMapping keyMapping = new PEKeyMapping(keyBind, modifier, keyCode);
		builder.put(keyBind, keyMapping);
		event.register(keyMapping);
	}

	public static Component getKeyName(PEKeybind k) {
		KeyMapping keyMapping = peToMc.get(k);
		if (keyMapping == null) {
			//Fallback to the translation key of the key's function
			return TextComponentUtil.build(k);
		}
		return keyMapping.getTranslatedKeyMessage();
	}

	private static class PEKeyMapping extends KeyMapping {

		private final PEKeybind keybind;
		private boolean lastState;

		PEKeyMapping(PEKeybind keybind, KeyModifier keyModifier, int keyCode) {
			super(keybind.getTranslationKey(), KeyConflictContext.IN_GAME, keyModifier, InputConstants.Type.KEYSYM, keyCode, PELang.PROJECTE.getTranslationKey());
			this.keybind = keybind;
		}

		@Override
		public void setDown(boolean value) {
			super.setDown(value);
			//Note: We check the state based on isDown instead of value, as the value may be wrong depending on the conflict context
			boolean state = isDown();
			if (state != lastState) {
				if (state) {
					PacketDistributor.sendToServer(new KeyPressPKT(keybind));
				}
				lastState = state;
			}
		}
	}
}