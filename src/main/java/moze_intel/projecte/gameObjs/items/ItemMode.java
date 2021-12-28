package moze_intel.projecte.gameObjs.items;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class ItemMode extends ItemPE implements IItemMode, IItemCharge {

	private final int numCharge;
	private final ILangEntry[] modes;

	public ItemMode(Properties props, int numCharge, ILangEntry... modeDescrp) {
		super(props);
		this.numCharge = numCharge;
		this.modes = modeDescrp;
		addItemCapability(ChargeItemCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(@Nonnull ItemStack stack) {
		return Math.round(13.0F - 13.0F * (float) (1.0D - getChargePercent(stack)));
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return numCharge;
	}
}