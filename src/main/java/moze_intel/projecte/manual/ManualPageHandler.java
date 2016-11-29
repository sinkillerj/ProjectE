package moze_intel.projecte.manual;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.gui.GUIManual;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ManualPageHandler
{
    public static final List<IndexPage> indexPages = Lists.newArrayList();
    public static final List<AbstractPage> pages = Lists.newArrayList();
    public static final Map<PageCategory, List<AbstractPage>> categoryMap = Maps.newEnumMap(PageCategory.class);
    public static final List<Pair<AbstractPage, AbstractPage>> spreads = Lists.newArrayList();

    public static void init()
    {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) resourceManager).registerReloadListener(resourceManager1 -> ManualPageHandler.reset());
        }

        reset();
    }

    private static void reset()
    {
        indexPages.clear();
        pages.clear();
        categoryMap.clear();
        spreads.clear();
        setupPages();
    }

    private static void setupPages()
    {
        for (PageCategory e : PageCategory.values())
        {
            categoryMap.put(e, Lists.newArrayList());
        }

        addTextPage("introduction", PageCategory.NONE);

        //Blocks
        addBlock(ObjHandler.alchChest, PageCategory.BLOCK);
        addImagePage("img_alchchest", new ResourceLocation("projecte:textures/gui/alchchest.png"), PageCategory.BLOCK);
        addBlock(ObjHandler.confuseTorch, PageCategory.BLOCK);
        addBlock(ObjHandler.transmuteStone, PageCategory.BLOCK);
        addBlock(ObjHandler.condenser, PageCategory.BLOCK);
        addBlock(ObjHandler.condenserMk2, PageCategory.BLOCK);
        addBlock(ObjHandler.rmFurnaceOff, PageCategory.BLOCK);
        addBlock(ObjHandler.dmFurnaceOff, PageCategory.BLOCK);
        addBlock(ObjHandler.dmPedestal, PageCategory.BLOCK);
        addBlock(ObjHandler.energyCollector, PageCategory.BLOCK);
        addBlock(ObjHandler.collectorMK2, PageCategory.BLOCK);
        addBlock(ObjHandler.collectorMK3, PageCategory.BLOCK);
        addBlock(ObjHandler.relay, PageCategory.BLOCK);
        addBlock(ObjHandler.relayMK2, PageCategory.BLOCK);
        addBlock(ObjHandler.relayMK3, PageCategory.BLOCK);
        addBlock(ObjHandler.novaCatalyst, PageCategory.BLOCK);
        addBlock(ObjHandler.novaCataclysm, PageCategory.BLOCK);

        // Blocks with different meta forms
        addBlock(ObjHandler.matterBlock, PageCategory.BLOCK);
        addBlock(ObjHandler.fuelBlock, PageCategory.BLOCK);

        //Items
        addItem(ObjHandler.philosStone, PageCategory.ITEM);
        addItem(ObjHandler.alchBag, PageCategory.ITEM);
        addItem(ObjHandler.repairTalisman, PageCategory.ITEM);
        addItem(ObjHandler.dmPick, PageCategory.TOOLS);
        addItem(ObjHandler.dmAxe, PageCategory.TOOLS);
        addItem(ObjHandler.dmShovel, PageCategory.TOOLS);
        addItem(ObjHandler.dmSword, PageCategory.TOOLS);
        addItem(ObjHandler.dmHoe, PageCategory.TOOLS);
        addItem(ObjHandler.dmShears, PageCategory.TOOLS);
        addItem(ObjHandler.dmHammer, PageCategory.TOOLS);
        addItem(ObjHandler.rmPick, PageCategory.TOOLS);
        addItem(ObjHandler.rmAxe, PageCategory.TOOLS);
        addItem(ObjHandler.rmShovel, PageCategory.TOOLS);
        addItem(ObjHandler.rmSword, PageCategory.TOOLS);
        addItem(ObjHandler.rmHoe, PageCategory.TOOLS);
        addItem(ObjHandler.rmShears, PageCategory.TOOLS);
        addItem(ObjHandler.rmHammer, PageCategory.TOOLS);
        addItem(ObjHandler.rmKatar, PageCategory.TOOLS);
        addItem(ObjHandler.rmStar, PageCategory.TOOLS);
        addItem(ObjHandler.dmHelmet, PageCategory.ARMOR);
        addItem(ObjHandler.dmChest, PageCategory.ARMOR);
        addItem(ObjHandler.dmLegs, PageCategory.ARMOR);
        addItem(ObjHandler.dmFeet, PageCategory.ARMOR);
        addItem(ObjHandler.rmHelmet, PageCategory.ARMOR);
        addItem(ObjHandler.rmChest, PageCategory.ARMOR);
        addItem(ObjHandler.rmLegs, PageCategory.ARMOR);
        addItem(ObjHandler.rmFeet, PageCategory.ARMOR);
        addItem(ObjHandler.gemHelmet, PageCategory.ARMOR);
        addItem(ObjHandler.gemChest, PageCategory.ARMOR);
        addItem(ObjHandler.gemLegs, PageCategory.ARMOR);
        addItem(ObjHandler.gemFeet, PageCategory.ARMOR);
        addItem(ObjHandler.ironBand, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.blackHole, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.angelSmite, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.harvestGod, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.ignition, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.zero, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.swrg, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.timeWatch, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.everTide, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.volcanite, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.eternalDensity, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.dRod1, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.dRod2, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.dRod3, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.mercEye, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.voidRing, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.arcana, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.dCatalyst, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.hyperLens, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.cataliticLens, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.bodyStone, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.soulStone, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.mindStone, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.lifeStone, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.tome, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.transmutationTablet, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);

        // Items with different meta forms
        addItem(ObjHandler.matter, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.fuels, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.covalence, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItem(ObjHandler.kleinStars, PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);

        for (List<AbstractPage> categoryPages : categoryMap.values())
        {
            Collections.sort(categoryPages, (o1, o2) -> I18n.format(o1.getHeaderText()).compareToIgnoreCase(I18n.format(o2.getHeaderText())));
            for (AbstractPage page : categoryPages)
            {
                pages.add(page);
            }
        }
        PELogger.logDebug("Built %d standard pages", pages.size());
        generateDummyIndexPages();
        buildPageSpreads();
    }

    private static void generateDummyIndexPages()
    {
        List<IndexPage> toAdd = Lists.newArrayList();
        int numIndexPages = Math.round(((float) ManualPageHandler.pages.size()) / GUIManual.ENTRIES_PER_PAGE);
        PELogger.logDebug("" + (float) ManualPageHandler.pages.size() / GUIManual.ENTRIES_PER_PAGE);
        for (int i = 0; i < numIndexPages; i++)
        {
            toAdd.add(new IndexPage());
        }
        indexPages.addAll(toAdd);
        pages.addAll(0, indexPages);
        PELogger.logDebug("Built %d dummy index pages", indexPages.size());
    }

    private static void buildPageSpreads()
    {
        int firstNormalPage = 0;
        for (AbstractPage page : pages)
        {
            if (!(page instanceof IndexPage))
            {
                firstNormalPage = pages.indexOf(page);
                break;
            }
        }

        // Build index and normal spreads separately
        doBuildSpread(pages.subList(0, firstNormalPage));
        PELogger.logDebug("Built %d index spreads", spreads.size());
        doBuildSpread(pages.subList(firstNormalPage, pages.size()));
        PELogger.logDebug("Built %d spreads total", spreads.size());
    }

    private static void doBuildSpread(List<AbstractPage> list)
    {
        for (int i = 0; i < list.size(); i += 2)
        {
            if (i == list.size() - 1)
            {
                // Handle last page being odd
                spreads.add(ImmutablePair.of(list.get(i), ((AbstractPage) null)));
                continue;
            }
            spreads.add(ImmutablePair.of(list.get(i), list.get(i + 1)));
        }
    }

    private static void addItem(Item item, PageCategory category)
    {
        // Manually exclude alchBag from having 16 of the same entry
        List<ItemStack> list = (item == ObjHandler.alchBag || !item.getHasSubtypes()) ? Collections.singletonList(new ItemStack(item)) : getSubItems(item);

        for (ItemStack s : list)
        {
            AbstractPage page = AbstractPage.createItemPage(s, category);
            categoryMap.get(category).add(page);
            categoryMap.get(category).addAll(page.subPages);
            PELogger.logDebug("Added %d item pages for stack %s", page.subPages.size() + 1, s.toString());
        }
    }

    private static void addBlock(Block block, PageCategory category)
    {
        addItem(Item.getItemFromBlock(block), category);
    }

    private static void addTextPage(String identifier, PageCategory category)
    {
        AbstractPage page = AbstractPage.createTextPages(identifier, category);
        categoryMap.get(category).add(page);
        categoryMap.get(category).addAll(page.subPages);
        PELogger.logDebug("Added %d text pages for identifier %s", page.subPages.size() + 1, identifier);
    }

    private static void addImagePage(String identifier, ResourceLocation resource, PageCategory category)
    {
        AbstractPage page = AbstractPage.createImagePage(identifier, resource, category);
        categoryMap.get(category).add(page);
    }

    private static List<ItemStack> getSubItems(Item i)
    {
        List<ItemStack> list = Lists.newArrayList();
        i.getSubItems(i, null, list);
        return list;
    }
}
