package moze_intel.projecte.manual;

import net.minecraft.util.StatCollector;

public class TextPage extends PEManualPage
{
    private final String header;
    protected TextPage(String identifier)
    {
        super(EnumPageType.TEXTPAGE);
        this.header = identifier;
    }

    @Override
    public String getHeaderText()
    {
        //TODO change key
        return StatCollector.translateToLocal("pe.manual.title." + header);
    }

    @Override
    public String getBodyText()
    {
        //TODO change key and var name
        return StatCollector.translateToLocal("pe.manual." + header);
    }
}
