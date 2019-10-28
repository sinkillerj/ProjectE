package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class AlchChestItemCapabilityWrapper extends ItemCapability<IAlchChestItem> implements IAlchChestItem {

	@Override
	protected Capability<IAlchChestItem> getCapability() {
		return ProjectEAPI.ALCH_CHEST_ITEM_CAPABILITY;
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
		getItem().updateInAlchChest(world, pos, stack);
	}
}