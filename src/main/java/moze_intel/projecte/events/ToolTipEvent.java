package moze_intel.projecte.events;

import java.util.List;
import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ToolTipEvent {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.getItemStack();
		if (current.isEmpty()) {
			return;
		}
		if (ProjectEConfig.client.pedestalToolTips.get()) {
			IPedestalItem pedestalItem = current.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
			if (pedestalItem != null) {
				event.getToolTip().add(PELang.PEDESTAL_ON.translateColored(ChatFormatting.DARK_PURPLE));
				Level level = Minecraft.getInstance().level;
				List<Component> description = pedestalItem.getPedestalDescription(level == null ? SharedConstants.TICKS_PER_SECOND : level.tickRateManager().tickrate());
				if (description.isEmpty()) {
					event.getToolTip().add(PELang.PEDESTAL_DISABLED.translateColored(ChatFormatting.RED));
				} else {
					event.getToolTip().addAll(description);
				}
			}
		}

		if (ProjectEConfig.client.tagToolTips.get()) {
			current.getTags().forEach(tag -> event.getToolTip().add(Component.literal("#" + tag.location())));
		}

		if (ProjectEConfig.client.emcToolTips.get() && (!ProjectEConfig.client.shiftEmcToolTips.get() || Screen.hasShiftDown())) {
			long value = EMCHelper.getEmcValue(current);
			if (value > 0) {
				event.getToolTip().add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					event.getToolTip().add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				Player clientPlayer = Minecraft.getInstance().player;
				if (clientPlayer != null && (!ProjectEConfig.client.shiftLearnedToolTips.get() || Screen.hasShiftDown())) {
					IKnowledgeProvider knowledgeProvider = clientPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
					if (knowledgeProvider != null && knowledgeProvider.hasKnowledge(current)) {
						event.getToolTip().add(PELang.EMC_HAS_KNOWLEDGE.translateColored(ChatFormatting.YELLOW));
					} else {
						event.getToolTip().add(PELang.EMC_NO_KNOWLEDGE.translateColored(ChatFormatting.RED));
					}
				}
			}
		}

		if (current.hasTag() || current.hasAttachments()) {
			long value;
			Optional<Long> existingData = current.getExistingData(PEAttachmentTypes.STORED_EMC);
			if (existingData.isPresent()) {
				value = existingData.get();
			} else {
				IItemEmcHolder emcHolder = current.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
				if (emcHolder != null) {
					value = emcHolder.getStoredEmc(current);
				} else {
					return;
				}
			}
			event.getToolTip().add(PELang.EMC_STORED.translateColored(ChatFormatting.YELLOW, ChatFormatting.WHITE, Constants.EMC_FORMATTER.format(value)));
		}
	}
}