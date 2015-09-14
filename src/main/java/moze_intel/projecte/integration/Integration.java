package moze_intel.projecte.integration;

import codechicken.nei.api.API;
// todo 1.8 restore when MT updates import moze_intel.projecte.integration.MineTweaker.TweakInit;
import moze_intel.projecte.integration.NEI.NEIAlchBagHandler;
import moze_intel.projecte.integration.NEI.NEIKleinStarHandler;
import moze_intel.projecte.integration.NEI.NEIPhiloSmeltingHandler;
import moze_intel.projecte.integration.NEI.NEIWorldTransmuteHandler;
import net.minecraftforge.fml.common.Loader;
import moze_intel.projecte.integration.NEI.NEIInit;

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
				// todo 1.8 restore when MT updates TweakInit.init();
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		if (NEI)
		{

			try
			{
				NEIInit.init();
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
