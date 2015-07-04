package moze_intel.projecte.manual;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class PEManualPage
{
	private ItemStack is = null;
	private String title = null;
	private ResourceLocation resource = null;
	private Enum pageType;
	
	public enum type {
		ITEMPAGE, TEXTPAGE, IMAGEPAGE
		}
	
	public PEManualPage(Item item)
	{
		this.is = new ItemStack(item);
		this.pageType = type.ITEMPAGE;
	}
	
	public PEManualPage(Block block)
	{
		this.is = new ItemStack(block);
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

	public PEManualPage(ItemStack is) {
		this.is = is;
		this.pageType = type.ITEMPAGE;
	}
	
	public ItemStack getItemStack()
	{
		return is;
	}
	
	public Enum getType()
	{
		return pageType;
	}
	
	public ResourceLocation getResource()
	{
		return resource;
	}

	public String getItemStackName()
	{
		return StatCollector.translateToLocal(is.getUnlocalizedName() + ".name");
	}

	public String getHelpInfo()
	{
		if(is!=null){
			return StatCollector.translateToLocal("pe.manual." + is.getUnlocalizedName().substring(5)); // Strip "item." or "tile."
		}else{
			return StatCollector.translateToLocal("pe.manual." + title);
		}
	}
	
	public String getTitle()
	{
		return StatCollector.translateToLocal("pe.manual.title." + title);
	}
	
}
