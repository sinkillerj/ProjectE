package moze_intel.projecte.gameObjs.items.tools;

import java.util.List;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedMatterSword extends PESword implements IItemMode {

	private final ILangEntry[] modeDesc;

	public RedMatterSword(Properties props) {
		super(EnumMatterType.RED_MATTER, 3, 12, props);
		modeDesc = new ILangEntry[]{PELang.MODE_RED_SWORD_1, PELang.MODE_RED_SWORD_2};
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Override
	protected boolean slayAll(@NotNull ItemStack stack) {
		return getMode(stack) == 1;
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modeDesc;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}
}