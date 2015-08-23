package moze_intel.projecte.integration;

import minetweaker.MineTweakerAPI;

public class PEMineTweaker
{
	public static void init()
	{
		MineTweakerAPI.registerClass(WorldTransmutation.class);
		MineTweakerAPI.registerClass(HiddenShapeless.class);
		MineTweakerAPI.registerClass(KleinStar.class);
		MineTweakerAPI.registerClass(AlchBag.class);
	}

}
