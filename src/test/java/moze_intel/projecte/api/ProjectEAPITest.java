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
	public void testGetConversionProxy() throws Exception
	{
		assertNotNull(ProjectEAPI.getConversionProxy());
	}

	@Test
	public void testGetTransmutationProxy() throws Exception
	{
		assertNotNull(ProjectEAPI.getTransmutationProxy());
	}

	@Test
	public void testGetBlacklistProxy() throws Exception
	{
		assertNotNull(ProjectEAPI.getBlacklistProxy());
	}
}