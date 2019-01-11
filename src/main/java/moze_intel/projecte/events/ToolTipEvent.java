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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
		EntityPlayer clientPlayer = Minecraft.getInstance().player;

		if (ProjectEConfig.misc.pedestalToolTips
			&& currentItem instanceof IPedestalItem)
		{
			event.getToolTip().add(new TextComponentTranslation("pe.pedestal.on_pedestal").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)).appendText(" "));
			List<String> description = ((IPedestalItem) currentItem).getPedestalDescription();
			if (description.isEmpty())
			{
				event.getToolTip().add(IPedestalItem.TOOLTIPDISABLED);
			}
			else
			{
				((IPedestalItem) currentItem).getPedestalDescription()
						.stream()
						.map(TextComponentString::new)
						.forEach(event.getToolTip()::add);
			}
		}

		if (ProjectEConfig.misc.tagToolTips)
		{
			for (ResourceLocation tag : ItemTags.getCollection().getOwningTags(current.getItem()))
			{
				event.getToolTip().add(new TextComponentString("#" + tag.toString()));
			}
		}

		if (ProjectEConfig.misc.emcToolTips)
		{
			if (EMCHelper.doesItemHaveEmc(current))
			{
				long value = EMCHelper.getEmcValue(current);

				ITextComponent prefix = new TextComponentTranslation("pe.emc.emc_tooltip_prefix").setStyle(new Style().setColor(TextFormatting.YELLOW)).appendText(" ");
				ITextComponent valueText = new TextComponentString(Constants.EMC_FORMATTER.format(value)).setStyle(new Style().setColor(TextFormatting.WHITE));
				ITextComponent sell = new TextComponentString(EMCHelper.getEmcSellString(current, 1)).setStyle(new Style().setColor(TextFormatting.BLUE));

				event.getToolTip().add(prefix.appendSibling(valueText).appendSibling(sell));

				if (current.getCount() > 1)
				{
					prefix = new TextComponentTranslation("pe.emc.stackemc_tooltip_prefix").setStyle(new Style().setColor(TextFormatting.YELLOW)).appendText(" ");
					long total;
					try
					{
						total = LongMath.checkedMultiply(value, current.getCount());
						if (total < 0 || total <= value)
						{
							event.getToolTip().add(prefix.appendSibling(new TextComponentTranslation("pe.emc.too_much").setStyle(new Style().setObfuscated(true))));
						}
						else
						{
							valueText = new TextComponentString(Constants.EMC_FORMATTER.format(value * current.getCount())).setStyle(new Style().setColor(TextFormatting.WHITE));
							sell = new TextComponentString(EMCHelper.getEmcSellString(current, current.getCount())).setStyle(new Style().setColor(TextFormatting.BLUE));
							event.getToolTip().add(prefix.appendSibling(valueText).appendSibling(sell));
						}
					} catch (ArithmeticException e) {
						event.getToolTip().add(prefix.appendSibling(new TextComponentTranslation("pe.emc.too_much").setStyle(new Style().setObfuscated(true))));
					}
				}

				if (GuiScreen.isShiftKeyDown()
						&& clientPlayer != null
						&& clientPlayer.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).map(k -> k.hasKnowledge(current)).orElse(false))
				{
					event.getToolTip().add(new TextComponentTranslation("pe.emc.has_knowledge").setStyle(new Style().setColor(TextFormatting.YELLOW)));
				}
			}
		}

		if (ProjectEConfig.misc.statToolTips)
		{
			ITextComponent unit = new TextComponentTranslation("pe.emc.name");
			ITextComponent rate = new TextComponentTranslation("pe.emc.rate");
			/*
			 * Collector ToolTips
			 */
			int genRate = 0;
			int storage = 0;
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
				ITextComponent label = new TextComponentTranslation("pe.emc.maxgenrate_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				ITextComponent val = new TextComponentString(Integer.toString(genRate)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(rate));

				label = new TextComponentTranslation("pe.emc.maxstorage_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				val = new TextComponentString(Integer.toString(storage)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(unit));
			}

			/*
			 * Relay ToolTips
			 */
			int outRate = 0;
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
				ITextComponent label = new TextComponentTranslation("pe.emc.maxoutrate_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				ITextComponent val = new TextComponentString(Integer.toString(outRate)).setStyle(new Style().setColor(TextFormatting.BLUE));
				event.getToolTip().add(label.appendText(" ").appendSibling(val).appendText(" ").appendSibling(rate));

				label = new TextComponentTranslation("pe.emc.maxstorage_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				val = new TextComponentString(Integer.toString(storage)).setStyle(new Style().setColor(TextFormatting.BLUE));
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

				ITextComponent label = new TextComponentTranslation("pe.emc.storedemc_tooltip").setStyle(new Style().setColor(TextFormatting.YELLOW));
				ITextComponent val = new TextComponentString(Constants.EMC_FORMATTER.format(value)).setStyle(new Style().setColor(TextFormatting.RESET));
				event.getToolTip().add(label.appendText(" ").appendSibling(val));
			}
		}
	}
}
