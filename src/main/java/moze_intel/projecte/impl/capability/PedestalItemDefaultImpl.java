package moze_intel.projecte.impl.capability;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public final class PedestalItemDefaultImpl implements IPedestalItem {

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		return Collections.emptyList();
	}
}