package moze_intel.projecte.manual;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        return I18n.translateToLocal("pe.manual." + header + ".header");
    }

    @Override
    public String getBodyText()
    {
        return text;
    }

}
