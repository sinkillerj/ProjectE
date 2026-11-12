package moze_intel.projecte.api.nss;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Fluid}s.
 */
public final class NSSFluid extends AbstractDataComponentHolderNSSTag<Fluid> {

	private static final ResourceKey<Fluid> DEFAULT_KEY = ResourceKey.create(Registries.FLUID, BuiltInRegistries.FLUID.getDefaultKey());
	public static final MapCodec<NSSFluid> CODEC = createCodec(BuiltInRegistries.FLUID, false, NSSFluid::new);


	private NSSFluid(@NotNull Identifier resourceLocation, boolean isTag, @NotNull DataComponentPatch componentsPatch) {
		super(resourceLocation, isTag, componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link FluidStack}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull FluidStack stack) {
		//Don't bother checking if it is empty as getFluid returns EMPTY which will then fail anyway for being empty
		return createFluid(stack.typeHolder(), stack.getComponentsPatch());
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Fluid fluid) {
		return createFluid(fluid, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid} and an optional {@link DataComponentPatch}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Fluid fluid, @NotNull DataComponentPatch componentsPatch) {
		return createFluid(fluid.builtInRegistryHolder(), componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Holder} with no data components.
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Holder<Fluid> item) {
		return createFluid(item, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Holder} and an optional {@link DataComponentPatch}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Holder<Fluid> fluidHolder, @NotNull DataComponentPatch componentsPatch) {
		ResourceKey<Fluid> key = fluidHolder.getKey();
		if (key == null) {
			if (!fluidHolder.isBound()) {
				throw new IllegalArgumentException("Can't make an NSSFluid with an unbound direct holder");
			}
			Optional<ResourceKey<Fluid>> registryKey = BuiltInRegistries.FLUID.getResourceKey(fluidHolder.value());
			if (registryKey.isEmpty()) {
				throw new IllegalArgumentException("Can't make an NSSFluid with an unregistered fluid");
			}
			key = registryKey.get();
		}
		if (key == DEFAULT_KEY) {
			throw new IllegalArgumentException("Can't make NSSFluid with an empty stack");
		}
		//This should never be null, or it would have crashed on being registered
		return createFluid(key.identifier(), componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Identifier}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Identifier fluidID) {
		return createFluid(fluidID, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Identifier} and an optional {@link DataComponentPatch}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Identifier fluidID, @NotNull DataComponentPatch componentsPatch) {
		return new NSSFluid(fluidID, false, componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link Identifier}
	 */
	@NotNull
	public static NSSFluid createTag(@NotNull Identifier tagId) {
		return new NSSFluid(tagId, true, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link TagKey<Fluid>}
	 */
	@NotNull
	public static NSSFluid createTag(@NotNull TagKey<Fluid> tag) {
		return createTag(tag.location());
	}

	@NotNull
	@Override
	protected Registry<Fluid> getRegistry() {
		return BuiltInRegistries.FLUID;
	}

	@Override
	protected NSSFluid createNew(Holder<Fluid> fluid) {
		return createFluid(fluid);
	}

	@Override
	public MapCodec<NSSFluid> codec() {
		return CODEC;
	}
}
