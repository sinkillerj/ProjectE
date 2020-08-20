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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CollectorItem extends BlockItem {

	private final EnumCollectorTier tier;

	public CollectorItem(Collector block, Properties props) {
		super(block, props);
		this.tier = block.getTier();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
		if (ProjectEConfig.client.statToolTips.get()) {
			tooltip.add(PELang.EMC_MAX_GEN_RATE.translateColored(TextFormatting.DARK_PURPLE, TextFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getGenRate())));
			tooltip.add(PELang.EMC_MAX_STORAGE.translateColored(TextFormatting.DARK_PURPLE, TextFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getStorage())));
		}
	}
}