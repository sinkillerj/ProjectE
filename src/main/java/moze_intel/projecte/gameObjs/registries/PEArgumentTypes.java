package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.ArgumentTypeInfoDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ArgumentTypeInfoRegistryObject;
import moze_intel.projecte.network.commands.argument.ColorArgument;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;

public class PEArgumentTypes {

	public static final ArgumentTypeInfoDeferredRegister ARGUMENT_TYPES = new ArgumentTypeInfoDeferredRegister(PECore.MODID);

	public static final ArgumentTypeInfoRegistryObject<ColorArgument> COLOR = ARGUMENT_TYPES.registerContextFree("color", ColorArgument.class, ColorArgument::color);
	public static final ArgumentTypeInfoRegistryObject<NSSItemArgument> NSS = ARGUMENT_TYPES.registerContextAware("nss", NSSItemArgument.class, NSSItemArgument::nss);
}