package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

public class ExtraFunctionItemCapabilityWrapper extends ItemCapability<IExtraFunction> implements IExtraFunction {

	@Override
	protected Capability<IExtraFunction> getCapability() {
		return ProjectEAPI.EXTRA_FUNCTION_ITEM_CAPABILITY;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nullable Hand hand) {
		return getItem().doExtraFunction(stack, player, hand);
	}
}