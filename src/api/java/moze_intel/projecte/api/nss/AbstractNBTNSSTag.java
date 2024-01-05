package moze_intel.projecte.api.nss;

import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract implementation to make implementing {@link NSSTag} and {@link NSSNBT} simpler, and automatically be able to register conversions for:
 * <p>
 * - Tag -> Type
 * <p>
 * - Type -> Tag
 *
 * @param <TYPE> The type of the {@link net.neoforged.neoforge.registries.tags.ITag} this {@link NormalizedSimpleStack} is for.
 *
 * @implNote This does not handle NBT on Tags.
 */
public abstract class AbstractNBTNSSTag<TYPE> extends AbstractNSSTag<TYPE> implements NSSNBT {

	@Nullable
	private final CompoundTag nbt;

	protected AbstractNBTNSSTag(@NotNull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag);
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
	}

	@Nullable
	@Override
	public CompoundTag getNBT() {
		return nbt;
	}

	@Override
	public String json() {
		String json = super.json();
		if (hasNBT()) {
			return json + nbt;
		}
		return json;
	}

	@Override
	public String toString() {
		String string = super.toString();
		if (hasNBT()) {
			return string + nbt;
		}
		return string;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		return super.equals(o) && Objects.equals(nbt, ((AbstractNBTNSSTag<?>) o).nbt);
	}

	@Override
	public int hashCode() {
		int code = super.hashCode();
		if (hasNBT()) {
			code = 31 * code + getNBT().hashCode();
		}
		return code;
	}
}