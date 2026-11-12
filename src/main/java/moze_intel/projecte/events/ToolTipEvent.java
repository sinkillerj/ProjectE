package moze_intel.projecte.events;

import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ToolTipEvent {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {
		ItemStack current = event.getItemStack();
		if (current.isEmpty()) {
			return;
		}
		List<Component> tooltip = event.getToolTip();
		if (ProjectEConfig.client.pedestalToolTips.get()) {
			IPedestalItem pedestalItem = current.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
			if (pedestalItem != null) {
				tooltip.add(PELang.PEDESTAL_ON.translateColored(ChatFormatting.DARK_PURPLE));
				List<Component> description = pedestalItem.getPedestalDescription(event.getContext().tickRate());
				if (description.isEmpty()) {
					tooltip.add(PELang.PEDESTAL_DISABLED.translateColored(ChatFormatting.RED));
				} else {
					tooltip.addAll(description);
				}
			}
		}

		if (ProjectEConfig.client.tagToolTips.get()) {
			current.tags().forEach(tag -> tooltip.add(Component.literal("#" + tag.location())));
		}

		if (ProjectEConfig.client.emcToolTips.get() && (!ProjectEConfig.client.shiftEmcToolTips.get() || Minecraft.getInstance().options.keyShift.isDown())) {
			long value = IEMCProxy.INSTANCE.getValue(current);
			if (value > 0) {
				tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					tooltip.add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				Player player = event.getEntity();
				if (player != null && (!ProjectEConfig.client.shiftLearnedToolTips.get() || Minecraft.getInstance().options.keyShift.isDown())) {
					IKnowledgeProvider knowledgeProvider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
					if (knowledgeProvider != null && knowledgeProvider.hasKnowledge(current)) {
						tooltip.add(PELang.EMC_HAS_KNOWLEDGE.translateColored(ChatFormatting.YELLOW));
					} else {
						tooltip.add(PELang.EMC_NO_KNOWLEDGE.translateColored(ChatFormatting.RED));
					}
				}
			}
		}

		long value = current.getOrDefault(PEDataComponentTypes.STORED_EMC, 0L);
		if (value == 0) {
			IItemEmcHolder emcHolder = current.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				value = emcHolder.getStoredEmc(current);
			}
		}
		if (value > 0) {
			tooltip.add(PELang.EMC_STORED.translateColored(ChatFormatting.YELLOW, ChatFormatting.WHITE, EMCHelper.formatEmc(value)));
		}
	}
}
