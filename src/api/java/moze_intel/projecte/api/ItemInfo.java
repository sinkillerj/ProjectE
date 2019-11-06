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

//TODO: Document, and figure out a good spot for this
public class ItemInfo {

	@Nonnull
	private final Item item;
	@Nullable
	private final CompoundNBT nbt;

	private ItemInfo(@Nonnull Item item, @Nullable CompoundNBT nbt) {
		this.item = item;
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
	}

	public static ItemInfo fromItem(@Nonnull Item item, @Nullable CompoundNBT nbt) {
		return new ItemInfo(item, nbt);
	}

	public static ItemInfo fromItem(@Nonnull Item item) {
		return fromItem(item, null);
	}

	//TODO: Nullable if empty??
	public static ItemInfo fromStack(@Nonnull ItemStack stack) {
		return fromItem(stack.getItem(), stack.getTag());
	}

	@Nullable
	public static ItemInfo fromNSS(NSSItem stack) {
		if (stack.representsTag()) {
			return null;
		}
		Item item = ForgeRegistries.ITEMS.getValue(stack.getResourceLocation());
		if (item == null) {
			return null;
		}
		return fromItem(item, stack.getNBT());
	}

	@Nullable
	public static ItemInfo read(CompoundNBT nbt) {
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

	@Nonnull
	public Item getItem() {
		return item;
	}

	@Nullable
	public CompoundNBT getNBT() {
		return nbt;
	}

	public ItemStack createStack() {
		ItemStack stack = new ItemStack(item);
		stack.setTag(nbt);
		return stack;
	}

	public CompoundNBT write(CompoundNBT nbt) {
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