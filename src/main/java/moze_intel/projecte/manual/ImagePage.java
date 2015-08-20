package moze_intel.projecte.manual;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class ImagePage extends AbstractPage
{
    private final ResourceLocation imageLocation;
    private final String header;

    protected ImagePage(String header, ResourceLocation imageLocation, PageCategory category)
    {
        super(category);
        this.header = header;
        this.imageLocation = imageLocation;
        this.setIndexed(false);
    }

    public ResourceLocation getImageLocation()
    {
        return this.imageLocation;
    }

    @Override
    public String getHeaderText()
    {
        return StatCollector.translateToLocal("pe.manual." + header + ".header");
    }

    @Override
    public String getBodyText()
    {
        return "";
    }

}
