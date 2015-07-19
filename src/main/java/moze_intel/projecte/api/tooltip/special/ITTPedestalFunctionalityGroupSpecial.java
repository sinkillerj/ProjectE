package moze_intel.projecte.api.tooltip.special;

import moze_intel.projecte.api.tooltip.ITTPedestalFunctionalityGroup;

import java.util.List;

public interface ITTPedestalFunctionalityGroupSpecial extends ITTPedestalFunctionalityGroup, ITTShiftDetailView
{

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief string describing item function in pedestal.
	 */
	List<String> getPedestalDescription();
}
