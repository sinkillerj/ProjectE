package moze_intel.projecte.capability;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;

public class AlchChestItemCapabilityWrapper extends BasicItemCapability<IAlchChestItem> implements IAlchChestItem {

	@Override
	public Capability<IAlchChestItem> getCapability() {
		return PECapabilities.ALCH_CHEST_ITEM_CAPABILITY;
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		return getItem().updateInAlchChest(level, pos, stack);
	}
}