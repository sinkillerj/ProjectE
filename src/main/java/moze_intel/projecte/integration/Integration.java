package moze_intel.projecte.integration;

// todo 1.8 restore when MT updates import moze_intel.projecte.integration.MineTweaker.TweakInit;

import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.fml.common.Loader;

public class Integration
{
	// Single class to initiate different mod compatibilities. Idea came from Avaritia by SpitefulFox

	public static boolean mtweak = false;

	public static void modChecks()
	{
		mtweak = Loader.isModLoaded("MineTweaker3");
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
	}
}
