package moze_intel.projecte.integration;

import moze_intel.projecte.integration.minetweaker.TweakInit;
import net.minecraftforge.fml.ModList;

public class Integration
{
	// Single class to initiate different mod compatibilities. Idea came from Avaritia by SpitefulFox

	public static boolean mtweak = false;

	public static void modChecks()
	{
		mtweak = ModList.get().isLoaded("minetweaker3");
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
	}
}
