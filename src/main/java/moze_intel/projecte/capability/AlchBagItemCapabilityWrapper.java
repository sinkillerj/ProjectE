package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;

public class AlchBagItemCapabilityWrapper extends BasicItemCapability<IAlchBagItem> implements IAlchBagItem {

	@Override
	public Capability<IAlchBagItem> getCapability() {
		return PECapabilities.ALCH_BAG_ITEM_CAPABILITY;
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull Player player, @Nonnull ItemStack stack) {
		return getItem().updateInAlchBag(inv, player, stack);
	}
}