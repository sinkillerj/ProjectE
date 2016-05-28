package moze_intel.projecte.manual;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IndexPage extends AbstractPage
{
    protected IndexPage()
    {
        super(PageCategory.INDEX);
        this.setIndexed(false);
    }

    @Override
    public String getBodyText()
    {
        return "";
    }

    @Override
    public String getHeaderText()
    {
        return I18n.format("pe.manual.index");
    }
}
