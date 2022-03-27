package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Range;

public class ItemPE extends Item {

	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();

	public ItemPE(Properties props) {
		super(props);
	}

	protected void addItemCapability(Supplier<ItemCapability<?>> capabilitySupplier) {
		supportedCapabilities.add(capabilitySupplier);
	}

	protected void addItemCapability(String modid, Supplier<Supplier<ItemCapability<?>>> capabilitySupplier) {
		if (ModList.get().isLoaded(modid)) {
			supportedCapabilities.add(capabilitySupplier.get());
		}
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

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new ItemCapabilityWrapper(stack, supportedCapabilities);
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
}