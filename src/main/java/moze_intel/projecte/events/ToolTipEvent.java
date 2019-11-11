package moze_intel.projecte.events;

import java.util.List;
import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.LazyOptionalHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ToolTipEvent {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.getItemStack();
		if (current.isEmpty()) {
			return;
		}
		PlayerEntity clientPlayer = Minecraft.getInstance().player;
		if (ProjectEConfig.client.pedestalToolTips.get()) {
			current.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> {
				event.getToolTip().add(new TranslationTextComponent("pe.pedestal.on_pedestal").applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" "));
				List<ITextComponent> description = pedestalItem.getPedestalDescription();
				if (description.isEmpty()) {
					event.getToolTip().add(IPedestalItem.TOOLTIP_DISABLED);
				} else {
					event.getToolTip().addAll(description);
				}
			});
		}

		if (ProjectEConfig.client.tagToolTips.get()) {
			for (ResourceLocation tag : ItemTags.getCollection().getOwningTags(current.getItem())) {
				event.getToolTip().add(new StringTextComponent("#" + tag.toString()));
			}
		}

		if (ProjectEConfig.client.emcToolTips.get() && (!ProjectEConfig.client.shiftEmcToolTips.get() || Screen.hasShiftDown())) {
			long value = EMCHelper.getEmcValue(current);
			if (value > 0) {
				event.getToolTip().add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					event.getToolTip().add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				if (Screen.hasShiftDown() && clientPlayer != null && clientPlayer.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).map(k -> k.hasKnowledge(current)).orElse(false)) {
					event.getToolTip().add(new TranslationTextComponent("pe.emc.has_knowledge").applyTextStyle(TextFormatting.YELLOW));
				}
			}
		}

		if (current.hasTag()) {
			long value;
			if (current.getTag().contains(Constants.NBT_KEY_STORED_EMC)) {
				value = current.getTag().getLong(Constants.NBT_KEY_STORED_EMC);
			} else {
				Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(current.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
				if (holderCapability.isPresent()) {
					value = holderCapability.get().getStoredEmc(current);
				} else {
					return;
				}
			}
			event.getToolTip().add(new TranslationTextComponent("pe.emc.storedemc_tooltip").applyTextStyle(TextFormatting.YELLOW).appendText(" ")
					.appendSibling(new StringTextComponent(Constants.EMC_FORMATTER.format(value)).applyTextStyle(TextFormatting.RESET)));
		}
	}
}