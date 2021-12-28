package moze_intel.projecte.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class ItemCapability<TYPE> {

	private ItemCapabilityWrapper wrapper;

	/**
	 * @apiNote Should only be used by {@link ItemCapabilityWrapper}
	 */
	public void setWrapper(ItemCapabilityWrapper wrapper) {
		if (this.wrapper == null) {
			this.wrapper = wrapper;
		}
	}

	public abstract Capability<TYPE> getCapability();

	public abstract LazyOptional<TYPE> getLazyCapability();

	protected ItemStack getStack() {
		return wrapper.getItemStack();
	}

	protected TYPE getItem() {
		return (TYPE) getStack().getItem();
	}
}