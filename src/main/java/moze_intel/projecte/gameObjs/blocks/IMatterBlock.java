package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.EnumMatterType;

public interface IMatterBlock {

	/**
	 * Gets the matter type this block is made of/is needed to break.
	 */
	EnumMatterType getMatterType();
}