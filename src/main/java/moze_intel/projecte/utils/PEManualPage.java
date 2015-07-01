package moze_intel.projecte.utils;

import net.minecraft.item.Item;

public class PEManualPage {

	private Item item;
	private String itemName;
	private String helpInfo;
	
	public PEManualPage(Item item, String itemName, String helpInfo){
		this.item = item;
		this.itemName = itemName;
		this.helpInfo = helpInfo;
	}

	public Item getItem() {
		return item;
	}

	public String getItemName() {
		return itemName;
	}

	public String getHelpInfo() {
		return helpInfo;
	}
	
}
