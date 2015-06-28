package moze_intel.projecte.api;

import cpw.mods.fml.common.FMLLog;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;

public final class ProjectEAPI
{
	/**
	 * @return The proxy for EMC-based API queries
	 */
	public static IEMCProxy getEMCProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.proxies.EMCProxyImpl");
			return (IEMCProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.info("[ProjectEAPI] Error retrieving EMCProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}

	/**
	 * @return The proxy for Transmutation-based API queries
	 */
	public static ITransmutationProxy getTransmutationProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.proxies.TransmutationProxyImpl");
			return (ITransmutationProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.info("[ProjectEAPI] Error retrieving TransmutationProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}

	/**
	 * @return The proxy for whitelisting or blacklisting things
	 */
	public static IBlacklistProxy getBlacklistProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.proxies.BlacklistProxyImpl");
			return (IBlacklistProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.info("[ProjectEAPI] Error retrieving BlacklistProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}
}
