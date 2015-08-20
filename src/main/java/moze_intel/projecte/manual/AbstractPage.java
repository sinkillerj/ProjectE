package moze_intel.projecte.manual;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.gui.GUIManual;
import moze_intel.projecte.utils.CollectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class AbstractPage
{
    protected final PageCategory category;
    protected AbstractPage parent = null;
    protected List<AbstractPage> subPages = Lists.newArrayList();
    private boolean indexed = true;

    protected AbstractPage(PageCategory category)
    {
        this.category = category;
    }

    public boolean shouldAppearInIndex()
    {
        return indexed;
    }

    public AbstractPage setIndexed(boolean flag)
    {
        indexed = flag;
        return this;
    }

    public abstract String getBodyText();

    public abstract String getHeaderText();

    public void addSubPage(AbstractPage page)
    {
        if (parent != null)
        {
            throw new UnsupportedOperationException("Cannot nest pages deeper than 1 level!");
        }
        page.parent = this;
        subPages.add(page);
    }

    public static AbstractPage createItemPage(ItemStack stack, PageCategory category)
    {
        String body = StatCollector.translateToLocal("pe.manual." + stack.getUnlocalizedName().substring(5));
        List<List<String>> parts = CollectionHelper.splitToLength(GUIManual.splitBody(body), GUIManual.TEXT_HEIGHT / GUIManual.TEXT_Y_OFFSET);
        AbstractPage ret = new ItemPage(stack.copy(), category, StringUtils.join(parts.get(0), ""));
        for (int i = 1; i < parts.size(); i++)
        {
            ret.addSubPage(new ItemPage(stack.copy(), category, StringUtils.join(parts.get(i), "")).setIndexed(false));
        }
        return ret;
    }

    public static AbstractPage createTextPages(String identifier, PageCategory category)
    {
        String body = StatCollector.translateToLocal("pe.manual." + identifier);
        List<List<String>> parts = CollectionHelper.splitToLength(GUIManual.splitBody(body), GUIManual.TEXT_HEIGHT / GUIManual.TEXT_Y_OFFSET);
        AbstractPage ret = new TextPage(identifier, category, StringUtils.join(parts.get(0), ""));
        for (int i = 1; i < parts.size(); i++)
        {
            ret.addSubPage(new TextPage(identifier, category, StringUtils.join(parts.get(i), "")).setIndexed(false));
        }
        return ret;
    }

    public static AbstractPage createImagePage(String header, ResourceLocation imageLocation, PageCategory category)
    {
        return new ImagePage(header, imageLocation, category);
    }
}
