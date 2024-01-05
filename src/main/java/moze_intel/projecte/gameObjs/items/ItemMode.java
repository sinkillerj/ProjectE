package moze_intel.projecte.gameObjs.items;

import java.util.List;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemMode extends ItemPE implements IItemMode, IItemCharge, IBarHelper {

	private final int numCharge;
	private final ILangEntry[] modes;

	public ItemMode(Properties props, int numCharge, ILangEntry... modeDescrp) {
		super(props);
		this.numCharge = numCharge;
		this.modes = modeDescrp;
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		return 1 - getChargePercent(stack);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getColorForBar(stack);
	}

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return numCharge;
	}
}