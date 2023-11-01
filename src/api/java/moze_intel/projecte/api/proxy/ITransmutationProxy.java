package moze_intel.projecte.api.proxy;

import java.util.ServiceLoader;
import java.util.UUID;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import org.jetbrains.annotations.NotNull;

public interface ITransmutationProxy {

	/**
	 * The proxy for Transmutation-based API queries.
	 */
	ITransmutationProxy INSTANCE = ServiceLoader.load(ITransmutationProxy.class).findFirst()
			.orElseThrow(() -> new IllegalStateException("No valid ServiceImpl for ITransmutationProxy found, ProjectE may be absent, damaged, or outdated"));

	/**
	 * Gets an {@link IKnowledgeProvider} representing the UUID provided.
	 * <p>
	 * If the provided UUID is offline, note that the returned {@link IKnowledgeProvider} is immutable! If called clientside, {@param playerUUID} is ignored and the
	 * client player is used instead. If called serverside, this must be called after the server has reached state SERVER_STARTED.
	 * <p>
	 * If the provided UUID could not be found both on or offline, an {@link IKnowledgeProvider} with no knowledge is returned.
	 *
	 * @param playerUUID The UUID to query
	 *
	 * @return an {@link IKnowledgeProvider} representing the UUID provided, or an {@link IKnowledgeProvider} representing no knowledge if the requested UUID could not be
	 * found
	 */
	@NotNull
	IKnowledgeProvider getKnowledgeProviderFor(@NotNull UUID playerUUID);
}