package moze_intel.projecte.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class used for keeping track of a combined {@link Item} and {@link DataComponentPatch}. Unlike {@link ItemStack} this class does not keep track of count, and overrides
 * {@link #equals(Object)} and {@link #hashCode()} so that it can be used properly in a {@link java.util.Set}.
 *
 * @implNote If the {@link DataComponentPatch} this {@link ItemInfo} is given is empty, then it converts it to being null.
 * @apiNote {@link ItemInfo} and the data it stores is Immutable
 */
public final class ItemInfo {

	/**
	 * MapCodec for encoding and decoding ItemInfo.
	 */
	public static final MapCodec<ItemInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(ItemInfo::getItem),
			DataComponentPatch.CODEC.optionalFieldOf("data", DataComponentPatch.EMPTY).forGetter(ItemInfo::getComponentsPatch)
	).apply(instance, ItemInfo::new));

	/**
	 * Codec for encoding and decoding ItemInfo.
	 */
	public static final Codec<ItemInfo> CODEC = MAP_CODEC.codec();

	/**
	 * Stream codec for encoding ItemInfo across the network.
	 */
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemInfo> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.holderRegistry(Registries.ITEM), ItemInfo::getItem,
			DataComponentPatch.STREAM_CODEC, ItemInfo::getComponentsPatch,
			ItemInfo::new
	);

	@NotNull
	private final Holder<Item> item;
	@NotNull
	private final DataComponentPatch componentsPatch;
	private boolean hasCachedHash;
	private int cachedHashCode;

	private ItemInfo(@NotNull Holder<Item> item, @NotNull DataComponentPatch componentsPatch) {
		this.item = item;
		this.componentsPatch = componentsPatch;
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Item} with an optional {@link DataComponentPatch} attached.
	 *
	 * @apiNote While it is not required that the item is not air, it is expected to check yourself to make sure it is not air.
	 */
	@SuppressWarnings("deprecation")
	public static ItemInfo fromItem(@NotNull ItemLike itemLike, @NotNull DataComponentPatch componentsPatch) {
		return new ItemInfo(itemLike.asItem().builtInRegistryHolder(), componentsPatch);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Holder} with an optional {@link DataComponentPatch} attached.
	 *
	 * @throws IllegalArgumentException if the holder is a Direct Holder that can't be resolved as a reference holder from the item registry.
	 * @apiNote While it is not required that the holder does not represent air, it is expected to check yourself to make sure it is not air.
	 */
	public static ItemInfo fromItem(@NotNull Holder<Item> holder, @NotNull DataComponentPatch componentsPatch) {
		if (holder.kind() == Holder.Kind.DIRECT) {
			if (!holder.isBound()) {//This should always be true, unless someone made a custom direct holder for some reason
				throw new IllegalArgumentException("ItemInfo does not support unbound direct holders.");
			}
			holder = BuiltInRegistries.ITEM.wrapAsHolder(holder.value());
			if (holder.kind() == Holder.Kind.DIRECT) {
				throw new IllegalArgumentException("ItemInfo does not support direct holders for unregistered items.");
			}
		}
		return new ItemInfo(holder, componentsPatch);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Item} with no {@link DataComponentPatch} attached.
	 *
	 * @apiNote While it is not required that the item is not air, it is expected to check yourself to make sure it is not air.
	 */
	@SuppressWarnings("deprecation")
	public static ItemInfo fromItem(@NotNull ItemLike itemLike) {
		return fromItem(itemLike.asItem().builtInRegistryHolder(), DataComponentPatch.EMPTY);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Holder} with no {@link DataComponentPatch} attached.
	 *
	 * @throws IllegalArgumentException if the holder is a Direct Holder
	 * @apiNote While it is not required that the holder does not represent air, it is expected to check yourself to make sure it is not air.
	 */
	public static ItemInfo fromItem(@NotNull Holder<Item> holder) {
		return fromItem(holder, DataComponentPatch.EMPTY);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link ItemStack}.
	 *
	 * @apiNote While it is not required that the stack is not empty, it is expected to check yourself to make sure it is not empty.
	 */
	public static ItemInfo fromStack(@NotNull ItemStack stack) {
		return new ItemInfo(stack.typeHolder(), stack.getComponentsPatch());
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link NSSItem}.
	 *
	 * @return An {@link ItemInfo} object from a given {@link NSSItem}, or null if the given {@link NSSItem} represents a tag or the item it represents is not registered
	 */
	@Nullable
	public static ItemInfo fromNSS(@NotNull NSSItem stack) {
		if (stack.representsTag()) {
			return null;
		}
		Optional<Holder.Reference<Item>> holder = BuiltInRegistries.ITEM.get(stack.getResourceLocation());
		//noinspection OptionalIsPresent - Capturing lambda
		if (holder.isEmpty()) {
			return null;
		}
		return fromItem(holder.get(), stack.getComponentsPatch());
	}

	/**
	 * Creates an {@link NSSItem} object from this item info.
	 */
	public NSSItem toNSS() {
		return NSSItem.createItem(item, componentsPatch);
	}

	/**
	 * @return The {@link Holder} that represents the item stored by this {@link ItemInfo}.
	 */
	@NotNull
	public Holder<Item> getItem() {
		return item;
	}

	/**
	 * @return The {@link DataComponentPatch} stored in this {@link ItemInfo}, or {@link DataComponentPatch#EMPTY} if there is no custom data stored.
	 */
	@NotNull
	public DataComponentPatch getComponentsPatch() {
		return componentsPatch;
	}

	/**
	 * @return true if this {@link ItemInfo} has any components modified, added, or removed from the default components for the item.
	 */
	public boolean hasModifiedComponents() {
		return !componentsPatch.isEmpty();
	}

	/**
	 * Gets the value stored in the component patch for the given component type, or null if there is no stored value for said type.
	 *
	 * @apiNote This might return null if the item has the component as a default component and the component is at that default value.
	 */
	@Nullable
	public <T> T getOrNull(DataComponentType<? extends T> type) {
		return componentsPatch.get(item.value().components(), type);
	}

	/**
	 * Gets a version of this ItemInfo without any of the data component changes.
	 */
	public ItemInfo itemOnly() {
		return componentsPatch.isEmpty() ? this : fromItem(item);
	}

	/**
	 * @return A new {@link ItemStack} created from the stored {@link Item} and {@link DataComponentPatch}
	 */
	public ItemStack createStack() {
		return new ItemStack(getItem(), 1, getComponentsPatch());
	}

	@Override
	public int hashCode() {
		if (!hasCachedHash) {
			hasCachedHash = true;
			ResourceKey<Item> resourceKey = item.getKey();
			int code = resourceKey == null ? 0 : resourceKey.hashCode();
			cachedHashCode = 31 * code + componentsPatch.hashCode();
		}
		return cachedHashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ItemInfo other) {
			return item.is(other.item) && componentsPatch.equals(other.componentsPatch);
		}
		return false;
	}

	@Override
	public String toString() {
		if (componentsPatch.isEmpty()) {
			return item.getRegisteredName();
		}
		//Note: We display the component patch using the more readable string representation rather than the command representation
		// as this is mostly for debug purposes
		return item.getRegisteredName() + " " + componentsPatch;
	}
}
