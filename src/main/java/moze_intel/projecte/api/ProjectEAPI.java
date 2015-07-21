package moze_intel.projecte.api;

import cpw.mods.fml.common.FMLLog;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;

public final class ProjectEAPI
{
	/**
	 * Retrieves the proxy for EMC-based API queries.
	 * Hold on to this object after you get it, as this getter uses reflection and repeated calls may slow down your mod.
	 * @return The proxy for EMC-based API queries
	 */
	public static IEMCProxy getEMCProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.impl.EMCProxyImpl");
			return (IEMCProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.warning("[ProjectEAPI] Error retrieving EMCProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}

	/**
	 * Retrieves the proxy for Transmutation-based API queries.
	 * Hold on to this object after you get it, as this getter uses reflection and repeated calls may slow down your mod.
	 * @return The proxy for Transmutation-based API queries
	 */
	public static ITransmutationProxy getTransmutationProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.impl.TransmutationProxyImpl");
			return (ITransmutationProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.warning("[ProjectEAPI] Error retrieving TransmutationProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}

	/**
	 * Retrieves the proxy for black/whitelist-based API queries.
	 * Hold on to this object after you get it, as this getter uses reflection and repeated calls may slow down your mod.
	 * @return The proxy for black/whitelist-based API queries
	 */
	public static IBlacklistProxy getBlacklistProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.impl.BlacklistProxyImpl");
			return (IBlacklistProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.warning("[ProjectEAPI] Error retrieving BlacklistProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}
}
