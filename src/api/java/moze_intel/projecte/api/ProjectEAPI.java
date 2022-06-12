package moze_intel.projecte.api;

import com.mojang.logging.LogUtils;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import org.slf4j.Logger;

public final class ProjectEAPI {

	private static IEMCProxy emcProxy;
	private static ITransmutationProxy transProxy;
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String PROJECTE_MODID = "projecte";

	private ProjectEAPI() {
	}

	/**
	 * Retrieves the proxy for EMC-based API queries.
	 *
	 * @return The proxy for EMC-based API queries
	 */
	public static IEMCProxy getEMCProxy() {
		// Harmless race
		if (emcProxy == null) {
			try {
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.EMCProxyImpl");
				emcProxy = (IEMCProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex) {
				LOGGER.warn("Error retrieving EMCProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return emcProxy;
	}

	/**
	 * Retrieves the proxy for Transmutation-based API queries.
	 *
	 * @return The proxy for Transmutation-based API queries
	 */
	public static ITransmutationProxy getTransmutationProxy() {
		// Harmless race
		if (transProxy == null) {
			try {
				Class<?> clazz = Class.forName("moze_intel.projecte.impl.TransmutationProxyImpl");
				transProxy = (ITransmutationProxy) clazz.getField("instance").get(null);
			} catch (ReflectiveOperationException ex) {
				LOGGER.warn("Error retrieving TransmutationProxyImpl, ProjectE may be absent, damaged, or outdated.");
			}
		}
		return transProxy;
	}
}