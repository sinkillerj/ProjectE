package moze_intel.projecte.gameObjs.items.tools;

import java.util.List;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.tools.PEKatar.KatarMode;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedMatterSword extends PESword implements IItemMode<KatarMode> {

	public RedMatterSword(Properties props) {
		super(EnumMatterType.RED_MATTER, 3, 12, props);
	}

	@Override
	protected boolean slayAll(@NotNull ItemStack stack) {
		return getMode(stack) == KatarMode.SLAY_ALL;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}

	@Override
	public AttachmentType<KatarMode> getAttachmentType() {
		return PEAttachmentTypes.KATAR_MODE.get();
	}
}