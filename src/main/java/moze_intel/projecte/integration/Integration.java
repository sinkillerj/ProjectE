package moze_intel.projecte.integration;

import cpw.mods.fml.common.Loader;
import moze_intel.projecte.integration.MineTweaker.TweakInit;
import moze_intel.projecte.integration.NEI.NEIInit;
import moze_intel.projecte.utils.PELogger;

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
				NEIInit.init();
			} catch (NoClassDefFoundError e)
			{
				PELogger.logWarn("NEI integration not loaded due to server side being detected");
			} catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
