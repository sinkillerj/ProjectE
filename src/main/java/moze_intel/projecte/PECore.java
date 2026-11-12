package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.ProjectERegistries;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.nss.AbstractNSSTag;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.items.IHasConditionalAttributes;
import moze_intel.projecte.gameObjs.registries.PEArmorMaterials;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.registries.PECreativeTabs;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PENormalizedSimpleStacks;
import moze_intel.projecte.gameObjs.registries.PERecipeConditions;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.capability.AlchBagImpl;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.EMCCMD;
import moze_intel.projecte.network.commands.KnowledgeCMD;
import moze_intel.projecte.network.commands.RemoveEmcCMD;
import moze_intel.projecte.network.commands.ResetEmcCMD;
import moze_intel.projecte.network.commands.SetEmcCMD;
import moze_intel.projecte.network.commands.ShowBagCMD;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.SyncFuelMapperPKT;
import moze_intel.projecte.network.packets.to_client.SyncWorldTransmutations;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.world_transmutation.WorldTransmutationManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.callback.ClearCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(PECore.MODID)
public class PECore {

	public static final String MODID = ProjectEAPI.PROJECTE_MODID;
	public static final String MODNAME = "ProjectE";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final List<String> uuids = new ArrayList<>();

	public static ModContainer MOD_CONTAINER;

	public static void debugLog(String msg, Object... args) {
		if (!FMLEnvironment.isProduction() || ProjectEConfig.common.debugLogging.get()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	public static Identifier rl(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	private static PECore instance;

	@Nullable
	private EmcUpdateData emcUpdateResourceManager;
	private final PacketHandler packetHandler;

	public PECore(ModContainer modContainer, IEventBus modEventBus) {
		instance = this;
		MOD_CONTAINER = modContainer;

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(IntegrationHelper::sendIMCMessages);
		modEventBus.addListener(this::registerCapabilities);
		modEventBus.addListener(this::registerRegistries);
		modEventBus.addListener(this::modifyRegistries);
		PEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
		PEBlocks.BLOCKS.register(modEventBus);
		PEBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		PEContainerTypes.CONTAINER_TYPES.register(modEventBus);
		PECreativeTabs.CREATIVE_TABS.register(modEventBus);
		PEDataComponentTypes.DATA_COMPONENT_TYPES.register(modEventBus);
		PEEntityTypes.ENTITY_TYPES.register(modEventBus);
		PEItems.ITEMS.register(modEventBus);
		PENormalizedSimpleStacks.NSS_SERIALIZERS.register(modEventBus);
		PERecipeConditions.CONDITION_CODECS.register(modEventBus);
		PERecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		PESoundEvents.SOUND_EVENTS.register(modEventBus);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);
		NeoForge.EVENT_BUS.addListener(this::dataPackSync);
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		NeoForge.EVENT_BUS.addListener(this::serverStarting);
		NeoForge.EVENT_BUS.addListener(this::serverQuit);
		NeoForge.EVENT_BUS.addListener(PEPermissions::registerPermissionNodes);
		NeoForge.EVENT_BUS.addListener(this::onModifyItemAttributes);

		//Register our config files
		ProjectEConfig.register(modContainer);
		modEventBus.addListener(ProjectEConfig::onConfigLoad);

		this.packetHandler = new PacketHandler(modEventBus, modContainer.getModInfo().getVersion());
	}

	public static PacketHandler packetHandler() {
		return instance.packetHandler;
	}

	private void registerRegistries(NewRegistryEvent event) {
		event.register(ProjectERegistries.NSS_SERIALIZER);
	}

	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerEntity(PECapabilities.ALCH_BAG_CAPABILITY, EntityTypes.PLAYER, (player, context) -> new AlchBagImpl(player));
		event.registerEntity(PECapabilities.KNOWLEDGE_CAPABILITY, EntityTypes.PLAYER, (player, context) -> new KnowledgeImpl(player));
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		new ThreadCheckUpdate().start();
		EMCMappingHandler.loadMappers();

		event.enqueueWork(() -> {
			//Dispenser Behavior
			registerDispenseBehavior(new ShearsDispenseItemBehavior(), PEItems.DARK_MATTER_SHEARS, PEItems.RED_MATTER_SHEARS, PEItems.RED_MATTER_KATAR);
			DispenserBlock.registerBehavior(PEBlocks.NOVA_CATALYST, PEBlocks.NOVA_CATALYST.getBlock().createDispenseItemBehavior());
			DispenserBlock.registerBehavior(PEBlocks.NOVA_CATACLYSM, PEBlocks.NOVA_CATACLYSM.getBlock().createDispenseItemBehavior());
			registerDispenseBehavior(new OptionalDispenseItemBehavior() {
				@NotNull
				@Override
				protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
					//[VanillaCopy] Based off the flint and steel dispense behavior
					if (!stack.canPerformAction(ItemAbilities.FIRESTARTER_LIGHT)) {
						//Only allow using the arcana ring to ignite things when on ignition mode
						setSuccess(false);
						return super.execute(source, stack);
					}
					Level level = source.level();
					setSuccess(true);
					Direction direction = source.state().getValue(DispenserBlock.FACING);
					BlockPos pos = source.pos().relative(direction);
					BlockState state = level.getBlockState(pos);
					if (BaseFireBlock.canBePlacedAt(level, pos, direction)) {
						level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
						level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
					} else {
						Direction opposite = direction.getOpposite();
						BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), opposite, pos, false);
						UseOnContext context = new UseOnContext(level, null, InteractionHand.MAIN_HAND, stack, hitResult);
						BlockState modifiedState = state.getToolModifiedState(context, ItemAbilities.FIRESTARTER_LIGHT, false);
						if (modifiedState != null) {
							level.setBlockAndUpdate(pos, modifiedState);
							level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
						} else if (state.isFlammable(level, pos, opposite)) {
							state.onCaughtFire(level, pos, opposite, null, stack);
							if (state.getBlock() instanceof TntBlock) {
								level.removeBlock(pos, false);
							}
						} else {
							setSuccess(false);
						}
					}
					return stack;
				}
			}, PEItems.IGNITION_RING, PEItems.ARCANA_RING);
			DispenserBlock.registerBehavior(PEItems.EVERTIDE_AMULET, new DefaultDispenseItemBehavior() {
				@NotNull
				@Override
				public ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
					//Based off of vanilla's bucket dispense behaviors
					// Note: We only do evertide, not volcanite, as placing lava requires EMC
					Level level = source.level();
					Direction direction = source.state().getValue(DispenserBlock.FACING);
					BlockPos pos = source.pos().relative(direction);
					ResourceHandler<FluidResource> fluidHandler = WorldHelper.getFluidHandler(level, pos, direction.getOpposite());
					if (fluidHandler != null) {
						net.neoforged.neoforge.transfer.ResourceHandlerUtil.insertStacking(
								fluidHandler, net.neoforged.neoforge.transfer.fluid.FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME, null);
						return stack;
					}
					WorldHelper.placeFluid(null, level, pos, Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
					return stack;
				}
			});
		});
	}

	private static void registerDispenseBehavior(DispenseItemBehavior behavior, ItemLike... items) {
		for (ItemLike item : items) {
			DispenserBlock.registerBehavior(item, behavior);
		}
	}

	private void dataPackSync(OnDatapackSyncEvent event) {
		if (emcUpdateResourceManager != null) {
			long start = System.currentTimeMillis();
			//Clear the cached created tags
			AbstractNSSTag.clearCreatedTags();
			CustomEMCParser.init(emcUpdateResourceManager.registries());
			try {
				EMCMappingHandler.map(emcUpdateResourceManager.serverResources(), emcUpdateResourceManager.registries(), emcUpdateResourceManager.resourceManager());
				PECore.LOGGER.info("Registered {} EMC values. (took {} ms)", EMCMappingHandler.getEmcMapSize(), System.currentTimeMillis() - start);
			} catch (Throwable t) {
				PECore.LOGGER.error("Error calculating EMC values", t);
			}
			emcUpdateResourceManager = null;
		}
		if (event.getPlayer() == null) {
			List<ServerPlayer> players = event.getPlayerList().getPlayers();
			if (players.isEmpty()) {
				return;
			}
			SyncEmcPKT pkt = SyncEmcPKT.serializeEmcData(players.getFirst().registryAccess());
			SyncFuelMapperPKT fuelPkt = FuelMapper.getSyncPacket();
			SyncWorldTransmutations transmutationPkt = WorldTransmutationManager.getSyncPacket();
			for (ServerPlayer player : players) {
				if (!player.connection.getConnection().isMemoryConnection()) {
					PacketDistributor.sendToPlayer(player, pkt, fuelPkt);
					PacketDistributor.sendToPlayer(player, transmutationPkt);
				}
			}
		} else {
			ServerPlayer player = event.getPlayer();
			if (!player.connection.getConnection().isMemoryConnection()) {
				PacketDistributor.sendToPlayer(player, SyncEmcPKT.serializeEmcData(player.registryAccess()), FuelMapper.getSyncPacket());
				PacketDistributor.sendToPlayer(player, WorldTransmutationManager.getSyncPacket());
			}
		}
	}

	private void addReloadListeners(AddServerReloadListenersEvent event) {
		event.addListener(PECore.rl("emc_update"), (ResourceManagerReloadListener) manager -> emcUpdateResourceManager = new EmcUpdateData(event.getServerResources(), event.getServerResources().getRegistryLookup(), manager));
		event.addListener(PECore.rl("world_transmutation"), WorldTransmutationManager.INSTANCE);
	}

	private void registerCommands(RegisterCommandsEvent event) {
		CommandBuildContext context = event.getBuildContext();
		event.getDispatcher().register(Commands.literal("projecte")
				.requires(PEPermissions.COMMAND)
				.then(RemoveEmcCMD.register(context))
				.then(ResetEmcCMD.register(context))
				.then(SetEmcCMD.register(context))
				.then(ShowBagCMD.register(context))
				.then(EMCCMD.register(context))
				.then(KnowledgeCMD.register(context))
		);
	}

	private void serverStarting(ServerStartingEvent event) {
		if (!ThreadCheckUUID.hasRunServer()) {
			new ThreadCheckUUID(true).start();
		}
	}

	private void serverQuit(ServerStoppedEvent event) {
		//Ensure we save any changes to the custom emc file
		CustomEMCParser.flush(event.getServer().registryAccess());
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}

	private void onModifyItemAttributes(ItemAttributeModifierEvent event) {
		if (event.getItemStack().getItem() instanceof IHasConditionalAttributes item) {
			item.adjustAttributes(event);
		}
	}

	private void modifyRegistries(ModifyRegistriesEvent event) {
		BuiltInRegistries.BLOCK.addCallback((ClearCallback<Block>) (registry, full) -> WorldHelper.clearCachedAgeProperties());
	}

	private record EmcUpdateData(ReloadableServerResources serverResources, HolderLookup.Provider registries, ResourceManager resourceManager) {
	}
}
