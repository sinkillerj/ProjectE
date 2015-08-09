package moze_intel.projecte.manual;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
