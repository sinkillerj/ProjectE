package moze_intel.projecte.api.nss;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import moze_intel.projecte.api.PEAttachments;
import moze_intel.projecte.api.codec.NSSCodecHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Item}s.
 */
public final class NSSItem extends AbstractNBTNSSTag<Item> {

	private static Registry<Item> registry() {
		try {
			return BuiltInRegistries.ITEM;
		} catch (Throwable throwable) {
			if (FMLEnvironment.production) {
				throw throwable;
			}
			//TODO: Come up with a better way to detect this, but when we are in dev if we can't initialize the registry
			// skip it and don't do the extra element is registered validation
			return null;
		}
	}

	private static final boolean ALLOW_DEFAULT = false;

	private static final Codec<String> OPTIONAL_PREFIX_CODEC = Codec.of(ExtraCodecs.NON_EMPTY_STRING, ExtraCodecs.NON_EMPTY_STRING.flatMap(str -> {
		if (str.startsWith("ITEM|")) {
			return DataResult.success(str.substring(5));
		}
		return DataResult.success(str);
	}), ExtraCodecs.NON_EMPTY_STRING + "[projecte:optionalPrefix]");

	/**
	 * Codec for encoding NSSItems to and from strings.
	 */
	public static final Codec<NSSItem> LEGACY_CODEC = createLegacyCodec(registry(), ALLOW_DEFAULT, OPTIONAL_PREFIX_CODEC, NSSItem::new);

	public static final MapCodec<NSSItem> EXPLICIT_MAP_CODEC = createExplicitCodec(registry(), ALLOW_DEFAULT, NSSItem::new);
	public static final Codec<NSSItem> EXPLICIT_CODEC = EXPLICIT_MAP_CODEC.codec();

	public static final NSSCodecHolder<NSSItem> CODECS = new NSSCodecHolder<>("ITEM", LEGACY_CODEC, EXPLICIT_CODEC);

	private NSSItem(@NotNull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemStack}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		//Skip adding any nbt that will be added by default, but make sure to include the attachments as nbt
		return createItem(stack.getItem(), PEAttachments.addAttachmentsToNbt(CraftingHelper.getTagForWriting(stack), stack.serializeAttachments()));
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemLike itemProvider) {
		return createItem(itemProvider, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link Holder} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull Holder<Item> item, @Nullable CompoundTag nbt) {
		return createItem(item.value(), nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from an {@link ItemLike} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ItemLike itemProvider, @Nullable CompoundTag nbt) {
		Item item = itemProvider.asItem();
		if (item == Items.AIR) {
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		Optional<ResourceKey<Item>> registryKey = BuiltInRegistries.ITEM.getResourceKey(item);
		if (registryKey.isEmpty()) {
			throw new IllegalArgumentException("Can't make an NSSItem with an unregistered item");
		}
		//This should never be null, or it would have crashed on being registered
		return createItem(registryKey.get().location(), nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ResourceLocation itemID) {
		return createItem(itemID, null);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing an item from a {@link ResourceLocation} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSItem createItem(@NotNull ResourceLocation itemID, @Nullable CompoundTag nbt) {
		return new NSSItem(itemID, false, nbt);
	}

	/**
	 * Helper method to create an {@link NSSItem} representing a tag from a {@link ResourceLocation}
	 */
	@NotNull
	public static NSSItem createTag(@NotNull ResourceLocation tagId) {
		return new NSSItem(tagId, true, null);
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
	protected NSSItem createNew(Item item) {
		return createItem(item);
	}

	@Override
	public NSSCodecHolder<?> codecs() {
		return CODECS;
	}
}