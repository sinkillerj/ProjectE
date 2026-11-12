package moze_intel.projecte.gameObjs.items.tools;

import java.util.function.Consumer;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.tools.PEKatar.KatarMode;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class RedMatterSword extends PESword implements IItemMode<KatarMode> {

	public RedMatterSword(Properties props) {
		super(EnumMatterType.RED_MATTER, 3, 12, props.component(PEDataComponentTypes.KATAR_MODE, KatarMode.SLAY_HOSTILE));
	}

	@Override
	protected boolean slayAll(@NotNull ItemStack stack) {
		return getMode(stack) == KatarMode.SLAY_ALL;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		tooltip.accept(getToolTip(stack));
	}

	@Override
	public DataComponentType<KatarMode> getDataComponentType() {
		return PEDataComponentTypes.KATAR_MODE.get();
	}

	@Override
	public KatarMode getDefaultMode() {
		return KatarMode.SLAY_HOSTILE;
	}
}
