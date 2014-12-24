package moze_intel.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.FluidMapper;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.text.DecimalFormat;

@SideOnly(Side.CLIENT)
public class ToolTipEvent 
{
	private DecimalFormat TooltipFormat = new DecimalFormat("###,###,###,###.###");

	@SubscribeEvent
	public void tTipEvent(ItemTooltipEvent event)
	{
		ItemStack current = event.itemStack;
		
		if (current == null)
		{
			return;
		}
		
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
		
		if (Utils.doesItemHaveEmc(current))
		{
			double value = Utils.getEmcValue(current);



			event.toolTip.add(String.format("EMC: %s", TooltipFormat.format(value)));
			
			if (current.stackSize > 1)
			{
				double total = value * current.stackSize;
				
				if (total < 0 || total <= value || total > Double.MAX_VALUE)
				{
					event.toolTip.add("Stack EMC: " + EnumChatFormatting.OBFUSCATED + "WAY TOO MUCH");
				}
				else
				{
					event.toolTip.add(String.format("Stack EMC: %s", TooltipFormat.format(value * current.stackSize)));
				}
			}
		}
		
		if (current.hasTagCompound())
		{
			if (current.stackTagCompound.getBoolean("ProjectEBlock"))
			{
				event.toolTip.add(EnumChatFormatting.GREEN + "Wrenched block!");
				
				if (current.stackTagCompound.getDouble("EMC") > 0)
				{
					event.toolTip.add(String.format("Stored EMC: %s", TooltipFormat.format(current.stackTagCompound.getDouble("EMC"))));
				}
			}
			
			if (current.stackTagCompound.hasKey("StoredEMC"))
			{
				event.toolTip.add(String.format("Stored EMC: %s", TooltipFormat.format(current.stackTagCompound.getDouble("StoredEMC"))));
			}
			else if (current.stackTagCompound.hasKey("StoredXP"))
			{
				event.toolTip.add(String.format("Stored XP: %,d", current.stackTagCompound.getInteger("StoredXP")));
			}
		}

		Block block = Block.getBlockFromItem(current.getItem());

		if (block != null && FluidMapper.doesFluidHaveEMC(block))
		{
			event.toolTip.add(String.format("EMC: %s", TooltipFormat.format(FluidMapper.getFluidEMC(block))));
		}
	}
}
