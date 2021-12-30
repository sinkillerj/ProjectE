package moze_intel.projecte.gameObjs.items.tools;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class RedMatterSword extends PESword implements IItemMode {

	private final ILangEntry[] modeDesc;

	public RedMatterSword(Properties props) {
		super(EnumMatterType.RED_MATTER, 3, 12, props);
		modeDesc = new ILangEntry[]{PELang.MODE_RED_SWORD_1, PELang.MODE_RED_SWORD_2};
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Override
	protected boolean slayAll(@Nonnull ItemStack stack) {
		return getMode(stack) == 1;
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modeDesc;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}
}