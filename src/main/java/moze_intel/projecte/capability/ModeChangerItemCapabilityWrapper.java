package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

public class ModeChangerItemCapabilityWrapper extends BasicItemCapability<IModeChanger> implements IModeChanger {

	@Override
	public Capability<IModeChanger> getCapability() {
		return ProjectEAPI.MODE_CHANGER_ITEM_CAPABILITY;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack) {
		return getItem().getMode(stack);
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, @Nullable Hand hand) {
		return getItem().changeMode(player, stack, hand);
	}
}