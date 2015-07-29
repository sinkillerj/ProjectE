package moze_intel.projecte.manual;

import java.util.List;

import moze_intel.projecte.gameObjs.gui.GUIManual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;

public class TextPage extends AbstractPage
{
    private final String header;
    private FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer; // IS NULL FOR SOME BLASTED REASON
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

	@Override
	public List<String> getBodyList() {
		return fontRendererObj.listFormattedStringToWidth(getBodyText(), GUIManual.textWidth);
	}
}
