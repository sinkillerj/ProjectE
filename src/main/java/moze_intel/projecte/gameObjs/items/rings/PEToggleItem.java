package moze_intel.projecte.gameObjs.items.rings;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class PEToggleItem extends ItemPE implements IModeChanger {

	public PEToggleItem(Properties props) {
		super(props);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Override
	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack) {
		return ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE) ? (byte) 1 : 0;
	}

	@Override
	public boolean changeMode(@Nonnull Player player, @Nonnull ItemStack stack, InteractionHand hand) {
		CompoundTag nbt = stack.getOrCreateTag();
		boolean isActive = nbt.getBoolean(Constants.NBT_KEY_ACTIVE);
		player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), isActive ? PESoundEvents.UNCHARGE.get() : PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		nbt.putBoolean(Constants.NBT_KEY_ACTIVE, !isActive);
		return true;
	}
}