package moze_intel.projecte.api;

import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.FMLLog;

public final class ProjectEAPI
{
	private static IEMCProxy emcProxy;
	private static ITransmutationProxy transProxy;
	private static IBlacklistProxy blacklistProxy;
	private static IConversionProxy recipeProxy;

	private ProjectEAPI() {}

	/**
	 * The capability object for IAlchBagProvider
	 */
	@CapabilityInject(IAlchBagProvider.class)
	public static final Capability<IAlchBagProvider> ALCH_BAG_CAPABILITY = null;

	/**
	 * The capability object for IKnowledgeProvider
	 */
	@CapabilityInject(IKnowledgeProvider.class)
	public static final Capability<IKnowledgeProvider> KNOWLEDGE_CAPABILITY = null;

	/**
	 * Retrieves the proxy for EMC-based API queries.
	 * @return The proxy for EMC-based API queries
	 */
	public static IEMCProxy getEMCProxy()
	{
		if (emcProxy == null)
		{
			try
			{
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.EMCProxyImpl");
				emcProxy = (IEMCProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex)
			{
				FMLLog.warning("[ProjectEAPI] Error retrieving EMCProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return emcProxy;
	}

	/**
	 * Retrieves the proxy for EMC-Recipe-Calculation-based API queries.
	 * @return The proxy for EMC-Recipe-Calculation-based API queries
	 */
	public static IConversionProxy getConversionProxy()
	{
		if (recipeProxy == null)
		{
			try
			{
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.ConversionProxyImpl");
				recipeProxy = (IConversionProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex)
			{
				FMLLog.warning("[ProjectEAPI] Error retrieving ConversionProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return recipeProxy;
	}

	/**
	 * Retrieves the proxy for Transmutation-based API queries.
	 * @return The proxy for Transmutation-based API queries
	 */
	public static ITransmutationProxy getTransmutationProxy()
	{
		if (transProxy == null)
		{
			try
			{
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.TransmutationProxyImpl");
				transProxy = (ITransmutationProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex)
			{
				FMLLog.warning("[ProjectEAPI] Error retrieving TransmutationProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return transProxy;
	}

	/**
	 * Retrieves the proxy for black/whitelist-based API queries.
	 * @return The proxy for black/whitelist-based API queries
	 */
	public static IBlacklistProxy getBlacklistProxy()
	{
		if (blacklistProxy == null)
		{
			try
			{
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.BlacklistProxyImpl");
				blacklistProxy = (IBlacklistProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex)
			{
				FMLLog.warning("[ProjectEAPI] Error retrieving BlacklistProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return blacklistProxy;
	}
}