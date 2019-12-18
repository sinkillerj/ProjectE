package moze_intel.projecte.gameObjs.items.rings;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;

public abstract class PEToggleItem extends ItemPE implements IModeChanger {

	public PEToggleItem(Properties props) {
		super(props);
		this.addPropertyOverride(ACTIVE_NAME, ACTIVE_GETTER);
		addItemCapability(new ModeChangerItemCapabilityWrapper());
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean(Constants.NBT_KEY_ACTIVE) ? (byte) 1 : 0;
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		CompoundNBT nbt = stack.getOrCreateTag();
		boolean isActive = nbt.getBoolean(Constants.NBT_KEY_ACTIVE);
		player.getEntityWorld().playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), isActive ? PESounds.UNCHARGE : PESounds.HEAL, SoundCategory.PLAYERS, 1.0F, 1.0F);
		nbt.putBoolean(Constants.NBT_KEY_ACTIVE, !isActive);
		return true;
	}
}