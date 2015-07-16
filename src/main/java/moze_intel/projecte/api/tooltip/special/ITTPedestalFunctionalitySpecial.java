package moze_intel.projecte.api.tooltip.special;

import moze_intel.projecte.api.tooltip.ITTPedestalFunctionality;

import java.util.List;

public interface ITTPedestalFunctionalitySpecial extends ITTPedestalFunctionality
{

	/***
	 * Called clientside when inside the pedestal gui to add special function descriptions
	 * @return Brief string describing item function in pedestal.
	 */
	List<String> getPedestalDescription();
}
