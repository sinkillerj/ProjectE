package moze_intel.projecte.impl;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.UUID;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class TransmutationProxyImpl implements ITransmutationProxy {

	@NotNull
	@Override
	public IKnowledgeProvider getKnowledgeProviderFor(@NotNull UUID playerUUID) {
		//TODO - 1.20.4: Should this use the dist executor instead of this thread thing
		if (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER) {
			if (FMLEnvironment.dist.isClient()) {
				Preconditions.checkState(Minecraft.getInstance().player != null, "Client player doesn't exist!");
				return Objects.requireNonNull(Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY));
			}
			throw new RuntimeException("unreachable");
		} else {
			Preconditions.checkNotNull(playerUUID);
			Preconditions.checkNotNull(ServerLifecycleHooks.getCurrentServer(), "Server must be running to query knowledge!");
			Player player = findOnlinePlayer(playerUUID);
			if (player != null) {
				return Objects.requireNonNull(player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY));
			}
			return TransmutationOffline.forPlayer(playerUUID);
		}
	}

	private Player findOnlinePlayer(UUID playerUUID) {
		for (Player player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			if (player.getUUID().equals(playerUUID)) {
				return player;
			}
		}
		return null;
	}
}