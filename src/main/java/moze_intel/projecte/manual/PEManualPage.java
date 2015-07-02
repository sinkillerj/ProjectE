package moze_intel.projecte.manual;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;

public class PEManualPage
{
	private Item item = null;
	private String title = null;
	public boolean textPage = false;
	
	public PEManualPage(Item item)
	{
		this.item = item;
	}
	
	public PEManualPage(String string)
	{
		this.title = string;
		this.textPage = true;	
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
		if(item!=null)return StatCollector.translateToLocal("pe.manual." + item.getUnlocalizedName().substring(5)); // Strip "item." or "tile."
		else return StatCollector.translateToLocal("pe.manual." + title);
	}
	
	public String getTitle()
	{
		return StatCollector.translateToLocal("pe.manual.title." + title);
	}
	
}
