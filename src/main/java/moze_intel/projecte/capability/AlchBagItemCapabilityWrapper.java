package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;

public class AlchBagItemCapabilityWrapper extends ItemCapability<IAlchBagItem> implements IAlchBagItem {

	@Override
	protected Capability<IAlchBagItem> getCapability() {
		return ProjectEAPI.ALCH_BAG_ITEM_CAPABILITY;
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull PlayerEntity player, @Nonnull ItemStack stack) {
		return getItem().updateInAlchBag(inv, player, stack);
	}
}