package moze_intel.projecte.gameObjs.items.blocks;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CollectorItem extends BlockItem {

	private final EnumCollectorTier tier;

	public CollectorItem(Collector block, Properties props) {
		super(block, props);
		this.tier = block.getTier();
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.addInformation(stack, world, tooltips, flags);
		if (ProjectEConfig.client.statToolTips.get()) {
			tooltips.add(PELang.EMC_MAX_GEN_RATE.translateColored(TextFormatting.DARK_PURPLE, TextFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getGenRate())));
			tooltips.add(PELang.EMC_MAX_STORAGE.translateColored(TextFormatting.DARK_PURPLE, TextFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getStorage())));
		}
	}
}