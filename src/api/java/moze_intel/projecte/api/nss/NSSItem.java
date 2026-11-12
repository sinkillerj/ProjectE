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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Item}s.
 */
public final class NSSItem extends AbstractDataComponentHolderNSSTag<Item> {

	private static final ResourceKey<Item> DEFAULT_KEY = ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getDefaultKey());
	public static final MapCodec<NSSItem> CODEC = createCodec(BuiltInRegistries.ITEM, false, NSSItem::new);

	private NSSItem(@NotNull Identifier resourceLocation, boolean isTag, @NotNull DataComponentPatch componentsPatch) {
		super(resourceLocation, isTag, componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemStack}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		return createItem(stack.typeHolder(), stack.getComponentsPatch());
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemLike itemProvider) {
		return createItem(itemProvider, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link Holder} with no data components.
	 */
	@NotNull
	public static NSSItem createItem(@NotNull Holder<Item> item) {
		return createItem(item, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link Holder} and an optional {@link DataComponentPatch}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull Holder<Item> itemHolder, @NotNull DataComponentPatch componentsPatch) {
		ResourceKey<Item> key = itemHolder.getKey();
		if (key == null) {
			if (!itemHolder.isBound()) {
				throw new IllegalArgumentException("Can't make an NSSItem with an unbound direct holder");
			}
			Optional<ResourceKey<Item>> registryKey = BuiltInRegistries.ITEM.getResourceKey(itemHolder.value());
			if (registryKey.isEmpty()) {
				throw new IllegalArgumentException("Can't make an NSSItem with an unregistered item");
			}
			key = registryKey.get();
		}
		if (key == DEFAULT_KEY) {
			throw new IllegalArgumentException("Can't make NSSItem with an empty stack");
		}
		//This should never be null, or it would have crashed on being registered
		return createItem(key.identifier(), componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike} and an optional {@link DataComponentPatch}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemLike itemProvider, @NotNull DataComponentPatch componentsPatch) {
		return createItem(itemProvider.asItem().builtInRegistryHolder(), componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link Identifier}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull Identifier itemID) {
		return createItem(itemID, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceKey}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ResourceKey<Item> itemID) {
		return createItem(itemID.identifier());
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link Identifier} and an optional {@link DataComponentPatch}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull Identifier itemID, @NotNull DataComponentPatch componentsPatch) {
		return new NSSItem(itemID, false, componentsPatch);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link Identifier}
	 */
	@NotNull
	public static NSSItem createTag(@NotNull Identifier tagId) {
		return new NSSItem(tagId, true, DataComponentPatch.EMPTY);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link TagKey<Item>}
	 */
	@NotNull
	public static NSSItem createTag(@NotNull TagKey<Item> tag) {
		return createTag(tag.location());
	}

	@NotNull
	@Override
	protected Registry<Item> getRegistry() {
		return BuiltInRegistries.ITEM;
	}

	@Override
	protected NSSItem createNew(Holder<Item> item) {
		return createItem(item);
	}

	@Override
	public MapCodec<NSSItem> codec() {
		return CODEC;
	}
}
