package moze_intel.projecte.manual;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.gameObjs.ObjHandler;

import java.util.List;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ManualPageHandler
{
	public static final List<PEManualPage> pages = Lists.newArrayList();
	// Collection of pages that should be shown in the index. K = index in above list, V = the page object
	public static final TreeMap<Integer, PEManualPage> indexedPages = Maps.newTreeMap();

	public static void init()
	{
		
		addTextPage("introduction");
		
		//Blocks
		addItem(ObjHandler.alchChest);
		addImagePage("alchchest", new ResourceLocation("projecte:textures/gui/alchchest.png"));
		addItem(ObjHandler.confuseTorch);
		addItem(ObjHandler.transmuteStone);
		addItem(ObjHandler.condenser);
		addItem(ObjHandler.condenserMk2);
		addItem(ObjHandler.rmFurnaceOff);
		addItem(ObjHandler.dmFurnaceOff);
		addItem(ObjHandler.dmPedestal);
		addItem(ObjHandler.energyCollector);
		addItem(ObjHandler.collectorMK2);
		addItem(ObjHandler.collectorMK3);
		addItem(ObjHandler.relay);
		addItem(ObjHandler.relayMK2);
		addItem(ObjHandler.relayMK3);
		addItem(ObjHandler.novaCatalyst);
		addItem(ObjHandler.novaCataclysm);
		
		// Blocks with different meta forms
		addSubItems(getSubItems(ObjHandler.matterBlock));
		addSubItems(getSubItems(ObjHandler.fuelBlock));
		
		//Items
		addItem(ObjHandler.philosStone);
		addItem(ObjHandler.alchBag);
		addItem(ObjHandler.repairTalisman);
		addItem(ObjHandler.dmPick);
		addItem(ObjHandler.dmAxe);
		addItem(ObjHandler.dmShovel);
		addItem(ObjHandler.dmSword);
		addItem(ObjHandler.dmHoe);
		addItem(ObjHandler.dmShears);
		addItem(ObjHandler.dmHammer);
		addItem(ObjHandler.rmPick);
		addItem(ObjHandler.rmAxe);
		addItem(ObjHandler.rmShovel);
		addItem(ObjHandler.rmSword);
		addItem(ObjHandler.rmHoe);
		addItem(ObjHandler.rmShears);
		addItem(ObjHandler.rmHammer);
		addItem(ObjHandler.rmKatar);
		addItem(ObjHandler.rmStar);
		addItem(ObjHandler.dmHelmet);
		addItem(ObjHandler.dmChest);
		addItem(ObjHandler.dmLegs);
		addItem(ObjHandler.dmFeet);
		addItem(ObjHandler.rmHelmet);
		addItem(ObjHandler.rmChest);
		addItem(ObjHandler.rmLegs);
		addItem(ObjHandler.rmFeet);
		addItem(ObjHandler.gemHelmet);
		addItem(ObjHandler.gemChest);
		addItem(ObjHandler.gemLegs);
		addItem(ObjHandler.gemFeet);
		addItem(ObjHandler.ironBand);
		addItem(ObjHandler.blackHole);
		addItem(ObjHandler.angelSmite);
		addItem(ObjHandler.harvestGod);
		addItem(ObjHandler.ignition);
		addItem(ObjHandler.zero);
		addItem(ObjHandler.swrg);
		addItem(ObjHandler.timeWatch);
		addItem(ObjHandler.everTide);
		addItem(ObjHandler.volcanite);
		addItem(ObjHandler.eternalDensity);
		addItem(ObjHandler.dRod1);
		addItem(ObjHandler.dRod2);
		addItem(ObjHandler.dRod3);
		addItem(ObjHandler.mercEye);
		addItem(ObjHandler.voidRing);
		addItem(ObjHandler.arcana);
		addItem(ObjHandler.dCatalyst);
		addItem(ObjHandler.hyperLens);
		addItem(ObjHandler.cataliticLens);
		addItem(ObjHandler.bodyStone);
		addItem(ObjHandler.soulStone);
		addItem(ObjHandler.mindStone);
		addItem(ObjHandler.lifeStone);
		addItem(ObjHandler.tome);
		addItem(ObjHandler.transmutationTablet);
		
		// Items with different meta forms
		addSubItems(getSubItems(ObjHandler.matter));
		addSubItems(getSubItems(ObjHandler.fuels));
		addSubItems(getSubItems(ObjHandler.covalence));
		addSubItems(getSubItems(ObjHandler.kleinStars));
	}
	
	private static void addItem(Item item)
	{
		PEManualPage page = PEManualPage.createItemPage(item);
		pages.add(page);
		indexedPages.put(pages.indexOf(page), page);
	}
	
	private static void addItem(Block block)
	{
		PEManualPage page = PEManualPage.createItemPage(block);
		pages.add(page);
		indexedPages.put(pages.indexOf(page), page);
	}
	
	private static void addSubItems(List<ItemStack> list)
	{
		for (ItemStack is : list)
		{
			PEManualPage page = PEManualPage.createItemPage(is);
			pages.add(page);
			indexedPages.put(pages.indexOf(page), page);
		}
	}
	
	private static void addTextPage(String identifier)
	{
		PEManualPage page = PEManualPage.createTextPage(identifier);
		pages.add(page);
		indexedPages.put(pages.indexOf(page), page);
	}
	
	private static void addImagePage(String title, ResourceLocation resource)
	{
		pages.add(PEManualPage.createImagePage(title, resource));
		// indexedPages.put(pages.indexOf(page),title); Uncomment to add image pages to index
	}

	private static List<ItemStack> getSubItems(Block b)
	{
		List<ItemStack> list = Lists.newArrayList();
		b.getSubBlocks(Item.getItemFromBlock(b), null, list);
		return list;
	}

	private static List<ItemStack> getSubItems(Item i)
	{
		List<ItemStack> list = Lists.newArrayList();
		i.getSubItems(i, null, list);
		return list;
	}
}
