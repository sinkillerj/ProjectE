package moze_intel.projecte.capability;

import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class PedestalItemCapabilityWrapper extends BasicItemCapability<IPedestalItem> implements IPedestalItem {

	@Override
	public Capability<IPedestalItem> getCapability() {
		return ProjectEAPI.PEDESTAL_ITEM_CAPABILITY;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		getItem().updateInPedestal(world, pos);
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		return getItem().getPedestalDescription();
	}
}