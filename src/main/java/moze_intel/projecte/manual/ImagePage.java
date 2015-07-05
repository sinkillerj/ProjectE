package moze_intel.projecte.manual;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ImagePage extends PEManualPage
{
    private final ResourceLocation imageLocation;
    private final String header;

    protected ImagePage(String header, ResourceLocation imageLocation)
    {
        super(EnumPageType.IMAGEPAGE);
        this.header = header;
        this.imageLocation = imageLocation;
    }

    public ResourceLocation getImageLocation()
    {
        return this.imageLocation;
    }

    @Override
    public String getHeaderText()
    {
        // TODO change key
        return StatCollector.translateToLocal("pe.manual.title." + header);
    }

    @Override
    public String getBodyText()
    {
        return "";
    }
}
