package moze_intel.projecte.api;

import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.codec.NSSCodecHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ProjectERegistries {

	private ProjectERegistries() {
	}

	private static ResourceLocation rl(String path) {
		return new ResourceLocation(ProjectEAPI.PROJECTE_MODID, path);
	}

	/**
	 * Gets the {@link ResourceKey} representing the name of the Registry for {@link NSSCodecHolder}.
	 *
	 * @apiNote When registering {@link NSSCodecHolder} using {@link DeferredRegister <NSSCodecHolder<?>}, use this field to get access to the {@link ResourceKey}.
	 */
	public static final ResourceKey<Registry<NSSCodecHolder<?>>> NSS_SERIALIZER_NAME = ResourceKey.createRegistryKey(rl("nss_serializer"));

	/**
	 * Gets the Registry for {@link NSSCodecHolder}.
	 *
	 * @see #NSS_SERIALIZER_NAME
	 */
	public static final Registry<NSSCodecHolder<?>> NSS_SERIALIZER = new RegistryBuilder<>(NSS_SERIALIZER_NAME)
			.defaultKey(rl("item"))//Default to item serialization
			.onBake(IPECodecHelper.INSTANCE::setSerializers)//Map what the legacy serializers use as their keys
			.create();
}