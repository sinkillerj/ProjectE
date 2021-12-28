package moze_intel.projecte.gameObjs.items.blocks;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CollectorItem extends BlockItem {

	private final EnumCollectorTier tier;

	public CollectorItem(Collector block, Properties props) {
		super(block, props);
		this.tier = block.getTier();
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		if (ProjectEConfig.client.statToolTips.get()) {
			tooltips.add(PELang.EMC_MAX_GEN_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getGenRate())));
			tooltips.add(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getStorage())));
		}
	}
}