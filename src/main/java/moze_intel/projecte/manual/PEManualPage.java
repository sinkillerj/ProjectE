package moze_intel.projecte.manual;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class PEManualPage
{
	private final EnumPageType pageType;

	protected PEManualPage(EnumPageType type)
	{
		this.pageType = type;
	}

	public enum EnumPageType
	{
		ITEMPAGE,
		TEXTPAGE,
		IMAGEPAGE
	}

	public EnumPageType getType()
	{
		return pageType;
	}

	public abstract String getBodyText();
	
	public abstract String getHeaderText();

	public static PEManualPage createItemPage(Item item)
	{
		return new ItemPage(new ItemStack(item));
	}

	public static PEManualPage createItemPage(Block block)
	{
		return new ItemPage(new ItemStack(block));
	}

	public static PEManualPage createItemPage(ItemStack stack)
	{
		return new ItemPage(stack.copy());
	}

	public static PEManualPage createTextPage(String identifier)
	{
		return new TextPage(identifier);
	}

	public static PEManualPage createImagePage(String header, ResourceLocation imageLocation)
	{
		return new ImagePage(header, imageLocation);
	}
}
