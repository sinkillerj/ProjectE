package moze_intel.projecte.manual;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SubPage extends AbstractPage {
	
	private final List<String> splitText;
	private final String bodyText;
	//To add header simply add it in constructor
	protected SubPage(List<String> bodyTexts, PageCategory category) {
		super(category);
		this.splitText = bodyTexts;
		this.bodyText = StringUtils.join(bodyTexts, "");
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
		return "";
	}

}
