package moze_intel.projecte.capability;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class AlchBagItemCapabilityWrapper extends BasicItemCapability<IAlchBagItem> implements IAlchBagItem {

	@Override
	public Capability<IAlchBagItem> getCapability() {
		return PECapabilities.ALCH_BAG_ITEM_CAPABILITY;
	}

	@Override
	public boolean updateInAlchBag(@NotNull IItemHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		return getItem().updateInAlchBag(inv, player, stack);
	}
}