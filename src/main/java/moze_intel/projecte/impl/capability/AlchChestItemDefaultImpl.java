package moze_intel.projecte.impl.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class AlchChestItemDefaultImpl implements IAlchChestItem {

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
	}
}