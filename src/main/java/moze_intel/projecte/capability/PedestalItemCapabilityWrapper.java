package moze_intel.projecte.capability;

import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;

public class PedestalItemCapabilityWrapper extends BasicItemCapability<IPedestalItem> implements IPedestalItem {

	@Override
	public Capability<IPedestalItem> getCapability() {
		return ProjectEAPI.PEDESTAL_ITEM_CAPABILITY;
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull BlockPos pos,
			@Nonnull PEDESTAL pedestal) {
		return getItem().updateInPedestal(stack, level, pos, pedestal);
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		return getItem().getPedestalDescription();
	}
}