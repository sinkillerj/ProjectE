package moze_intel.projecte.capability;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class ItemCapabilityWrapper implements ICapabilityProvider {

	private final List<ItemCapability<?>> capabilities;
	private final ItemStack itemStack;

	public ItemCapabilityWrapper(ItemStack stack, List<ItemCapability<?>> capabilities) {
		itemStack = stack;
		this.capabilities = capabilities;
		this.capabilities.forEach(cap -> cap.wrapper = this);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		for (ItemCapability<?> cap : capabilities) {
			if (capability == cap.getCapability()) {
				return cap.getLazyCapability().cast();
			}
		}
		return LazyOptional.empty();
	}

	public static abstract class ItemCapability<TYPE> {

		private final LazyOptional<TYPE> capability = LazyOptional.of(() -> (TYPE) this);
		private ItemCapabilityWrapper wrapper;

		protected abstract Capability<TYPE> getCapability();

		protected LazyOptional<TYPE> getLazyCapability() {
			return capability;
		}

		protected ItemStack getStack() {
			return wrapper.itemStack;
		}

		protected TYPE getItem() {
			return (TYPE) getStack().getItem();
		}
	}
}