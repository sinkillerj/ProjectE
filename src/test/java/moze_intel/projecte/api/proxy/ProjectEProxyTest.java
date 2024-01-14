package moze_intel.projecte.api.proxy;

import moze_intel.projecte.api.codec.IPECodecHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test ProjectE's Proxies")
class ProjectEProxyTest {

	@Test
	@DisplayName("Test getting the EMC Proxy")
	void testGetEMCProxy() {
		Assertions.assertNotNull(IEMCProxy.INSTANCE);
	}

	@Test
	@DisplayName("Test getting the Transmutation Proxy")
	void testGetTransmutationProxy() {
		Assertions.assertNotNull(ITransmutationProxy.INSTANCE);
	}

	@Test
	@DisplayName("Test getting the Codec Helper")
	void testGetCodecHelper() {
		Assertions.assertNotNull(IPECodecHelper.INSTANCE);
	}
}