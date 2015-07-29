package moze_intel.projecte.manual;

import java.util.List;

import moze_intel.projecte.gameObjs.gui.GUIManual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ImagePage extends AbstractPage
{
    private final ResourceLocation imageLocation;
    private final String header;
    private FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

    protected ImagePage(String header, ResourceLocation imageLocation, PageCategory category)
    {
        super(category);
        this.header = header;
        this.imageLocation = imageLocation;
    }

    @Override
    public boolean shouldAppearInIndex()
    {
        return false;
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

	@Override
	public List<String> getBodyList() {
		return fontRendererObj.listFormattedStringToWidth(getBodyText(), GUIManual.textWidth);
	}
}
