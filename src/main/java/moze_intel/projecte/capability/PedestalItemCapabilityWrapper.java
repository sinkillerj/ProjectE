package moze_intel.projecte.capability;

import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;

public class PedestalItemCapabilityWrapper extends BasicItemCapability<IPedestalItem> implements IPedestalItem {

	@Override
	public Capability<IPedestalItem> getCapability() {
		return ProjectEAPI.PEDESTAL_ITEM_CAPABILITY;
	}

	@Override
	public void updateInPedestal(@Nonnull Level world, @Nonnull BlockPos pos) {
		getItem().updateInPedestal(world, pos);
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		return getItem().getPedestalDescription();
	}
}