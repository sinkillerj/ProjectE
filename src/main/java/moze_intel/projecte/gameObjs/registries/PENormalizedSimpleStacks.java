package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectERegistries;
import moze_intel.projecte.api.codec.NSSCodecHolder;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;

public class PENormalizedSimpleStacks {

	private PENormalizedSimpleStacks() {
	}

	public static final PEDeferredRegister<NSSCodecHolder<?>> NSS_SERIALIZERS = new PEDeferredRegister<>(ProjectERegistries.NSS_SERIALIZER_NAME, PECore.MODID);

	public static final PEDeferredHolder<NSSCodecHolder<?>, NSSCodecHolder<NSSItem>> ITEM = NSS_SERIALIZERS.register("item", () -> NSSItem.CODECS);
	public static final PEDeferredHolder<NSSCodecHolder<?>, NSSCodecHolder<NSSFluid>> FLUID = NSS_SERIALIZERS.register("fluid", () -> NSSFluid.CODECS);
	public static final PEDeferredHolder<NSSCodecHolder<?>, NSSCodecHolder<NSSFake>> FAKE = NSS_SERIALIZERS.register("fake", () -> NSSFake.CODECS);
}