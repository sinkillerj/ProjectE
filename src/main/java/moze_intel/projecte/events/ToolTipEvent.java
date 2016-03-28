package moze_intel.projecte.events;

import com.google.common.math.LongMath;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.item.IPedestalItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.gui.GUIPedestal;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ToolTipEvent 
{
	@SubscribeEvent
	public void tTipEvent(ItemTooltipEvent event)
	{
		ItemStack current = event.getItemStack();
		Item currentItem = current.getItem();
		Block currentBlock = Block.getBlockFromItem(currentItem);

		if (current == null)
		{
			return;
		}

		if (currentBlock == ObjHandler.dmPedestal)
		{
			event.getToolTip().add(I18n.translateToLocal("pe.pedestal.tooltip1"));
			event.getToolTip().add(I18n.translateToLocal("pe.pedestal.tooltip2"));
		}

		if (currentItem == ObjHandler.manual)
		{
			event.getToolTip().add(I18n.translateToLocal("pe.manual.tooltip1"));
		}

		if (ProjectEConfig.showPedestalTooltip
			&& currentItem instanceof IPedestalItem)
		{
			if (ProjectEConfig.showPedestalTooltipInGUI)
			{
				if (Minecraft.getMinecraft().currentScreen instanceof GUIPedestal)
				{
					event.getToolTip().add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("pe.pedestal.on_pedestal") + " ");
					List<String> description = ((IPedestalItem) currentItem).getPedestalDescription();
					if (description.isEmpty())
					{
						event.getToolTip().add(IPedestalItem.TOOLTIPDISABLED);
					}
					else
					{
						event.getToolTip().addAll(((IPedestalItem) currentItem).getPedestalDescription());
					}
				}
			}
			else
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("pe.pedestal.on_pedestal") + " ");
				List<String> description = ((IPedestalItem) currentItem).getPedestalDescription();
				if (description.isEmpty())
				{
					event.getToolTip().add(IPedestalItem.TOOLTIPDISABLED);
				}
				else
				{
					event.getToolTip().addAll(((IPedestalItem) currentItem).getPedestalDescription());
				}
			}
			
		}

		if (ProjectEConfig.showUnlocalizedNames)
		{
			event.getToolTip().add("UN: " + Item.itemRegistry.getNameForObject(current.getItem()));
		}
		
		if (ProjectEConfig.showODNames)
		{
			for (int id : OreDictionary.getOreIDs(current))
			{
				event.getToolTip().add("OD: " + OreDictionary.getOreName(id));
			}
			if (currentBlock instanceof BlockFluidBase) {
				event.getToolTip().add("Fluid: " + ((BlockFluidBase) currentBlock).getFluid().getName());
			}
		}

		if (ProjectEConfig.showEMCTooltip)
		{
			if (EMCHelper.doesItemHaveEmc(current))
			{
				int value = EMCHelper.getEmcValue(current);

				event.getToolTip().add(TextFormatting.YELLOW +
						I18n.translateToLocal("pe.emc.emc_tooltip_prefix") + " " + TextFormatting.WHITE + String.format("%,d", value));

				if (current.stackSize > 1)
				{
					long total;
					try
					{
						total = LongMath.checkedMultiply(value, current.stackSize);
					} catch (ArithmeticException e) {
						total = Long.MAX_VALUE;
					}
					if (total < 0 || total <= value || total > Integer.MAX_VALUE)
					{
						event.getToolTip().add(TextFormatting.YELLOW + I18n.translateToLocal("pe.emc.stackemc_tooltip_prefix") + " " + TextFormatting.OBFUSCATED + I18n.translateToLocal("pe.emc.too_much"));
					}
					else
					{
						event.getToolTip().add(TextFormatting.YELLOW + I18n.translateToLocal("pe.emc.stackemc_tooltip_prefix") + " " + TextFormatting.WHITE + String.format("%,d", value * current.stackSize));
					}

				}
			}
		}

		if (ProjectEConfig.showStatTooltip)
		{
			/**
			 * Collector ToolTips
			 */
			String unit = I18n.translateToLocal("pe.emc.name");
			String rate = I18n.translateToLocal("pe.emc.rate");

			if (currentBlock == ObjHandler.energyCollector)
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxgenrate_tooltip")
						+ TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK1_GEN));
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxstorage_tooltip")
						+ TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK1_MAX));
			}

			if (currentBlock == ObjHandler.collectorMK2)
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxgenrate_tooltip")
						+ TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK2_GEN));
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxstorage_tooltip")
						+ TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK2_MAX));
			}

			if (currentBlock == ObjHandler.collectorMK3)
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxgenrate_tooltip")
						+ TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK3_GEN));
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxstorage_tooltip")
						+ TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK3_MAX));
			}

			/**
			 * Relay ToolTips
			 */
			if (currentBlock == ObjHandler.relay)
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxoutrate_tooltip")
						+ TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK1_OUTPUT));
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxstorage_tooltip")
						+ TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK1_MAX));
			}

			if (currentBlock == ObjHandler.relayMK2)
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxoutrate_tooltip")
						+ TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK2_OUTPUT));
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxstorage_tooltip")
						+ TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK2_MAX));
			}

			if (currentBlock == ObjHandler.relayMK3)
			{
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxoutrate_tooltip")
						+ TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK3_OUTPUT));
				event.getToolTip().add(TextFormatting.DARK_PURPLE
						+ String.format(I18n.translateToLocal("pe.emc.maxstorage_tooltip")
						+ TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK3_MAX));
			}
		}

		if (current.hasTagCompound())
		{
			if (current.getTagCompound().getBoolean("ProjectEBlock"))
			{
				event.getToolTip().add(TextFormatting.GREEN + I18n.translateToLocal("pe.misc.wrenched_block"));
				
				if (current.getTagCompound().getDouble("EMC") > 0)
				{
					event.getToolTip().add(TextFormatting.YELLOW + String.format(
							I18n.translateToLocal("pe.emc.storedemc_tooltip") + " " + TextFormatting.RESET + "%,d", (int) current.getTagCompound().getDouble("EMC")));
				}
			}

			if (current.getItem() instanceof IItemEmc || current.getTagCompound().hasKey("StoredEMC"))
			{
				double value = 0;
				if (current.getTagCompound().hasKey("StoredEMC"))
				{
					value = current.getTagCompound().getDouble("StoredEMC");
				} else
				{
					value = ((IItemEmc) current.getItem()).getStoredEmc(current);
				}

				event.getToolTip().add(TextFormatting.YELLOW + I18n.translateToLocal("pe.emc.storedemc_tooltip") + " " + TextFormatting.RESET + Constants.EMC_FORMATTER.format(value));
			}

			if (current.getTagCompound().hasKey("StoredXP"))
			{
				event.getToolTip().add(String.format(TextFormatting.DARK_GREEN + I18n.translateToLocal("pe.misc.storedxp_tooltip") + " " + TextFormatting.GREEN + "%,d", current.getTagCompound().getInteger("StoredXP")));
			}
		}
	}
}
