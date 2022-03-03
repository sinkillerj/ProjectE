package moze_intel.projecte.events;

import java.util.List;
import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
		Player clientPlayer = Minecraft.getInstance().player;
		if (ProjectEConfig.client.pedestalToolTips.get()) {
			current.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> {
				event.getToolTip().add(PELang.PEDESTAL_ON.translateColored(ChatFormatting.DARK_PURPLE));
				List<Component> description = pedestalItem.getPedestalDescription();
				if (description.isEmpty()) {
					event.getToolTip().add(PELang.PEDESTAL_DISABLED.translateColored(ChatFormatting.RED));
				} else {
					event.getToolTip().addAll(description);
				}
			});
		}

		if (ProjectEConfig.client.tagToolTips.get()) {
			current.getTags().forEach(tag -> event.getToolTip().add(new TextComponent("#" + tag.location())));
		}

		if (ProjectEConfig.client.emcToolTips.get() && (!ProjectEConfig.client.shiftEmcToolTips.get() || Screen.hasShiftDown())) {
			long value = EMCHelper.getEmcValue(current);
			if (value > 0) {
				event.getToolTip().add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					event.getToolTip().add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				if (Screen.hasShiftDown() && clientPlayer != null && clientPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).map(k -> k.hasKnowledge(current)).orElse(false)) {
					event.getToolTip().add(PELang.EMC_HAS_KNOWLEDGE.translateColored(ChatFormatting.YELLOW));
				}
			}
		}

		if (current.hasTag()) {
			long value;
			CompoundTag tag = current.getOrCreateTag();
			if (tag.contains(Constants.NBT_KEY_STORED_EMC, Tag.TAG_LONG)) {
				value = tag.getLong(Constants.NBT_KEY_STORED_EMC);
			} else {
				Optional<IItemEmcHolder> holderCapability = current.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (holderCapability.isPresent()) {
					value = holderCapability.get().getStoredEmc(current);
				} else {
					return;
				}
			}
			event.getToolTip().add(PELang.EMC_STORED.translateColored(ChatFormatting.YELLOW, ChatFormatting.WHITE, Constants.EMC_FORMATTER.format(value)));
		}
	}
}