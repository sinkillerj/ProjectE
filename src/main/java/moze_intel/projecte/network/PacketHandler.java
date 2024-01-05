package moze_intel.projecte.network;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite;
import moze_intel.projecte.network.packets.IPEPacket;
import moze_intel.projecte.network.packets.to_client.SyncBagDataPKT;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.SyncFuelMapperPKT;
import moze_intel.projecte.network.packets.to_client.UpdateCondenserLockPKT;
import moze_intel.projecte.network.packets.to_client.UpdateWindowIntPKT;
import moze_intel.projecte.network.packets.to_client.UpdateWindowLongPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncChangePKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncInputsAndLocksPKT;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeSyncPKT;
import moze_intel.projecte.network.packets.to_server.KeyPressPKT;
import moze_intel.projecte.network.packets.to_server.SearchUpdatePKT;
import moze_intel.projecte.network.packets.to_server.UpdateGemModePKT;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf.Reader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.IConfigurationPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Heavily based off of Mekanism's packet handler
 */
public final class PacketHandler {
	//TODO - 1.20.4: Validate no packets rely on serialization deserialization as then they won't work properly in single player
	// And also potentially rename any packet ids that aren't that great

	//Client to server instanced packets
	private IPEPacket<PlayPayloadContext> leftClickArchangel;

	//Server to client instanced packets
	private IPEPacket<PlayPayloadContext> clearKnowledge;
	private IPEPacket<PlayPayloadContext> updateTransmutationTargets;

	private IPEPacket<PlayPayloadContext> resetCooldown;

	public PacketHandler(IEventBus modEventBus, ArtifactVersion version) {
		modEventBus.addListener(RegisterPayloadHandlerEvent.class, event -> {
			IPayloadRegistrar registrar = event.registrar(PECore.MODID)
					.versioned(version.toString());
			registerClientToServer(new PacketRegistrar(registrar, IDirectionAwarePayloadHandlerBuilder::server));
			registerServerToClient(new PacketRegistrar(registrar, IDirectionAwarePayloadHandlerBuilder::client));
		});
	}

	private void registerClientToServer(PacketRegistrar registrar) {
		registrar.play(KeyPressPKT.ID, KeyPressPKT::new);
		leftClickArchangel = registrar.playInstanced(PECore.rl("left_click_archangel"), context -> context.player().ifPresent(player -> {
			ItemStack main = player.getMainHandItem();
			if (!main.isEmpty() && main.getItem() instanceof ArchangelSmite archangelSmite) {
				archangelSmite.fireVolley(main, player);
			}
		}));
		registrar.play(SearchUpdatePKT.ID, SearchUpdatePKT::new);
		registrar.play(UpdateGemModePKT.ID, UpdateGemModePKT::new);
	}

	private void registerServerToClient(PacketRegistrar registrar) {
		//Server to client messages
		resetCooldown = registrar.playInstanced(PECore.rl("reset_cooldown"), context -> context.player().ifPresent(Player::resetAttackStrengthTicker));
		clearKnowledge = registrar.playInstanced(PECore.rl("clear_knowledge"), context -> context.player().ifPresent(player -> {
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				knowledge.clearKnowledge();
				if (player.containerMenu instanceof TransmutationContainer container) {
					container.transmutationInventory.updateClientTargets();
				}
			}
		}));
		registrar.play(KnowledgeSyncPKT.ID, KnowledgeSyncPKT::new);
		registrar.play(KnowledgeSyncEmcPKT.ID, KnowledgeSyncEmcPKT::new);
		registrar.play(KnowledgeSyncInputsAndLocksPKT.ID, KnowledgeSyncInputsAndLocksPKT::new);
		registrar.play(KnowledgeSyncChangePKT.ID, KnowledgeSyncChangePKT::new);
		registrar.play(SyncBagDataPKT.ID, SyncBagDataPKT::new);
		registrar.play(SyncEmcPKT.ID, SyncEmcPKT::new);
		registrar.play(SyncFuelMapperPKT.ID, SyncFuelMapperPKT::new);
		registrar.play(UpdateCondenserLockPKT.ID, UpdateCondenserLockPKT::new);
		updateTransmutationTargets = registrar.playInstanced(PECore.rl("update_transmutation_targets"), context ->
				PacketUtils.container(context, TransmutationContainer.class)
						.ifPresent(container -> container.transmutationInventory.updateClientTargets())
		);
		registrar.play(UpdateWindowIntPKT.ID, UpdateWindowIntPKT::new);
		registrar.play(UpdateWindowLongPKT.ID, UpdateWindowLongPKT::new);
	}

	public void clearKnowledge(ServerPlayer player) {
		PacketUtils.sendTo(clearKnowledge, player);
	}

	public void updateTransmutationTargets(ServerPlayer player) {
		PacketUtils.sendTo(updateTransmutationTargets, player);
	}

	public void resetCooldown(ServerPlayer player) {
		PacketUtils.sendTo(resetCooldown, player);
	}

	public void leftClickArchangel() {
		PacketUtils.sendToServer(leftClickArchangel);
	}

	@FunctionalInterface
	private interface ContextAwareHandler {

		<PAYLOAD extends CustomPacketPayload, HANDLER> IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> accept(IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> builder, HANDLER handler);
	}

	protected record PacketRegistrar(IPayloadRegistrar registrar, ContextAwareHandler contextAwareHandler) {

		private <MSG extends IPEPacket<IPayloadContext>> void common(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IPayloadHandler<MSG> handler) {
			registrar.common(id, reader, builder -> contextAwareHandler.accept(builder, handler));
		}

		public <MSG extends IPEPacket<IPayloadContext>> void common(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
			common(id, reader, IPEPacket::handleMainThread);
		}

		public <MSG extends IPEPacket<IPayloadContext>> void commonNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
			common(id, reader, IPEPacket::handle);
		}

		public IPEPacket<IPayloadContext> commonInstanced(ResourceLocation id, Consumer<IPayloadContext> handler) {
			return instanced(id, handler, this::common);
		}

		private <MSG extends IPEPacket<ConfigurationPayloadContext>> void configuration(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IConfigurationPayloadHandler<MSG> handler) {
			registrar.configuration(id, reader, builder -> contextAwareHandler.accept(builder, handler));
		}

		public void configuration(ResourceLocation id, FriendlyByteBuf.Reader<? extends IPEPacket<ConfigurationPayloadContext>> reader) {
			configuration(id, reader, IPEPacket::handleMainThread);
		}

		public void configurationNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<? extends IPEPacket<ConfigurationPayloadContext>> reader) {
			configuration(id, reader, IPEPacket::handle);
		}

		public IPEPacket<ConfigurationPayloadContext> configurationInstanced(ResourceLocation id, Consumer<ConfigurationPayloadContext> handler) {
			return instanced(id, handler, this::configuration);
		}

		private <MSG extends IPEPacket<PlayPayloadContext>> void play(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IPlayPayloadHandler<MSG> handler) {
			registrar.play(id, reader, builder -> contextAwareHandler.accept(builder, handler));
		}

		public void play(ResourceLocation id, FriendlyByteBuf.Reader<? extends IPEPacket<PlayPayloadContext>> reader) {
			play(id, reader, IPEPacket::handleMainThread);
		}

		public void playNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<? extends IPEPacket<PlayPayloadContext>> reader) {
			play(id, reader, IPEPacket::handle);
		}

		public IPEPacket<PlayPayloadContext> playInstanced(ResourceLocation id, Consumer<PlayPayloadContext> handler) {
			return instanced(id, handler, this::play);
		}

		private <CONTEXT extends IPayloadContext> IPEPacket<CONTEXT> instanced(ResourceLocation id, Consumer<CONTEXT> handler,
				BiConsumer<ResourceLocation, Reader<IPEPacket<CONTEXT>>> registerMethod) {
			IPEPacket<CONTEXT> instance = new IPEPacket<>() {
				@Override
				public void write(@NotNull FriendlyByteBuf buf) {
				}

				@NotNull
				@Override
				public ResourceLocation id() {
					return id;
				}

				@Override
				public void handle(CONTEXT context) {
					handler.accept(context);
				}
			};
			registerMethod.accept(id, buf -> instance);
			return instance;
		}
	}
}