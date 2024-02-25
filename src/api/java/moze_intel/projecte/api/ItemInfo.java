package moze_intel.projecte.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class used for keeping track of a combined {@link Item} and {@link CompoundTag}. Unlike {@link ItemStack} this class does not keep track of count, and overrides
 * {@link #equals(Object)} and {@link #hashCode()} so that it can be used properly in a {@link java.util.Set}.
 *
 * @implNote If the {@link CompoundTag} this {@link ItemInfo} is given is empty, then it converts it to being null.
 * @apiNote {@link ItemInfo} and the data it stores is Immutable
 */
public final class ItemInfo {

	/**
	 * Codec for encoding ItemInfo to and from strings.
	 */
	public static final Codec<ItemInfo> LEGACY_CODEC = IPECodecHelper.INSTANCE.validatePresent(
			NSSItem.LEGACY_CODEC.xmap(ItemInfo::fromNSS, itemInfo -> NSSItem.createItem(itemInfo.getItem(), itemInfo.getNBT())),
			() -> "ItemInfo does not support tags or missing items"
	);

	public static final Codec<ItemInfo> EXPLICIT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemInfo::getItem),
			CraftingHelper.TAG_CODEC.optionalFieldOf("nbt").forGetter(itemInfo -> Optional.ofNullable(itemInfo.getNBT()))
	).apply(instance, (item, nbt) -> new ItemInfo(item, nbt.orElse(null))));

	@NotNull
	private final Item item;
	@Nullable
	private final CompoundTag nbt;

	private ItemInfo(@NotNull ItemLike item, @Nullable CompoundTag nbt) {
		this.item = item.asItem();
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Item} with an optional {@link CompoundTag} attached.
	 *
	 * @apiNote While it is not required that the item is not air, it is expected to check yourself to make sure it is not air.
	 */
	public static ItemInfo fromItem(@NotNull ItemLike item, @Nullable CompoundTag nbt) {
		return new ItemInfo(item, nbt == null ? null : nbt.copy());
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Item} with no {@link CompoundTag} attached.
	 *
	 * @apiNote While it is not required that the item is not air, it is expected to check yourself to make sure it is not air.
	 */
	public static ItemInfo fromItem(@NotNull ItemLike item) {
		return fromItem(item, null);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link ItemStack}.
	 *
	 * @apiNote While it is not required that the stack is not empty, it is expected to check yourself to make sure it is not empty.
	 */
	public static ItemInfo fromStack(@NotNull ItemStack stack) {
		return new ItemInfo(stack.getItem(), PEAttachments.addAttachmentsToNbt(stack.getTag(), stack.serializeAttachments()));
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
		return BuiltInRegistries.ITEM.getOptional(stack.getResourceLocation())
				.map(item -> fromItem(item, stack.getNBT()))
				.orElse(null);
	}

	/**
	 * Reads an {@link ItemInfo} from the given {@link CompoundTag}.
	 *
	 * @param nbt {@link CompoundTag} representing an {@link ItemInfo}
	 *
	 * @return An {@link ItemInfo} that is represented by the given {@link CompoundTag}, or null if no {@link ItemInfo} is stored or the item is not registered.
	 */
	@Nullable
	public static ItemInfo read(@NotNull CompoundTag nbt) {
		if (nbt.contains("item", Tag.TAG_STRING)) {
			ResourceLocation registryName = ResourceLocation.tryParse(nbt.getString("item"));
			if (registryName == null) {
				return null;
			}
			return BuiltInRegistries.ITEM.getOptional(registryName).map(item -> {
				if (nbt.contains("nbt", Tag.TAG_COMPOUND)) {
					return fromItem(item, nbt.getCompound("nbt"));
				}
				return fromItem(item);
			}).orElse(null);
		}
		return null;
	}

	/**
	 * Reads an {@link ItemInfo} from the given {@link FriendlyByteBuf}.
	 *
	 * @param buffer {@link FriendlyByteBuf} containing an {@link ItemInfo}
	 *
	 * @return An {@link ItemInfo} that is contained by the given {@link FriendlyByteBuf}.
	 */
	public static ItemInfo read(@NotNull FriendlyByteBuf buffer) {
		return new ItemInfo(buffer.readById(BuiltInRegistries.ITEM), buffer.readNbt());
	}

	/**
	 * Writes the item and nbt to a {@link FriendlyByteBuf}.
	 */
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeId(BuiltInRegistries.ITEM, getItem());
		buffer.writeNbt(this.nbt);
	}

	/**
	 * @return The {@link Item} stored in this {@link ItemInfo}.
	 */
	@NotNull
	public Item getItem() {
		return item;
	}

	/**
	 * @return The {@link CompoundTag} stored in this {@link ItemInfo}, or null if there is no nbt data stored.
	 *
	 * @apiNote The returned {@link CompoundTag} is a copy so as to ensure that this {@link ItemInfo} is not accidentally modified via modifying the returned
	 * {@link CompoundTag}. This means it is safe to modify the returned {@link CompoundTag}
	 */
	@Nullable
	public CompoundTag getNBT() {
		return nbt == null ? null : nbt.copy();
	}

	/**
	 * Checks if this {@link ItemInfo} has an associated {@link CompoundTag}.
	 *
	 * @return True if this {@link ItemInfo} has an associated {@link CompoundTag}, false otherwise.
	 */
	public boolean hasNBT() {
		return nbt != null;
	}

	/**
	 * Checks if the item backing this {@link ItemInfo} is contained in the given tag.
	 *
	 * @param tag Tag to check.
	 *
	 * @return True if it is contained.
	 */
	@SuppressWarnings("deprecation")
	public boolean is(TagKey<Item> tag) {
		return getItem().builtInRegistryHolder().is(tag);
	}

	/**
	 * @return A new {@link ItemStack} created from the stored {@link Item} and {@link CompoundTag}
	 */
	public ItemStack createStack() {
		CompoundTag nbt = getNBT();
		CompoundTag attachmentNbt = null;
		if (nbt != null && nbt.contains(AttachmentHolder.ATTACHMENTS_NBT_KEY, Tag.TAG_COMPOUND)) {
			//Note: getNBT returns a copy of the stored nbt, so we don't have to copy it or the attachment sub-compound
			attachmentNbt = nbt.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY);
			if (nbt.size() > 1) {
				nbt.remove(AttachmentHolder.ATTACHMENTS_NBT_KEY);
			} else {
				nbt = null;
			}
		}
		ItemStack stack = new ItemStack(item, 1, attachmentNbt);
		if (nbt != null) {
			//Only set the NBT if we have some, other than allowing the item to use its default NBT
			stack.setTag(nbt);
		}
		return stack;
	}

	/**
	 * Writes the item and nbt fields to a NBT object.
	 */
	public CompoundTag write(@NotNull CompoundTag nbt) {
		nbt.putString("item", getRegistryName().toString());
		if (this.nbt != null) {
			nbt.put("nbt", this.nbt.copy());
		}
		return nbt;
	}

	@Override
	public int hashCode() {
		int code = item.hashCode();
		if (nbt != null) {
			code = 31 * code + nbt.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ItemInfo other) {
			return item == other.item && Objects.equals(nbt, other.nbt);
		}
		return false;
	}

	@Override
	public String toString() {
		if (nbt != null) {
			return getRegistryName() + " " + nbt;
		}
		return getRegistryName().toString();
	}

	private ResourceLocation getRegistryName() {
		return BuiltInRegistries.ITEM.getKey(item);
	}
}