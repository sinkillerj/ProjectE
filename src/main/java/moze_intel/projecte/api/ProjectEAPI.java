package moze_intel.projecte.api;

import cpw.mods.fml.common.FMLLog;

/**
 * Class for basic mod interactions with ProjectE.<br>
 * For now, it's very simplistic, will be expanded in the future.<br>
 */
public final class ProjectEAPI
{
	public IEMCProxy getEMCProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.EMCProxyImpl");
			return (IEMCProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.info("[ProjectEAPI] Error retrieving EMCProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}

	public ITransmutationProxy getTransmutationProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.TransmutationProxyImpl");
			return (ITransmutationProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.info("[ProjectEAPI] Error retrieving TransmutationProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}

	public IExtraProxy getMiscellaneousProxy()
	{
		try
		{
			Class<?> clazz = Class.forName("moze_intel.projecte.ExtraProxyImpl");
			return (IExtraProxy) clazz.getField("instance").get(null);
		} catch (ReflectiveOperationException ex)
		{
			FMLLog.info("[ProjectEAPI] Error retrieving ExtraProxyImpl, ProjectE may be absent, damaged, or outdated.");
		}
		return null;
	}
}
