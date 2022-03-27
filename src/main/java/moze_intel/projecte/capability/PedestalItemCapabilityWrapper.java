package moze_intel.projecte.capability;

import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;

public class PedestalItemCapabilityWrapper extends BasicItemCapability<IPedestalItem> implements IPedestalItem {

	@Override
	public Capability<IPedestalItem> getCapability() {
		return PECapabilities.PEDESTAL_ITEM_CAPABILITY;
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		return getItem().updateInPedestal(stack, level, pos, pedestal);
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription() {
		return getItem().getPedestalDescription();
	}
}