package moze_intel.projecte.gameObjs.items.blocks;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RelayItem extends BlockItem {

	private final EnumRelayTier tier;

	public RelayItem(Relay block, Properties props) {
		super(block, props);
		this.tier = block.getTier();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
		if (ProjectEConfig.misc.statToolTips.get()) {
			tooltip.add(new TranslationTextComponent("pe.emc.maxoutrate_tooltip").applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" ")
					.appendSibling(new StringTextComponent(Constants.EMC_FORMATTER.format(tier.getChargeRate())).applyTextStyle(TextFormatting.BLUE))
					.appendText(" ").appendSibling(new TranslationTextComponent("pe.emc.rate")));
			tooltip.add(new TranslationTextComponent("pe.emc.maxstorage_tooltip").applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" ")
					.appendSibling(new StringTextComponent(Constants.EMC_FORMATTER.format(tier.getStorage())).applyTextStyle(TextFormatting.BLUE)).appendText(" ")
					.appendSibling(new TranslationTextComponent("pe.emc.name")));
		}
	}
}