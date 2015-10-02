package moze_intel.projecte.integration.NEI;

import codechicken.nei.api.API;

public class NEIInit
{

	public static void init()
	{
		API.registerRecipeHandler(new NEIWorldTransmuteHandler());
		API.registerUsageHandler(new NEIWorldTransmuteHandler());
		API.registerRecipeHandler(new NEIPhiloSmeltingHandler());
		API.registerUsageHandler(new NEIPhiloSmeltingHandler());
		API.registerRecipeHandler(new NEIKleinStarHandler());
		API.registerUsageHandler(new NEIKleinStarHandler());
		API.registerRecipeHandler(new NEIAlchBagHandler());
		API.registerUsageHandler(new NEIAlchBagHandler());
	}
}
