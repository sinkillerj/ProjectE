package moze_intel.projecte.manual;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.FuelBlock;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.gameObjs.items.AlchemicalFuel;
import moze_intel.projecte.gameObjs.items.CovalenceDust;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.items.Matter;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ManualPageHandler
{
	public static final List<PEManualPage> pages = Lists.newArrayList();
	public static final TreeMap<Integer, String> pageIndexes = Maps.newTreeMap();
	public static List<ItemStack> subItems = Lists.newArrayList();
	
	private static int pageNumber = 0;

	public static void init()
	{
		
		addPage("introduction");
		
		//Blocks
		addItems(ObjHandler.alchChest);
		addImagePage("alchchest", new ResourceLocation("projecte:textures/gui/alchchest.png"));
		addItems(ObjHandler.confuseTorch);
		addItems(ObjHandler.transmuteStone);
		addItems(ObjHandler.condenser);
		addItems(ObjHandler.condenserMk2);
		addItems(ObjHandler.rmFurnaceOff);
		addItems(ObjHandler.dmFurnaceOff);
		addItems(ObjHandler.dmPedestal);
		addItems(ObjHandler.energyCollector);
		addItems(ObjHandler.collectorMK2);
		addItems(ObjHandler.collectorMK3);
		addItems(ObjHandler.relay);
		addItems(ObjHandler.relayMK2);
		addItems(ObjHandler.relayMK3);
		addItems(ObjHandler.novaCatalyst);
		addItems(ObjHandler.novaCataclysm);
		
		//Meta-blocks
		new MatterBlock().getSubBlocks(ObjHandler.matterBlock, null, subItems);
		addSubItems(subItems);
		new FuelBlock().getSubBlocks(ObjHandler.fuelBlock, null, subItems);
		addSubItems(subItems);
		
		//Items
		addItems(ObjHandler.philosStone);
		addItems(ObjHandler.alchBag); //Left out of meta-items due to clutter reasons, don't need 16 alchbag pages
		addItems(ObjHandler.repairTalisman);
		addItems(ObjHandler.dmPick);
		addItems(ObjHandler.dmAxe);
		addItems(ObjHandler.dmShovel);
		addItems(ObjHandler.dmSword);
		addItems(ObjHandler.dmHoe);
		addItems(ObjHandler.dmShears);
		addItems(ObjHandler.dmHammer);
		addItems(ObjHandler.rmPick);
		addItems(ObjHandler.rmAxe);
		addItems(ObjHandler.rmShovel);
		addItems(ObjHandler.rmSword);
		addItems(ObjHandler.rmHoe);
		addItems(ObjHandler.rmShears);
		addItems(ObjHandler.rmHammer);
		addItems(ObjHandler.rmKatar);
		addItems(ObjHandler.rmStar);
		addItems(ObjHandler.dmHelmet);
		addItems(ObjHandler.dmChest);
		addItems(ObjHandler.dmLegs);
		addItems(ObjHandler.dmFeet);
		addItems(ObjHandler.rmHelmet);
		addItems(ObjHandler.rmChest);
		addItems(ObjHandler.rmLegs);
		addItems(ObjHandler.rmFeet);
		addItems(ObjHandler.gemHelmet);
		addItems(ObjHandler.gemChest);
		addItems(ObjHandler.gemLegs);
		addItems(ObjHandler.gemFeet);
		addItems(ObjHandler.ironBand);
		addItems(ObjHandler.blackHole);
		addItems(ObjHandler.angelSmite);
		addItems(ObjHandler.harvestGod);
		addItems(ObjHandler.ignition);
		addItems(ObjHandler.zero);
		addItems(ObjHandler.swrg);
		addItems(ObjHandler.timeWatch);
		addItems(ObjHandler.everTide);
		addItems(ObjHandler.volcanite);
		addItems(ObjHandler.eternalDensity);
		addItems(ObjHandler.dRod1);
		addItems(ObjHandler.dRod2);
		addItems(ObjHandler.dRod3);
		addItems(ObjHandler.mercEye);
		addItems(ObjHandler.voidRing);
		addItems(ObjHandler.arcana);
		addItems(ObjHandler.dCatalyst);
		addItems(ObjHandler.hyperLens);
		addItems(ObjHandler.cataliticLens);
		addItems(ObjHandler.bodyStone);
		addItems(ObjHandler.soulStone);
		addItems(ObjHandler.mindStone);
		addItems(ObjHandler.lifeStone);
		addItems(ObjHandler.tome);
		addItems(ObjHandler.transmutationTablet);
		
		//Meta-items
		new Matter().getSubItems(ObjHandler.matter, null, subItems);
		addSubItems(subItems);
		new AlchemicalFuel().getSubItems(ObjHandler.fuels, null, subItems);
		addSubItems(subItems);
		new CovalenceDust().getSubItems(ObjHandler.covalence, null, subItems);
		addSubItems(subItems);
		new KleinStar().getSubItems(ObjHandler.kleinStars, null, subItems);
		addSubItems(subItems);
		
		//for(Entry<String,Integer>entry : pageIndexes.entrySet()){
		//	System.out.println(entry.getKey() + ":" + entry.getValue());
		//}
	}
	
	private static void addItems(Item item)
	{
		pages.add(new PEManualPage(item));
		pageIndexes.put(pageNumber++, pages.get(pageNumber).getItemStack().getUnlocalizedName());
	}
	
	private static void addItems(Block block)
	{
		pages.add(new PEManualPage(block));
		pageIndexes.put(pageNumber++, pages.get(pageNumber).getItemStack().getUnlocalizedName());
	}
	
	private static void addSubItems(List<ItemStack> list)
	{
		for(ItemStack is : list){
			pages.add(new PEManualPage(is));
			pageIndexes.put(pageNumber++, pages.get(pageNumber).getItemStack().getUnlocalizedName());
		}
		list.clear();
	}	
	
	private static void addPage(String title)
	{
		pages.add(new PEManualPage(title));
		pageIndexes.put(pageNumber++,title);
	}
	
	private static void addImagePage(String title, ResourceLocation resource)
	{
		pages.add(new PEManualPage(title, resource));
		//pageIndexes.put(pageNumber++,title); //Don't want this in the index probably
		pageNumber++; //Don't forget to remove if you add to index
	}
}
