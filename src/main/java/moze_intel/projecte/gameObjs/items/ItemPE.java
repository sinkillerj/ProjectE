package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Range;

public class ItemPE extends Item {

	public ItemPE(Properties props) {
		super(props);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (oldStack.getItem() != newStack.getItem()) {
			return true;
		}
		if (oldStack.hasTag() && newStack.hasTag()) {
			CompoundTag newTag = newStack.getOrCreateTag();
			CompoundTag oldTag = oldStack.getOrCreateTag();
			boolean diffActive = oldTag.contains(Constants.NBT_KEY_ACTIVE) && newTag.contains(Constants.NBT_KEY_ACTIVE)
								 && !oldTag.get(Constants.NBT_KEY_ACTIVE).equals(newTag.get(Constants.NBT_KEY_ACTIVE));
			boolean diffMode = oldTag.contains(Constants.NBT_KEY_MODE) && newTag.contains(Constants.NBT_KEY_MODE)
							   && !oldTag.get(Constants.NBT_KEY_MODE).equals(newTag.get(Constants.NBT_KEY_MODE));
			return diffActive || diffMode;
		}
		return false;
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	public static long getEmc(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getLong(Constants.NBT_KEY_STORED_EMC) : 0;
	}

	public static void setEmc(ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
		setEmc(stack.getOrCreateTag(), amount);
	}

	public static void setEmc(CompoundTag nbt, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
		nbt.putLong(Constants.NBT_KEY_STORED_EMC, amount);
	}

	public static void addEmcToStack(ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
		if (amount > 0) {
			setEmc(stack, getEmc(stack) + amount);
		}
	}

	public static void removeEmc(ItemStack stack, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
		if (amount > 0) {
			setEmc(stack, Math.max(getEmc(stack) - amount, 0));
		}
	}

	public static boolean consumeFuel(Player player, ItemStack stack, long amount, boolean shouldRemove) {
		if (amount <= 0) {
			return true;
		}
		long current = getEmc(stack);
		if (current < amount) {
			long consume = EMCHelper.consumePlayerFuel(player, amount - current);
			if (consume == -1) {
				return false;
			}
			addEmcToStack(stack, consume);
		}
		if (shouldRemove) {
			removeEmc(stack, amount);
		}
		return true;
	}

	public static boolean hotBarOrOffHand(int slot) {
		return slot < Inventory.getSelectionSize() || slot == Inventory.SLOT_OFFHAND;
	}
}