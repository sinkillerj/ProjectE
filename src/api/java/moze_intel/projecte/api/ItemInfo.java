package moze_intel.projecte.api;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Class used for keeping track of a combined {@link Item} and {@link CompoundNBT}. Unlike {@link ItemStack} this class does not keep track of count, and overrides {@link
 * #equals(Object)} and {@link #hashCode()} so that it can be used properly in a {@link java.util.Set}.
 *
 * @implNote If the {@link CompoundNBT} this {@link ItemInfo} is given is empty, then it converts it to being null.
 * @apiNote {@link ItemInfo} and the data it stores is Immutable
 */
public final class ItemInfo {

	@Nonnull
	private final Item item;
	@Nullable
	private final CompoundNBT nbt;

	private ItemInfo(@Nonnull Item item, @Nullable CompoundNBT nbt) {
		this.item = item;
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Item} with an optional {@link CompoundNBT} attached.
	 *
	 * @apiNote While it is not required that the item is not air, it is expected to check yourself to make sure it is not air.
	 */
	public static ItemInfo fromItem(@Nonnull Item item, @Nullable CompoundNBT nbt) {
		return new ItemInfo(item, nbt);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link Item} with no {@link CompoundNBT} attached.
	 *
	 * @apiNote While it is not required that the item is not air, it is expected to check yourself to make sure it is not air.
	 */
	public static ItemInfo fromItem(@Nonnull Item item) {
		return fromItem(item, null);
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link ItemStack}.
	 *
	 * @apiNote While it is not required that the stack is not empty, it is expected to check yourself to make sure it is not empty.
	 */
	public static ItemInfo fromStack(@Nonnull ItemStack stack) {
		return fromItem(stack.getItem(), stack.getTag());
	}

	/**
	 * Creates an {@link ItemInfo} object from a given {@link NSSItem}.
	 *
	 * @return An {@link ItemInfo} object from a given {@link NSSItem}, or null if the given {@link NSSItem} represents a tag or the item it represents is not registered
	 */
	@Nullable
	public static ItemInfo fromNSS(@Nonnull NSSItem stack) {
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
	 * Reads an {@link ItemInfo} from the given {@link CompoundNBT}.
	 *
	 * @param nbt {@link CompoundNBT} representing a {@link ItemInfo}
	 *
	 * @return An {@link ItemInfo} that is represented by the given {@link CompoundNBT}, or null if no {@link ItemInfo} is stored or the item is not registered.
	 */
	@Nullable
	public static ItemInfo read(@Nonnull CompoundNBT nbt) {
		if (nbt.contains("item", NBT.TAG_STRING)) {
			ResourceLocation registryName = ResourceLocation.tryCreate(nbt.getString("item"));
			if (registryName == null) {
				return null;
			}
			Item item = ForgeRegistries.ITEMS.getValue(registryName);
			if (item == null) {
				return null;
			}
			if (nbt.contains("nbt", NBT.TAG_COMPOUND)) {
				return fromItem(item, nbt.getCompound("nbt"));
			}
			return fromItem(item, null);
		}
		return null;
	}

	/**
	 * @return The {@link Item} stored in this {@link ItemInfo}.
	 */
	@Nonnull
	public Item getItem() {
		return item;
	}

	/**
	 * @return The {@link CompoundNBT} stored in this {@link ItemInfo}, or null if there is no nbt data stored.
	 *
	 * @apiNote The returned {@link CompoundNBT} is a copy so as to ensure that this {@link ItemInfo} is not accidentally modified via modifying the returned {@link
	 * CompoundNBT}. This means it is safe to modify the returned {@link CompoundNBT}
	 */
	@Nullable
	public CompoundNBT getNBT() {
		return nbt == null ? null : nbt.copy();
	}

	/**
	 * Checks if this {@link ItemInfo} has an associated {@link CompoundNBT}.
	 *
	 * @return True if this {@link ItemInfo} has an associated {@link CompoundNBT}, false otherwise.
	 */
	public boolean hasNBT() {
		return nbt != null;
	}

	/**
	 * @return A new {@link ItemStack} created from the stored {@link Item} and {@link CompoundNBT}
	 */
	public ItemStack createStack() {
		ItemStack stack = new ItemStack(item);
		stack.setTag(getNBT());
		return stack;
	}

	/**
	 * Writes the item and nbt fields to a NBT object.
	 */
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
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
		}
		if (o instanceof ItemInfo) {
			ItemInfo other = (ItemInfo) o;
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