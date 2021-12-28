package moze_intel.projecte.api.nss;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

/**
 * Abstract implementation to make implementing {@link NSSTag} and {@link NSSNBT} simpler, and automatically be able to register conversions for:
 *
 * - Tag -> Type
 *
 * - Type -> Tag
 *
 * @param <TYPE> The type of the {@link Tag} this {@link NormalizedSimpleStack} is for.
 *
 * @implNote This does not handle NBT on Tags.
 */
public abstract class AbstractNBTNSSTag<TYPE> extends AbstractNSSTag<TYPE> implements NSSNBT {

	@Nullable
	private final CompoundTag nbt;

	protected AbstractNBTNSSTag(@Nonnull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
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