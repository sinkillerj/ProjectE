package moze_intel.projecte.capability;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtraFunctionItemCapabilityWrapper extends BasicItemCapability<IExtraFunction> implements IExtraFunction {

	@Override
	public Capability<IExtraFunction> getCapability() {
		return PECapabilities.EXTRA_FUNCTION_ITEM_CAPABILITY;
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand) {
		return getItem().doExtraFunction(stack, player, hand);
	}
}