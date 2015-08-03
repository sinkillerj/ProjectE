package moze_intel.projecte.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class IndexPage extends AbstractPage
{
    public final int id;
    protected IndexPage(int index)
    {
        super(PageCategory.INDEX);
        this.id = index;
    }

    @Override
    public String getBodyText()
    {
        return "";
    }

    @Override
    public String getHeaderText()
    {
        return StatCollector.translateToLocal("pe.manual.index");
    }
}
