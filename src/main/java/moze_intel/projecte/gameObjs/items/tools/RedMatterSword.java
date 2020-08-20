package moze_intel.projecte.gameObjs.items.tools;

import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		list.add(getToolTip(stack));
	}
}