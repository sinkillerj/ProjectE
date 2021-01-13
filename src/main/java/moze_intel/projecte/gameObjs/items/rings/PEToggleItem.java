package moze_intel.projecte.gameObjs.items.rings;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;

public abstract class PEToggleItem extends ItemPE implements IModeChanger {

	public PEToggleItem(Properties props) {
		super(props);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack) {
		return ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE) ? (byte) 1 : 0;
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		CompoundNBT nbt = stack.getOrCreateTag();
		boolean isActive = nbt.getBoolean(Constants.NBT_KEY_ACTIVE);
		player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), isActive ? PESoundEvents.UNCHARGE.get() : PESoundEvents.HEAL.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
		nbt.putBoolean(Constants.NBT_KEY_ACTIVE, !isActive);
		return true;
	}
}