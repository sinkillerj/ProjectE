package moze_intel.projecte.api.tooltip.keybinds;

import moze_intel.projecte.api.tooltip.ITTBaseFunctionality;
import moze_intel.projecte.api.tooltip.special.ITTGeneralFunctionality;

public interface ITTKeybind extends ITTGeneralFunctionality
{
	public String getTooltipLocalisationPrefix();
}
