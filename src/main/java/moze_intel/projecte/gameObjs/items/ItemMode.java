package moze_intel.projecte.gameObjs.items;

import java.util.List;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemMode<MODE extends Enum<MODE> & IModeEnum<MODE>> extends ItemPE implements IItemMode<MODE>, IItemCharge, IBarHelper {

	private final int numCharge;

	public ItemMode(Properties props, int numCharge) {
		super(props);
		this.numCharge = numCharge;
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