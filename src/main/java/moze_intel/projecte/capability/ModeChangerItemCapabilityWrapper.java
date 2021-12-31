package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class ModeChangerItemCapabilityWrapper extends BasicItemCapability<IModeChanger> implements IModeChanger {

	@Override
	public Capability<IModeChanger> getCapability() {
		return PECapabilities.MODE_CHANGER_ITEM_CAPABILITY;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack) {
		return getItem().getMode(stack);
	}

	@Override
	public boolean changeMode(@Nonnull Player player, @Nonnull ItemStack stack, @Nullable InteractionHand hand) {
		return getItem().changeMode(player, stack, hand);
	}
}