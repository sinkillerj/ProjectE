package moze_intel.projecte.api.imc;

import moze_intel.projecte.api.nss.NSSCreator;

public class NSSCreatorInfo {

	private final String key;
	private final NSSCreator creator;

	public NSSCreatorInfo(String key, NSSCreator creator) {
		this.key = key;
		this.creator = creator;
	}

	public String getKey() {
		return key;
	}

	public NSSCreator getCreator() {
		return creator;
	}
}