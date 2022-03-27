package moze_intel.projecte.gameObjs.items.blocks;

import java.util.List;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayItem extends BlockItem {

	private final EnumRelayTier tier;

	public RelayItem(Relay block, Properties props) {
		super(block, props);
		this.tier = block.getTier();
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		if (ProjectEConfig.client.statToolTips.get()) {
			tooltips.add(PELang.EMC_MAX_OUTPUT_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getChargeRate())));
			tooltips.add(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, Constants.EMC_FORMATTER.format(tier.getStorage())));
		}
	}
}