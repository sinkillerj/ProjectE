package moze_intel.projecte.gameObjs.items;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class PEBlockItem extends BlockItem {

	private final TooltipAppender tooltipAppender;

	public PEBlockItem(Block block, Properties props, TooltipAppender tooltipAppender) {
		super(block, props);
		this.tooltipAppender = tooltipAppender;
	}

	public PEBlockItem(Block block, Properties props) {
		this(block, props, null);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
			@NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		if (tooltipAppender != null) {
			tooltipAppender.append(stack, tooltip, flags);
		}
	}

	@FunctionalInterface
	public interface TooltipAppender {
		void append(ItemStack stack, Consumer<Component> tooltip, TooltipFlag flags);
	}
}
