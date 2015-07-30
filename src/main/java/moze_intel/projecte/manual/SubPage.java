package moze_intel.projecte.manual;

import java.util.List;

import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.StringUtils;

public class SubPage extends AbstractPage {
	
	private final List<String> splitText;
	private final String bodyText;
	private final String header;
	private final int i;

	protected SubPage(List<String> bodyTexts, PageCategory category, String header, int i) {
		super(category);
		this.splitText = bodyTexts;
		this.bodyText = StringUtils.join(bodyTexts, "");
		this.header = header;
		this.i = i;
	}
	
    @Override
    public boolean shouldAppearInIndex()
    {
        return false;
    }

	@Override
	public String getBodyText() {
		return this.bodyText;
	}

	@Override
	public String getHeaderText() {
		return StatCollector.translateToLocal(header) + "_" + this.i;
		
	}

}
