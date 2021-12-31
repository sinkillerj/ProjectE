package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class ExtraFunctionItemCapabilityWrapper extends BasicItemCapability<IExtraFunction> implements IExtraFunction {

	@Override
	public Capability<IExtraFunction> getCapability() {
		return PECapabilities.EXTRA_FUNCTION_ITEM_CAPABILITY;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull Player player, @Nullable InteractionHand hand) {
		return getItem().doExtraFunction(stack, player, hand);
	}
}