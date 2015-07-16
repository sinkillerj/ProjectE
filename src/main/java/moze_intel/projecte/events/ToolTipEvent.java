package moze_intel.projecte.events;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.api.tooltip.ITTAlchBagFunctionality;
import moze_intel.projecte.api.tooltip.ITTAlchChestFunctionality;
import moze_intel.projecte.api.tooltip.ITTBaseFunctionality;
import moze_intel.projecte.api.tooltip.ITTBaubleFunctionality;
import moze_intel.projecte.api.tooltip.ITTHotbarFunctionality;
import moze_intel.projecte.api.tooltip.ITTInventoryFunctionality;
import moze_intel.projecte.api.tooltip.ITTPedestalFunctionality;
import moze_intel.projecte.api.tooltip.special.ITTConsumesEMC;
import moze_intel.projecte.api.tooltip.special.ITTGeneralFunctionality;
import moze_intel.projecte.api.tooltip.special.ITTPedestalFunctionalitySpecial;
import moze_intel.projecte.api.tooltip.special.ITTSpecialFunctionality;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.gui.GUIPedestal;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;

import net.java.games.input.Keyboard;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;
import scala.collection.immutable.Set;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ToolTipEvent 
{
	@SubscribeEvent
	public void tTipEvent(ItemTooltipEvent event)
	{
		ItemStack current = event.itemStack;
		Item currentItem = current.getItem();
		Block currentBlock = Block.getBlockFromItem(currentItem);

		if (current == null)
		{
			return;
		}

		if (currentItem instanceof ITTBaseFunctionality) {
			addFunctionalityTooltip((ITTBaseFunctionality)currentItem, event);
		}

		if (currentBlock == ObjHandler.dmPedestal)
		{
			event.toolTip.add(StatCollector.translateToLocal("pe.pedestal.tooltip1"));
			event.toolTip.add(StatCollector.translateToLocal("pe.pedestal.tooltip2"));
		}

		/*if (ProjectEConfig.showPedestalTooltip
			&& currentItem instanceof IPedestalItem)
		{
			if (ProjectEConfig.showPedestalTooltipInGUI)
			{
				if (Minecraft.getMinecraft().currentScreen instanceof GUIPedestal)
				{
					event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("pe.pedestal.on_pedestal") + " ");
					List<String> description = ((IPedestalItem) currentItem).getPedestalDescription();
					if (description.isEmpty())
					{
						event.toolTip.add(IPedestalItem.TOOLTIPDISABLED);
					}
					else
					{
						event.toolTip.addAll(((IPedestalItem) currentItem).getPedestalDescription());
					}
				}
			}
			else
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("pe.pedestal.on_pedestal") + " ");
				List<String> description = ((IPedestalItem) currentItem).getPedestalDescription();
				if (description.isEmpty())
				{
					event.toolTip.add(IPedestalItem.TOOLTIPDISABLED);
				}
				else
				{
					event.toolTip.addAll(((IPedestalItem) currentItem).getPedestalDescription());
				}
			}
			
		}*/

		if (ProjectEConfig.showUnlocalizedNames)
		{
			event.toolTip.add("UN: " + Item.itemRegistry.getNameForObject(current.getItem()));
		}
		
		if (ProjectEConfig.showODNames)
		{
			for (int id : OreDictionary.getOreIDs(current))
			{
				event.toolTip.add("OD: " + OreDictionary.getOreName(id));
			}
		}

		if (ProjectEConfig.showEMCTooltip)
		{
			if (EMCHelper.doesItemHaveEmc(current))
			{
				int value = EMCHelper.getEmcValue(current);

				event.toolTip.add(EnumChatFormatting.YELLOW +
						StatCollector.translateToLocal("pe.emc.emc_tooltip_prefix") + " " + EnumChatFormatting.WHITE + String.format("%,d", value));

				if (current.stackSize > 1)
				{
					long total = value * current.stackSize;

					if (total < 0 || total <= value || total > Integer.MAX_VALUE)
					{
						event.toolTip.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("pe.emc.stackemc_tooltip_prefix") + " " + EnumChatFormatting.OBFUSCATED + StatCollector.translateToLocal("pe.emc.too_much"));
					}
					else
					{
						event.toolTip.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("pe.emc.stackemc_tooltip_prefix") + " " + EnumChatFormatting.WHITE + String.format("%,d", value * current.stackSize));
					}
				}
			}
		}

		if (ProjectEConfig.showStatTooltip)
		{
			/**
			 * Collector ToolTips
			 */
			String unit = StatCollector.translateToLocal("pe.emc.name");
			String rate = StatCollector.translateToLocal("pe.emc.rate");

			if (currentBlock == ObjHandler.energyCollector)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxgenrate_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK1_GEN));
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxstorage_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK1_MAX));
			}

			if (currentBlock == ObjHandler.collectorMK2)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxgenrate_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK2_GEN));
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxstorage_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK2_MAX));
			}

			if (currentBlock == ObjHandler.collectorMK3)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxgenrate_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK3_GEN));
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxstorage_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK3_MAX));
			}

			/**
			 * Relay ToolTips
			 */
			if (currentBlock == ObjHandler.relay)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxoutrate_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + rate, Constants.RELAY_MK1_OUTPUT));
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxstorage_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + unit, Constants.RELAY_MK1_MAX));
			}

			if (currentBlock == ObjHandler.relayMK2)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxoutrate_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + rate, Constants.RELAY_MK2_OUTPUT));
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxstorage_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + unit, Constants.RELAY_MK2_MAX));
			}

			if (currentBlock == ObjHandler.relayMK3)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxoutrate_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + rate, Constants.RELAY_MK3_OUTPUT));
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE
						+ String.format(StatCollector.translateToLocal("pe.emc.maxstorage_tooltip")
						+ EnumChatFormatting.BLUE + " %d " + unit, Constants.RELAY_MK3_MAX));
			}
		}

		if (current.hasTagCompound())
		{
			if (current.stackTagCompound.getBoolean("ProjectEBlock"))
			{
				event.toolTip.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("pe.misc.wrenched_block"));
				
				if (current.stackTagCompound.getDouble("EMC") > 0)
				{
					event.toolTip.add(EnumChatFormatting.YELLOW + String.format(
							StatCollector.translateToLocal("pe.emc.storedemc_tooltip") + " " + EnumChatFormatting.RESET + "%,d", (int) current.stackTagCompound.getDouble("EMC")));
				}
			}
			
			if (current.stackTagCompound.hasKey("StoredEMC"))
			{
				event.toolTip.add(EnumChatFormatting.YELLOW + String.format(
						StatCollector.translateToLocal("pe.emc.storedemc_tooltip") + " " + EnumChatFormatting.RESET + "%,d", (int) current.stackTagCompound.getDouble("StoredEMC")));
			}
			else if (current.stackTagCompound.hasKey("StoredXP"))
			{
				event.toolTip.add(String.format(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("pe.misc.storedxp_tooltip") + " " + EnumChatFormatting.GREEN + "%,d", current.stackTagCompound.getInteger("StoredXP")));
			}
		}
	}

	ImmutableMap<String, Class> functionalityClasses = ImmutableMap.<String,Class>builder()
			//.put("pe.tooltip.functionality.hotbar", ITTHotbarFunctionality.class)
			//.put("pe.tooltip.functionality.inventory", ITTInventoryFunctionality.class)
			.put("pe.tooltip.functionality.alchbag", ITTAlchBagFunctionality.class)
			.put("pe.tooltip.functionality.alchchest", ITTAlchChestFunctionality.class)
			.put("pe.tooltip.functionality.bauble", ITTBaubleFunctionality.class)
			.put("pe.tooltip.functionality.pedestal", ITTPedestalFunctionality.class)
			.build();

	private void addFunctionalityTooltip(ITTBaseFunctionality item, ItemTooltipEvent event)
	{
		if (item instanceof ITTGeneralFunctionality) {
			event.toolTip.addAll(((ITTGeneralFunctionality) item).getGeneralDescription());
		}
		List<String> functionality = Lists.newArrayList();
		if (item instanceof ITTInventoryFunctionality)
		{
			functionality.add(StatCollector.translateToLocal("pe.tooltip.functionality.inventory"));
		} else if (item instanceof ITTHotbarFunctionality)
		{
			functionality.add(StatCollector.translateToLocal("pe.tooltip.functionality.hotbar"));
		}
		for(Map.Entry<String, Class> e: functionalityClasses.entrySet()) {
			if (e.getValue().isInstance(item)) {
				functionality.add(StatCollector.translateToLocal(e.getKey()));
			}
		}
		event.toolTip.add(StatCollector.translateToLocal("pe.tooltip.functionality.prefix") + " " + makeSeparatedList(functionality));

		if (item instanceof ITTSpecialFunctionality)
		{
			addSpecialFunctionality((ITTSpecialFunctionality)item, event);
		}
	}

	private void addSpecialFunctionality(ITTSpecialFunctionality item, ItemTooltipEvent event)
	{
		//if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isPressed())
		if (org.lwjgl.input.Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()))
		{
			if (item instanceof ITTPedestalFunctionalitySpecial)
			{
				event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("pe.pedestal.on_pedestal") + " ");
				event.toolTip.addAll(((ITTPedestalFunctionalitySpecial) item).getPedestalDescription());
			}

			if (item instanceof ITTConsumesEMC)
			{
				event.toolTip.add(ChatFormatting.GOLD + StatCollector.translateToLocal("pe.tooltip.needs_fuel"));
			}
		}
		else
		{
			event.toolTip.add(ChatFormatting.ITALIC + ""+ ChatFormatting.DARK_GRAY + StatCollector.translateToLocal("pe.tooltip.sneakForDetails"));
		}
	}

	private String makeSeparatedList(List<String> list)
	{
		if (list.size() == 0)
		{
			return "";
		}
		if (list.size() == 1)
		{
			return list.get(0);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(list.get(0));
		for (int i = 1; i < list.size() - 1; i++)
		{
			sb.append(StatCollector.translateToLocal("pe.tooltip.functionality.commaSeparator")).append(' ').append(list.get(i));
		}
		sb.append(' ').append(StatCollector.translateToLocal("pe.tooltip.functionality.andSeparator")).append(' ').append(list.get(list.size() - 1));
		return sb.toString();
	}
}
