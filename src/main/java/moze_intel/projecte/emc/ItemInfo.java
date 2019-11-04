package moze_intel.projecte.emc;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraftforge.registries.ForgeRegistries;

//TODO: Document
public class ItemInfo {

	@Nonnull
	private final Item item;
	@Nullable
	private final CompoundNBT nbt;

	//TODO: Replace with a static fromStack method
	public ItemInfo(ItemStack stack) {
		this(stack.getItem(), stack.getTag());
	}

	public ItemInfo(@Nonnull Item item, @Nullable CompoundNBT nbt) {
		this.item = item;
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
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
		return new ItemInfo(item, stack.getNBT());
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

	//Version of PotionUtils.addPotionToItemStack except without boxing ItemInfo into an out of an ItemStack
	public ItemInfo makeWithPotion(Potion potion) {
		CompoundNBT nbt = this.nbt == null ? null : this.nbt.copy();
		if (potion == Potions.EMPTY) {
			if (nbt != null && nbt.contains("Potion")) {
				nbt.remove("Potion");
				if (nbt.isEmpty()) {
					nbt = null;
				}
			}
		} else {
			if (nbt == null) {
				nbt = new CompoundNBT();
			}
			nbt.putString("Potion", potion.getRegistryName().toString());
		}
		return new ItemInfo(item, nbt);
	}
}