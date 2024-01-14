package moze_intel.projecte.impl;

import java.util.Objects;
import java.util.UUID;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class TransmutationProxyImpl implements ITransmutationProxy {

	@NotNull
	@Override
	public IKnowledgeProvider getKnowledgeProviderFor(@NotNull UUID playerUUID) {
		if (EffectiveSide.get().isServer()) {
			Objects.requireNonNull(playerUUID);
			MinecraftServer server = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Server must be running to query knowledge!");
			Player player = server.getPlayerList().getPlayer(playerUUID);
			if (player != null) {
				return Objects.requireNonNull(player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY));
			}
			return TransmutationOffline.forPlayer(playerUUID);
		} else if (FMLEnvironment.dist.isClient()) {
			Objects.requireNonNull(Minecraft.getInstance().player, "Client player doesn't exist!");
			return Objects.requireNonNull(Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY));
		}
		throw new IllegalStateException("unreachable");
	}
}