package moze_intel.projecte.gameObjs.items.tools;

import javax.annotation.Nonnull;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import net.minecraft.item.ItemStack;

public class RedMatterSword extends PESword implements IItemMode {

	private final String[] modeDesc;

	public RedMatterSword(Properties props) {
		super(EnumMatterType.RED_MATTER, 3, 12, props);
		modeDesc = new String[]{"pe.redsword.mode1", "pe.redsword.mode2"};
		addItemCapability(new ModeChangerItemCapabilityWrapper());
	}

	@Override
	protected boolean slayAll(@Nonnull ItemStack stack) {
		return getMode(stack) == 1;
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modeDesc;
	}
}