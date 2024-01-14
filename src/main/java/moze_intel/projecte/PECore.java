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
import moze_intel.projecte.config.PEModConfig;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.mappers.recipe.CraftingMapper;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.rings.Arcana;
import moze_intel.projecte.gameObjs.registries.PEArgumentTypes;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlockTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.registries.PECreativeTabs;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PENormalizedSimpleStacks;
import moze_intel.projecte.gameObjs.registries.PERecipeConditions;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.capability.AlchBagImpl;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.EMCCMD;
import moze_intel.projecte.network.commands.KnowledgeCMD;
import moze_intel.projecte.network.commands.RemoveEmcCMD;
import moze_intel.projecte.network.commands.ResetEmcCMD;
import moze_intel.projecte.network.commands.SetEmcCMD;
import moze_intel.projecte.network.commands.ShowBagCMD;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.NewRegistryEvent;
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
		if (!FMLEnvironment.production || ProjectEConfig.common.debugLogging.get()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(MODID, path);
	}

	private static PECore instance;

	@Nullable
	private EmcUpdateData emcUpdateResourceManager;
	private final PacketHandler packetHandler;

	public PECore(ModContainer modContainer, IEventBus modEventBus) {
		instance = this;
		MOD_CONTAINER = modContainer;

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::imcQueue);
		modEventBus.addListener(IMCHandler::handleMessages);
		modEventBus.addListener(this::onConfigLoad);
		modEventBus.addListener(this::registerCapabilities);
		modEventBus.addListener(this::registerRegistries);
		PEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
		PEArgumentTypes.ARGUMENT_TYPES.register(modEventBus);
		PEBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		PEBlocks.BLOCKS.register(modEventBus);
		PEBlockTypes.BLOCK_TYPES.register(modEventBus);
		PEContainerTypes.CONTAINER_TYPES.register(modEventBus);
		PECreativeTabs.CREATIVE_TABS.register(modEventBus);
		PEEntityTypes.ENTITY_TYPES.register(modEventBus);
		PEItems.ITEMS.register(modEventBus);
		PENormalizedSimpleStacks.NSS_SERIALIZERS.register(modEventBus);
		PERecipeConditions.CONDITION_CODECS.register(modEventBus);
		PERecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		PESoundEvents.SOUND_EVENTS.register(modEventBus);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);
		NeoForge.EVENT_BUS.addListener(this::tagsUpdated);
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		NeoForge.EVENT_BUS.addListener(this::serverStarting);
		NeoForge.EVENT_BUS.addListener(this::serverQuit);
		NeoForge.EVENT_BUS.addListener(PEPermissions::registerPermissionNodes);

		//Register our config files
		ProjectEConfig.register(modContainer);

		this.packetHandler = new PacketHandler(modEventBus, modContainer.getModInfo().getVersion());
	}

	public static PacketHandler packetHandler() {
		return instance.packetHandler;
	}

	private void registerRegistries(NewRegistryEvent event) {
		event.register(ProjectERegistries.NSS_SERIALIZER);
	}

	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		//TODO - 1.20.4: Test that these work as expected and save properly
		event.registerEntity(PECapabilities.ALCH_BAG_CAPABILITY, EntityType.PLAYER, (player, context) -> new AlchBagImpl(player));
		event.registerEntity(PECapabilities.KNOWLEDGE_CAPABILITY, EntityType.PLAYER, (player, context) -> new KnowledgeImpl(player));
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		new ThreadCheckUpdate().start();

		EMCMappingHandler.loadMappers();
		CraftingMapper.loadMappers();
		NBTManager.loadProcessors();

		event.enqueueWork(() -> {
			//Dispenser Behavior
			registerDispenseBehavior(new ShearsDispenseItemBehavior(), PEItems.DARK_MATTER_SHEARS, PEItems.RED_MATTER_SHEARS, PEItems.RED_MATTER_KATAR);
			DispenserBlock.registerBehavior(PEBlocks.NOVA_CATALYST, PEBlocks.NOVA_CATALYST.getBlock().createDispenseItemBehavior());
			DispenserBlock.registerBehavior(PEBlocks.NOVA_CATACLYSM, PEBlocks.NOVA_CATACLYSM.getBlock().createDispenseItemBehavior());
			registerDispenseBehavior(new OptionalDispenseItemBehavior() {
				@NotNull
				@Override
				protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
					//Based off the flint and steel dispense behavior
					if (stack.getItem() instanceof Arcana item) {
						if (item.getMode(stack) != 1) {
							//Only allow using the arcana ring to ignite things when on ignition mode
							setSuccess(false);
							return super.execute(source, stack);
						}
					}
					Level level = source.level();
					setSuccess(true);
					Direction direction = source.state().getValue(DispenserBlock.FACING);
					BlockPos pos = source.pos().relative(direction);
					BlockState state = level.getBlockState(pos);
					if (BaseFireBlock.canBePlacedAt(level, pos, direction)) {
						level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
					} else if (CampfireBlock.canLight(state)) {
						level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
					} else if (state.isFlammable(level, pos, direction.getOpposite())) {
						state.onCaughtFire(level, pos, direction.getOpposite(), null);
						if (state.getBlock() instanceof TntBlock) {
							level.removeBlock(pos, false);
						}
					} else {
						setSuccess(false);
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
					IFluidHandler fluidHandler = WorldHelper.getCapability(level, FluidHandler.BLOCK, pos, direction.getOpposite());
					if (fluidHandler != null) {
						fluidHandler.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
						return stack;
					}
					BlockState state = level.getBlockState(pos);
					if (state.getBlock() == Blocks.CAULDRON) {
						level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1));
						return stack;
					} else if (state.getBlock() == Blocks.WATER_CAULDRON) {
						if (!((LayeredCauldronBlock) state.getBlock()).isFull(state)) {
							level.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, state.getValue(LayeredCauldronBlock.LEVEL) + 1));
							return stack;
						}
					} else {
						WorldHelper.placeFluid(null, level, pos, Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
						level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
						return stack;
					}
					return super.execute(source, stack);
				}
			});
			CauldronInteraction.EMPTY.map().put(PEItems.EVERTIDE_AMULET.get(), (state, level, pos, player, hand, stack) -> {
				//Raise the fill level
				if (!level.isClientSide) {
					level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1));
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			});
			CauldronInteraction.WATER.map().put(PEItems.EVERTIDE_AMULET.get(), (state, level, pos, player, hand, stack) -> {
				if (((LayeredCauldronBlock) state.getBlock()).isFull(state)) {
					return InteractionResult.PASS;
				} else if (!level.isClientSide) {
					//Raise the fill level
					level.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, state.getValue(LayeredCauldronBlock.LEVEL) + 1));
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			});
			CauldronInteraction.EMPTY.map().put(PEItems.VOLCANITE_AMULET.get(), (state, level, pos, player, hand, stack) -> {
				if (!level.isClientSide && ItemPE.consumeFuel(player, stack, 32, true)) {
					level.setBlockAndUpdate(pos, Blocks.LAVA_CAULDRON.defaultBlockState());
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			});
		});
	}

	private static void registerDispenseBehavior(DispenseItemBehavior behavior, ItemLike... items) {
		for (ItemLike item : items) {
			DispenserBlock.registerBehavior(item, behavior);
		}
	}

	private void imcQueue(InterModEnqueueEvent event) {
		WorldTransmutations.init();
		IntegrationHelper.sendIMCMessages(event);
	}

	private void onConfigLoad(ModConfigEvent configEvent) {
		//Note: We listen to both the initial load and the reload, so as to make sure that we fix any accidentally
		// cached values from calls before the initial loading
		ModConfig config = configEvent.getConfig();
		//Make sure it is for the same modid as us
		if (config.getModId().equals(MODID) && config instanceof PEModConfig peConfig) {
			peConfig.clearCache(configEvent);
		}
	}

	private void tagsUpdated(TagsUpdatedEvent event) {
		if (emcUpdateResourceManager != null) {
			long start = System.currentTimeMillis();
			//Clear the cached created tags
			AbstractNSSTag.clearCreatedTags();
			CustomEMCParser.init();
			try {
				EMCMappingHandler.map(emcUpdateResourceManager.serverResources(), emcUpdateResourceManager.registryAccess(), emcUpdateResourceManager.resourceManager());
				PECore.LOGGER.info("Registered {} EMC values. (took {} ms)", EMCMappingHandler.getEmcMapSize(), System.currentTimeMillis() - start);
				PacketUtils.sendFragmentedEmcPacketToAll();
			} catch (Throwable t) {
				PECore.LOGGER.error("Error calculating EMC values", t);
			}
			emcUpdateResourceManager = null;
		}
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener((ResourceManagerReloadListener) manager -> emcUpdateResourceManager = new EmcUpdateData(event.getServerResources(), event.getRegistryAccess(), manager));
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
		CustomEMCParser.flush();
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}

	private record EmcUpdateData(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
	}
}