package moze_intel.projecte.manual;

import net.minecraft.util.StatCollector;

public class TextPage extends AbstractPage
{
    private final String header;

    protected TextPage(String identifier, PageCategory category)
    {
        super(category);
        this.header = identifier;
    }

    @Override
    public String getHeaderText()
    {
        return StatCollector.translateToLocal("pe.manual." + header + ".header");
    }

    @Override
    public String getBodyText()
    {
        return StatCollector.translateToLocal("pe.manual." + header);
    }

}
