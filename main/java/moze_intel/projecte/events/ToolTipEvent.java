package moze_intel.projecte.events;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ToolTipEvent 
{
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
			event.toolTip.add("UN: "+Item.itemRegistry.getNameForObject(current.getItem()));
		}
		
		if (ProjectEConfig.showODNames)
		{
			for (int id : OreDictionary.getOreIDs(current))
			{
				event.toolTip.add("OD: "+OreDictionary.getOreName(id));
			}
		}
		
		if (Utils.doesItemHaveEmc(current))
		{
			int value = Utils.getEmcValue(current);
			
			event.toolTip.add(String.format("EMC: %,d", value));
			
			if (current.stackSize > 1)
			{
				event.toolTip.add(String.format("Stack EMC: %,d", value * current.stackSize));
			}
		}
		
		if (current.hasTagCompound())
		{
			if (current.stackTagCompound.hasKey("StoredEMC"))
			{
				event.toolTip.add(String.format("Stored EMC: %,d", (int) current.stackTagCompound.getDouble("StoredEMC")));
			}
			else if (current.stackTagCompound.hasKey("StoredXP"))
			{
				event.toolTip.add(String.format("Stored XP: %,d", current.stackTagCompound.getInteger("StoredXP")));
			}
		}
	}
}
