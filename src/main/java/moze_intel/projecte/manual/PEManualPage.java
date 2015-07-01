package moze_intel.projecte.manual;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;

public class PEManualPage
{
	private final Item item;
	
	public PEManualPage(Item item)
	{
		this.item = item;
	}

	public Item getItem()
	{
		return item;
	}

	public String getItemName()
	{
		return StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
	}

	public String getHelpInfo()
	{
		return StatCollector.translateToLocal("pe.manual." + item.getUnlocalizedName().substring(5)); // Strip "item." or "tile."
	}
	
}
