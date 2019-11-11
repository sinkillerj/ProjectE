package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

public class ItemPE extends Item {

	protected static final ResourceLocation ACTIVE_NAME = new ResourceLocation(PECore.MODID, "active");
	protected static final IItemPropertyGetter ACTIVE_GETTER = (stack, world, entity) -> stack.hasTag() && stack.getTag().getBoolean(Constants.NBT_KEY_ACTIVE) ? 1F : 0F;
	protected static final IItemPropertyGetter MODE_GETTER = (stack, world, entity) -> stack.hasTag() ? stack.getTag().getInt(Constants.NBT_KEY_MODE) : 0F;

	private final List<ItemCapability<?>> supportedCapabilities = new ArrayList<>();

	public ItemPE(Properties props) {
		super(props);
	}

	protected <TYPE> void addItemCapability(ItemCapability<TYPE> capability) {
		supportedCapabilities.add(capability);
	}

	protected <TYPE> void addItemCapability(String modid, Supplier<Supplier<ItemCapability<TYPE>>> capabilitySupplier) {
		if (ModList.get().isLoaded(modid)) {
			supportedCapabilities.add(capabilitySupplier.get().get());
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (oldStack.getItem() != newStack.getItem()) {
			return true;
		}
		if (oldStack.hasTag() && newStack.hasTag()) {
			CompoundNBT newTag = newStack.getTag();
			CompoundNBT oldTag = oldStack.getTag();
			boolean diffActive = oldTag.contains(Constants.NBT_KEY_ACTIVE) && newTag.contains(Constants.NBT_KEY_ACTIVE)
								 && !oldTag.get(Constants.NBT_KEY_ACTIVE).equals(newTag.get(Constants.NBT_KEY_ACTIVE));
			boolean diffMode = oldTag.contains(Constants.NBT_KEY_MODE) && newTag.contains(Constants.NBT_KEY_MODE)
							   && !oldTag.get(Constants.NBT_KEY_MODE).equals(newTag.get(Constants.NBT_KEY_MODE));
			return diffActive || diffMode;
		}
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new ItemCapabilityWrapper(stack, supportedCapabilities);
	}

	public static long getEmc(ItemStack stack) {
		return stack.getOrCreateTag().getLong(Constants.NBT_KEY_STORED_EMC);
	}

	public static void setEmc(ItemStack stack, long amount) {
		stack.getOrCreateTag().putLong(Constants.NBT_KEY_STORED_EMC, amount);
	}

	public static void addEmcToStack(ItemStack stack, long amount) {
		setEmc(stack, getEmc(stack) + amount);
	}

	public static void removeEmc(ItemStack stack, long amount) {
		long result = getEmc(stack) - amount;
		if (result < 0) {
			result = 0;
		}
		setEmc(stack, result);
	}

	public static boolean consumeFuel(PlayerEntity player, ItemStack stack, long amount, boolean shouldRemove) {
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