package moze_intel.projecte.api;

import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;

public final class ProjectEAPI {

	public static final String PROJECTE_MODID = "projecte";

	private ProjectEAPI() {
	}

	/**
	 * Retrieves the proxy for EMC-based API queries.
	 *
	 * @return The proxy for EMC-based API queries
	 *
	 * @deprecated Use {@link IEMCProxy#INSTANCE} instead.
	 */
	@Deprecated(forRemoval = true, since = "MC 1.20.1")
	public static IEMCProxy getEMCProxy() {
		return IEMCProxy.INSTANCE;
	}

	/**
	 * Retrieves the proxy for Transmutation-based API queries.
	 *
	 * @return The proxy for Transmutation-based API queries
	 *
	 * @deprecated Use {@link ITransmutationProxy#INSTANCE} instead.
	 */
	@Deprecated(forRemoval = true, since = "MC 1.20.1")
	public static ITransmutationProxy getTransmutationProxy() {
		return ITransmutationProxy.INSTANCE;
	}
}