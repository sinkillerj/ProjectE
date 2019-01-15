package moze_intel.projecte.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProjectEAPITest
{
	@Test
	public void testGetEMCProxy() throws Exception
	{
		assertNotNull(ProjectEAPI.getEMCProxy());
	}

	@Test
	public void testGetTransmutationProxy() throws Exception
	{
		assertNotNull(ProjectEAPI.getTransmutationProxy());
	}
}