package moze_intel.projecte.manual;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.gui.GUIManual;
import moze_intel.projecte.utils.Comparators;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ManualPageHandler
{
    public static final List<AbstractPage> pages = Lists.newArrayList();
    public static final Map<PageCategory, List<AbstractPage>> categoryMap = Maps.newEnumMap(PageCategory.class);

    public static void init()
    {
        for (PageCategory e : PageCategory.values())
        {
            categoryMap.put(e, Lists.<AbstractPage>newArrayList());
        }

        addTextPage("introduction", PageCategory.NONE);

        //Blocks
        addItem(ObjHandler.alchChest, PageCategory.BLOCK);
        addImagePage("img_alchchest", new ResourceLocation("projecte:textures/gui/alchchest.png"), PageCategory.BLOCK);
        addItem(ObjHandler.confuseTorch, PageCategory.BLOCK);
        addItem(ObjHandler.transmuteStone, PageCategory.BLOCK);
        addItem(ObjHandler.condenser, PageCategory.BLOCK);
        addItem(ObjHandler.condenserMk2, PageCategory.BLOCK);
        addItem(ObjHandler.rmFurnaceOff, PageCategory.BLOCK);
        addItem(ObjHandler.dmFurnaceOff, PageCategory.BLOCK);
        addItem(ObjHandler.dmPedestal, PageCategory.BLOCK);
        addItem(ObjHandler.energyCollector, PageCategory.BLOCK);
        addItem(ObjHandler.collectorMK2, PageCategory.BLOCK);
        addItem(ObjHandler.collectorMK3, PageCategory.BLOCK);
        addItem(ObjHandler.relay, PageCategory.BLOCK);
        addItem(ObjHandler.relayMK2, PageCategory.BLOCK);
        addItem(ObjHandler.relayMK3, PageCategory.BLOCK);
        addItem(ObjHandler.novaCatalyst, PageCategory.BLOCK);
        addItem(ObjHandler.novaCataclysm, PageCategory.BLOCK);

        // Blocks with different meta forms
        addItemAndSubs(getSubItems(ObjHandler.matterBlock), PageCategory.BLOCK);
        addItemAndSubs(getSubItems(ObjHandler.fuelBlock), PageCategory.BLOCK);

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
        addItemAndSubs(getSubItems(ObjHandler.matter), PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItemAndSubs(getSubItems(ObjHandler.fuels), PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItemAndSubs(getSubItems(ObjHandler.covalence), PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);
        addItemAndSubs(getSubItems(ObjHandler.kleinStars), PageCategory.MUSTFIGUREOUTTHERESTOFTHESE);

        registerAll();
    }

    private static void addItem(Item item, PageCategory category)
    {
        AbstractPage page = AbstractPage.createItemPage(item, category);
        categoryMap.get(category).add(page);
        checkSubPages(page, category, new ItemStack(item).getUnlocalizedName() + ".name");
    }

    private static void addItem(Block block, PageCategory category)
    {
        AbstractPage page = AbstractPage.createItemPage(block, category);
        categoryMap.get(category).add(page);
        checkSubPages(page, category, new ItemStack(block).getUnlocalizedName() + ".name");
    }

    private static void addItemAndSubs(List<ItemStack> list, PageCategory category)
    {
        for (ItemStack is : list)
        {
            AbstractPage page = AbstractPage.createItemPage(is, category);
            categoryMap.get(category).add(page);
            checkSubPages(page, category, is.getUnlocalizedName() + ".name");
        }
    }

    private static void addTextPage(String identifier, PageCategory category)
    {
        AbstractPage page = AbstractPage.createTextPage(identifier, category);
        categoryMap.get(category).add(page);
        checkSubPages(page, category, "pe.manual." + identifier + ".header");
    }

    private static void addImagePage(String identifier, ResourceLocation resource, PageCategory category)
    {
        AbstractPage page = AbstractPage.createImagePage(identifier, resource, category);
        categoryMap.get(category).add(page);
        checkSubPages(page, category, identifier);
    }

    private static void addSubPage(List<String> text, PageCategory category, String identifier, int i)
    {
        AbstractPage page = AbstractPage.createSubPage(text, category, identifier, i);
        categoryMap.get(category).add(page);
    }

    private static void checkSubPages(AbstractPage page, PageCategory category, String identifier)
    {
        int neededPages = (int) Math.ceil((page.getBodyList().size() * GUIManual.textYOffset) / GUIManual.textHeight);
        int k = 0;
        if (neededPages > 0)
        {
            List<List<String>> parts = chopped(page.getBodyList(), (int) Math.floor(GUIManual.textHeight / GUIManual.textYOffset));
            for (int i = 1; i <= neededPages; i++)
            {
                addSubPage(parts.get(i), category, identifier, k++);
            }
        }
    }

    /**
     * Iterates through all categories in enum order, sorts the list
     * alphabetically by localized header, then adds them to the page list
     */
    private static void registerAll()
    {
        for (List<AbstractPage> categoryPages : categoryMap.values())
        {
            Collections.sort(categoryPages, Comparators.PAGE_HEADER);
            for (AbstractPage page : categoryPages)
            {
                pages.add(page);
            }
        }
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

    // chops a list into non-view sublists of length L
    private static <T> List<List<T>> chopped(List<T> list, final int L)
    {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L)
        {
            parts.add(new ArrayList<T>(
                            list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }
}
