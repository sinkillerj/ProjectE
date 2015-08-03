package moze_intel.projecte.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class TextPage extends AbstractPage
{
    private final String header;
    private final String text;

    protected TextPage(String identifier, PageCategory category, String text)
    {
        super(category);
        this.header = identifier;
        this.text = text;
    }

    @Override
    public String getHeaderText()
    {
        return StatCollector.translateToLocal("pe.manual." + header + ".header");
    }

    @Override
    public String getBodyText()
    {
        return text;
    }

}
