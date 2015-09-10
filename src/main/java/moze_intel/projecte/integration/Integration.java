package moze_intel.projecte.integration;

import codechicken.nei.api.API;
import cpw.mods.fml.common.Loader;
import moze_intel.projecte.integration.MineTweaker.TweakInit;
import moze_intel.projecte.integration.NEI.NEIAlchBagHandler;
import moze_intel.projecte.integration.NEI.NEIKleinStarHandler;
import moze_intel.projecte.integration.NEI.NEIPhiloSmeltingHandler;
import moze_intel.projecte.integration.NEI.NEIWorldTransmuteHandler;

public class Integration
{
	// Single class to initiate different mod compatibilities. Idea came from Avaritia by SpitefulFox

	public static boolean mtweak = false;
	public static boolean NEI = false;

	public static void modChecks()
	{
		mtweak = Loader.isModLoaded("MineTweaker3");
		NEI = Loader.isModLoaded("NotEnoughItems");
	}

	public static void init()
	{
		modChecks();

		if (mtweak)
		{
			try
			{
				TweakInit.init();
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		if (NEI)
		{

			try
			{
				API.registerRecipeHandler(new NEIWorldTransmuteHandler());
				API.registerUsageHandler(new NEIWorldTransmuteHandler());
				API.registerRecipeHandler(new NEIPhiloSmeltingHandler());
				API.registerUsageHandler(new NEIPhiloSmeltingHandler());
				API.registerRecipeHandler(new NEIKleinStarHandler());
				API.registerUsageHandler(new NEIKleinStarHandler());
				API.registerRecipeHandler(new NEIAlchBagHandler());
				API.registerUsageHandler(new NEIAlchBagHandler());
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
