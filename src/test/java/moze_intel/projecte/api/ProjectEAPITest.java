package moze_intel.projecte.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test ProjectE's API")
class ProjectEAPITest
{
	@Test
	@DisplayName("Test getting the EMC Proxy")
	void testGetEMCProxy()
	{
		Assertions.assertNotNull(ProjectEAPI.getEMCProxy());
	}

	@Test
	@DisplayName("Test getting the Transmutation Proxy")
	void testGetTransmutationProxy()
	{
		Assertions.assertNotNull(ProjectEAPI.getTransmutationProxy());
	}
}