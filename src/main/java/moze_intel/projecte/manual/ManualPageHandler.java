package moze_intel.projecte.manual;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Comparators;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ManualPageHandler {

	public static final List<PEManualPage> pages = Lists.newArrayList();

	public static void init()
	{
		List<Block> nonTechnicalBlocks = Lists.newArrayList(Sets.difference(ObjHandler.blocks, ObjHandler.technicalBlocks));
		List<Item> nonTechnicalItems = Lists.newArrayList(Sets.difference(ObjHandler.items, ObjHandler.technicalItems));

		Collections.sort(nonTechnicalBlocks, Comparators.BLOCK_UNLOCAL_NAME);
		Collections.sort(nonTechnicalItems, Comparators.ITEM_UNLOCAL_NAME);

		for(Item item : nonTechnicalItems){
			pages.add(new PEManualPage(item));
		}

		for(Block block : nonTechnicalBlocks){
			pages.add(new PEManualPage(Item.getItemFromBlock(block)));
		}
	}
}
