package moze_intel.projecte.integration;

import moze_intel.projecte.integration.curios.CurioItemCapability;
import moze_intel.projecte.utils.ItemCapabilityHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

public class IntegrationHelper {

	public static final String CURIO_MODID = "curios";
	public static final String EMI_MODID = "emi";
	public static final String TOP_MODID = "theoneprobe";

	@Nullable
	private static EntityCapability<ResourceHandler<ItemResource>, Void> curioItemHandlerCapability;

	/**
	 * @return The player's curios inventory as a legacy {@link ResourceHandler<ItemResource>}, or null if Curios is not loaded.
	 */
	@Nullable
	public static ResourceHandler<ItemResource> getCurioItemHandler(Player player) {
		if (!ModList.get().isLoaded(CURIO_MODID)) {
			return null;
		}
		EntityCapability<ResourceHandler<ItemResource>, Void> capability = getCurioItemHandlerCapability();
		if (capability == null) {
			return null;
		}
		return ItemCapabilityHelper.of(player.getCapability(capability));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private static EntityCapability<ResourceHandler<ItemResource>, Void> getCurioItemHandlerCapability() {
		if (curioItemHandlerCapability == null) {
			try {
				Class<?> clazz = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
				curioItemHandlerCapability = (EntityCapability<ResourceHandler<ItemResource>, Void>) clazz.getField("ITEM_HANDLER").get(null);
			} catch (ReflectiveOperationException e) {
				return null;
			}
		}
		return curioItemHandlerCapability;
	}

	public static void sendIMCMessages(InterModEnqueueEvent event) {
		// TOP has no compatible published API for this Minecraft version.
	}

	public static void registerCuriosCapability(RegisterCapabilitiesEvent event, Item item) {
		if (ModList.get().isLoaded(CURIO_MODID)) {
			CurioItemCapability.register(event, item);
		}
	}

	private static void invokeOptionalIntegration(String className, String methodName, Object... args) {
		try {
			Class<?> clazz = Class.forName(className);
			Class<?>[] paramTypes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				paramTypes[i] = args[i].getClass();
			}
			clazz.getMethod(methodName, paramTypes).invoke(null, args);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to invoke optional integration " + className + "#" + methodName, e);
		}
	}
}
