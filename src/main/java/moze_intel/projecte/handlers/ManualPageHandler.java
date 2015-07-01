package moze_intel.projecte.handlers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.PEManualPage;

public class ManualPageHandler {
	
	public static ArrayList<Item> manualItems = new ArrayList<Item>();
	public static ArrayList<Block> manualBlocks = new ArrayList<Block>();

	public static void registerPages( ArrayList<PEManualPage> manualPages) {
		String helpInfo;

		addPages();
		
		for(Item item : manualItems){
			PECore.instance.manualPages.add(new PEManualPage(item, StatCollector.translateToLocal(item.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + item.getUnlocalizedName().substring(5) + ".info")));

		}
		for(Block block : manualBlocks){
			PECore.instance.manualPages.add(new PEManualPage(Item.getItemFromBlock(block), StatCollector.translateToLocal(block.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + block.getUnlocalizedName().substring(5) + ".info")));
		}
	}
	
	public static void addPages(){
		
		manualItems.add(ObjHandler.philosStone);
		manualItems.add(ObjHandler.blackHole);
		
		manualBlocks.add(ObjHandler.alchChest);
		
	}
	
}
