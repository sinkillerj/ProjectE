package moze_intel.projecte.events;

import com.google.common.math.LongMath;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.math.BigInteger;
import java.util.List;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ToolTipEvent
{
	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event)
	{
		ItemStack current = event.getItemStack();
		if (current.isEmpty())
		{
			return;
		}
		Item currentItem = current.getItem();
		Block currentBlock = Block.getBlockFromItem(currentItem);
		PlayerEntity clientPlayer = Minecraft.getInstance().player;

		if (ProjectEConfig.misc.pedestalToolTips.get()
			&& currentItem instanceof IPedestalItem)
		{
			event.getToolTip().add(new TranslationTextComponent("pe.pedestal.on_pedestal").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)).appendText(" "));
			List<ITextComponent> description = ((IPedestalItem) currentItem).getPedestalDescription();
			if (description.isEmpty())
			{
				event.getToolTip().add(IPedestalItem.TOOLTIPDISABLED);
			}
			else
			{
				event.getToolTip().addAll(description);
			}
		}

		if (ProjectEConfig.misc.tagToolTips.get())
		{
			for (ResourceLocation tag : ItemTags.getCollection().getOwningTags(current.getItem()))
			{
				event.getToolTip().add(new StringTextComponent("#" + tag.toString()));
			}
		}

		if (ProjectEConfig.misc.emcToolTips.get())
		{
			if (EMCHelper.doesItemHaveEmc(current))
			{
				long value = EMCHelper.getEmcValue(current);

				ITextComponent prefix = new TranslationTextComponent("pe.emc.emc_tooltip_prefix").applyTextStyle(TextFormatting.YELLOW).appendText(" ");
				ITextComponent valueText = new StringTextComponent(Constants.EMC_FORMATTER.format(value)).applyTextStyle(TextFormatting.WHITE);
				ITextComponent sell = new StringTextComponent(EMCHelper.getEmcSellString(current, 1)).applyTextStyle(TextFormatting.BLUE);

				event.getToolTip().add(prefix.appendSibling(valueText).appendSibling(sell));

				if (current.getCount() > 1)
				{
					prefix = new TranslationTextComponent("pe.emc.stackemc_tooltip_prefix").applyTextStyle(TextFormatting.YELLOW).appendText(" ");
					valueText= new StringTextComponent(Constants.EMC_FORMATTER.format(BigInteger.valueOf(value).multiply(BigInteger.valueOf(current.getCount())))).applyTextStyle(TextFormatting.WHITE);
					sell = new StringTextComponent(EMCHelper.getEmcSellString(current, current.getCount())).applyTextStyle(TextFormatting.BLUE);
					event.getToolTip().add(prefix.appendSibling(valueText).appendSibling(sell));
				}

				if (Screen.hasShiftDown()
						&& clientPlayer != null
						&& clientPlayer.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).map(k -> k.hasKnowledge(current)).orElse(false))
				{
					event.getToolTip().add(new TranslationTextComponent("pe.emc.has_knowledge").setStyle(new Style().setColor(TextFormatting.YELLOW)));
				}
			}
		}

		if (ProjectEConfig.misc.statToolTips.get())
		{
			ITextComponent unit = new TranslationTextComponent("pe.emc.name");
			ITextComponent rate = new TranslationTextComponent("pe.emc.rate");
			/*
			 * Collector ToolTips
			 */
			long genRate = 0;
			long storage = 0;
			if (currentBlock == ObjHandler.collectorMK1)
			{
				genRate = Constants.COLLECTOR_MK1_GEN;
				storage = Constants.COLLECTOR_MK1_MAX;
			}

			if (currentBlock == ObjHandler.collectorMK2)
			{
				genRate = Constants.COLLECTOR_MK2_GEN;
				storage = Constants.COLLECTOR_MK2_MAX;
			}

			if (currentBlock == ObjHandler.collectorMK3)
			{
				genRate = Constants.COLLECTOR_MK3_GEN;
				storage = Constants.COLLECTOR_MK3_MAX;
			}

			if (currentBlock instanceof Collector)
			{
				ITextComponent label = new TranslationTextComponent("pe.emc.maxgenrate_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				ITextComponent val = new StringTextComponent(Long.toString(genRate)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(rate));

				label = new TranslationTextComponent("pe.emc.maxstorage_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				val = new StringTextComponent(Long.toString(storage)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(unit));
			}

			/*
			 * Relay ToolTips
			 */
			long outRate = 0;
			if (currentBlock == ObjHandler.relay)
			{
				outRate = Constants.RELAY_MK1_OUTPUT;
				storage = Constants.RELAY_MK1_MAX;
			}

			if (currentBlock == ObjHandler.relayMK2)
			{
				outRate = Constants.RELAY_MK2_OUTPUT;
				storage = Constants.RELAY_MK2_MAX;
			}

			if (currentBlock == ObjHandler.relayMK3)
			{
				outRate = Constants.RELAY_MK3_OUTPUT;
				storage = Constants.RELAY_MK3_MAX;
			}

			if (currentBlock instanceof Relay)
			{
				ITextComponent label = new TranslationTextComponent("pe.emc.maxoutrate_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				ITextComponent val = new StringTextComponent(Long.toString(outRate)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(rate));

				label = new TranslationTextComponent("pe.emc.maxstorage_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				val = new StringTextComponent(Long.toString(storage)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(unit));
			}
		}

		if (current.hasTag())
		{
			if (current.getItem() instanceof IItemEmc || current.getTag().contains("StoredEMC"))
			{
				double value;
				if (current.getTag().contains("StoredEMC"))
				{
					value = current.getTag().getDouble("StoredEMC");
				} else
				{
					value = ((IItemEmc) current.getItem()).getStoredEmc(current);
				}

				ITextComponent label = new TranslationTextComponent("pe.emc.storedemc_tooltip").setStyle(new Style().setColor(TextFormatting.YELLOW));
				ITextComponent val = new StringTextComponent(Constants.EMC_FORMATTER.format(value)).setStyle(new Style().setColor(TextFormatting.RESET));
				event.getToolTip().add(label.appendText(" ").appendSibling(val));
			}
		}
	}
}
