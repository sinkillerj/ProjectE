package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.EnumMatterType;

public class RedHoe extends DarkHoe {

	public RedHoe(Properties props) {
		super(props, (byte) 3, new String[]{});
		this.peToolMaterial = EnumMatterType.RED_MATTER;
	}
}