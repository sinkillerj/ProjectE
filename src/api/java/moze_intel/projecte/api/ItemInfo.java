package moze_intel.projecte.api;

import java.util.Objects;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class used for keeping track of a combined {@link Item} and {@link CompoundTag}. Unlike {@link ItemStack} this class does not keep track of count, and overrides {@link
 * #equals(Object)} and {@link #hashCode()} so that it can be used properly in a {@link java.util.Set}.
 *
 * @implNote If the {@link CompoundTag} this {@link ItemInfo} is given is empty, then it converts it to being null.
 * @apiNote {@link ItemInfo} and the data it stores is Immutable
 */
public final class ItemInfo {

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
		return new ItemInfo(item, nbt);
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
		return fromItem(stack.getItem(), stack.getTag());
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
		Item item = ForgeRegistries.ITEMS.getValue(stack.getResourceLocation());
		if (item == null) {
			return null;
		}
		return fromItem(item, stack.getNBT());
	}

	/**
	 * Reads an {@link ItemInfo} from the given {@link CompoundTag}.
	 *
	 * @param nbt {@link CompoundTag} representing a {@link ItemInfo}
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
			Item item = ForgeRegistries.ITEMS.getValue(registryName);
			if (item == null) {
				return null;
			}
			if (nbt.contains("nbt", Tag.TAG_COMPOUND)) {
				return fromItem(item, nbt.getCompound("nbt"));
			}
			return fromItem(item, null);
		}
		return null;
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
	 * @apiNote The returned {@link CompoundTag} is a copy so as to ensure that this {@link ItemInfo} is not accidentally modified via modifying the returned {@link
	 * CompoundTag}. This means it is safe to modify the returned {@link CompoundTag}
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
	public boolean is(TagKey<Item> tag) {
		return getItem().builtInRegistryHolder().is(tag);
	}

	/**
	 * @return A new {@link ItemStack} created from the stored {@link Item} and {@link CompoundTag}
	 */
	public ItemStack createStack() {
		ItemStack stack = new ItemStack(item);
		CompoundTag nbt = getNBT();
		if (nbt != null) {
			//Only set the NBT if we have some, other then allow the item to use its default NBT
			stack.setTag(nbt);
		}
		return stack;
	}

	/**
	 * Writes the item and nbt fields to a NBT object.
	 */
	public CompoundTag write(@NotNull CompoundTag nbt) {
		nbt.putString("item", item.getRegistryName().toString());
		if (this.nbt != null) {
			nbt.put("nbt", this.nbt);
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
			return item.getRegistryName() + " " + nbt;
		}
		return item.getRegistryName().toString();
	}
}