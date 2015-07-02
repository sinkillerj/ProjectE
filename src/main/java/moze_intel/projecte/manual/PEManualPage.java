package moze_intel.projecte.manual;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class PEManualPage
{
	private Item item = null;
	private String title = null;
	private ResourceLocation resource = null;
	private Enum pageType;
	
	public enum type {
		ITEMPAGE, TEXTPAGE, IMAGEPAGE
		}
	
	public PEManualPage(Item item)
	{
		this.item = item;
		this.pageType = type.ITEMPAGE;
	}
	
	public PEManualPage(String title)
	{
		this.title = title;
		this.pageType = type.TEXTPAGE;
	}
	
	public PEManualPage(String title,ResourceLocation resource){
		this.resource = resource;
		this.title = title;
		this.pageType = type.IMAGEPAGE;
	}

	public Item getItem()
	{
		return item;
	}
	
	public Enum getType()
	{
		return pageType;
	}
	
	public ResourceLocation getResource()
	{
		return resource;
	}

	public String getItemName()
	{
		return StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
	}

	public String getHelpInfo()
	{
		if(item!=null){
			return StatCollector.translateToLocal("pe.manual." + item.getUnlocalizedName().substring(5)); // Strip "item." or "tile."
		}else{
			return StatCollector.translateToLocal("pe.manual." + title);
		}
	}
	
	public String getTitle()
	{
		return StatCollector.translateToLocal("pe.manual.title." + title);
	}
	
}
