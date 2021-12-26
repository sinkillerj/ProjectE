package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage;
import moze_intel.projecte.config.PEModConfig;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.EMCReloadListener;
import moze_intel.projecte.emc.json.NSSSerializer;
import moze_intel.projecte.emc.mappers.recipe.CraftingMapper;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarIngredient;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarsCondition;
import moze_intel.projecte.gameObjs.customRecipes.TomeEnabledCondition;
import moze_intel.projecte.gameObjs.items.rings.Arcana;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.handlers.CommonInternalAbilities;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.capability.AlchBagImpl;
import moze_intel.projecte.impl.capability.AlchBagItemDefaultImpl;
import moze_intel.projecte.impl.capability.AlchChestItemDefaultImpl;
import moze_intel.projecte.impl.capability.ChargeItemDefaultImpl;
import moze_intel.projecte.impl.capability.EmcHolderItemDefaultImpl;
import moze_intel.projecte.impl.capability.EmcStorageDefaultImpl;
import moze_intel.projecte.impl.capability.ExtraFunctionItemDefaultImpl;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.impl.capability.ModeChangerItemDefaultImpl;
import moze_intel.projecte.impl.capability.PedestalItemDefaultImpl;
import moze_intel.projecte.impl.capability.ProjectileShooterItemDefaultImpl;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.ClearKnowledgeCMD;
import moze_intel.projecte.network.commands.DumpMissingEmc;
import moze_intel.projecte.network.commands.RemoveEmcCMD;
import moze_intel.projecte.network.commands.ResetEmcCMD;
import moze_intel.projecte.network.commands.SetEmcCMD;
import moze_intel.projecte.network.commands.ShowBagCMD;
import moze_intel.projecte.network.commands.argument.ColorArgument;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.argument.UUIDArgument;
import moze_intel.projecte.utils.DummyIStorage;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.dispenser.BeehiveDispenseBehavior;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PECore.MODID)
@Mod.EventBusSubscriber(modid = PECore.MODID)
public class PECore {

	public static final String MODID = ProjectEAPI.PROJECTE_MODID;
	public static final String MODNAME = "ProjectE";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static final Logger LOGGER = LogManager.getLogger(MODID);

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

	public PECore() {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::imcQueue);
		modEventBus.addListener(this::imcHandle);
		modEventBus.addListener(this::onConfigLoad);
		modEventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
		PEBlocks.BLOCKS.register(modEventBus);
		PEContainerTypes.CONTAINER_TYPES.register(modEventBus);
		PEEntityTypes.ENTITY_TYPES.register(modEventBus);
		PEItems.ITEMS.register(modEventBus);
		PERecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		PESoundEvents.SOUND_EVENTS.register(modEventBus);
		PETileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::addReloadListenersLowest);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);

		//Register our config files
		ProjectEConfig.register();
	}

	private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		//Add our condition serializers
		CraftingHelper.register(TomeEnabledCondition.SERIALIZER);
		CraftingHelper.register(FullKleinStarsCondition.SERIALIZER);
		//Add our ingredients
		CraftingHelper.register(rl("full_klein_star"), FullKleinStarIngredient.SERIALIZER);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		AlchBagImpl.init();
		KnowledgeImpl.init();
		CapabilityManager.INSTANCE.register(InternalTimers.class, new DummyIStorage<>(), InternalTimers::new);
		CapabilityManager.INSTANCE.register(InternalAbilities.class, new DummyIStorage<>(), () -> new InternalAbilities(null));
		CapabilityManager.INSTANCE.register(CommonInternalAbilities.class, new DummyIStorage<>(), () -> new CommonInternalAbilities(null));
		CapabilityManager.INSTANCE.register(IAlchBagItem.class, new DummyIStorage<>(), AlchBagItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IAlchChestItem.class, new DummyIStorage<>(), AlchChestItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IExtraFunction.class, new DummyIStorage<>(), ExtraFunctionItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IItemCharge.class, new DummyIStorage<>(), ChargeItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IItemEmcHolder.class, new DummyIStorage<>(), EmcHolderItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IModeChanger.class, new DummyIStorage<>(), ModeChangerItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IPedestalItem.class, new DummyIStorage<>(), PedestalItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IProjectileShooter.class, new DummyIStorage<>(), ProjectileShooterItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IEmcStorage.class, new DummyIStorage<>(), EmcStorageDefaultImpl::new);

		new ThreadCheckUpdate().start();

		EMCMappingHandler.loadMappers();
		CraftingMapper.loadMappers();
		NBTManager.loadProcessors();

		event.enqueueWork(() -> {
			PacketHandler.register();
			//Dispenser Behavior
			registerDispenseBehavior(new BeehiveDispenseBehavior(), PEItems.DARK_MATTER_SHEARS, PEItems.RED_MATTER_SHEARS, PEItems.RED_MATTER_KATAR);
			DispenserBlock.registerBehavior(PEBlocks.NOVA_CATALYST, PEBlocks.NOVA_CATALYST.getBlock().createDispenseItemBehavior());
			DispenserBlock.registerBehavior(PEBlocks.NOVA_CATACLYSM, PEBlocks.NOVA_CATACLYSM.getBlock().createDispenseItemBehavior());
			registerDispenseBehavior(new OptionalDispenseBehavior() {
				@Nonnull
				@Override
				protected ItemStack execute(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
					//Based off the flint and steel dispense behavior
					if (stack.getItem() instanceof Arcana) {
						Arcana item = (Arcana) stack.getItem();
						if (item.getMode(stack) != 1) {
							//Only allow using the arcana ring to ignite things when on ignition mode
							setSuccess(false);
							return super.execute(source, stack);
						}
					}
					World world = source.getLevel();
					setSuccess(true);
					Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
					BlockPos pos = source.getPos().relative(direction);
					BlockState state = world.getBlockState(pos);
					if (AbstractFireBlock.canBePlacedAt(world, pos, direction)) {
						world.setBlockAndUpdate(pos, AbstractFireBlock.getState(world, pos));
					} else if (CampfireBlock.canLight(state)) {
						world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
					} else if (state.isFlammable(world, pos, direction.getOpposite())) {
						state.catchFire(world, pos, direction.getOpposite(), null);
						if (state.getBlock() instanceof TNTBlock) {
							world.removeBlock(pos, false);
						}
					} else {
						setSuccess(false);
					}
					return stack;
				}
			}, PEItems.IGNITION_RING, PEItems.ARCANA_RING);
			DispenserBlock.registerBehavior(PEItems.EVERTIDE_AMULET, new DefaultDispenseItemBehavior() {
				@Nonnull
				@Override
				public ItemStack execute(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
					//Based off of vanilla's bucket dispense behaviors
					// Note: We only do evertide, not volcanite, as placing lava requires EMC
					World world = source.getLevel();
					Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
					BlockPos pos = source.getPos().relative(direction);
					TileEntity tile = WorldHelper.getTileEntity(world, pos);
					Direction sideHit = direction.getOpposite();
					if (tile != null) {
						Optional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit).resolve();
						if (capability.isPresent()) {
							capability.get().fill(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
							return stack;
						}
					}
					BlockState state = world.getBlockState(pos);
					if (state.getBlock() == Blocks.CAULDRON) {
						int waterLevel = state.getValue(CauldronBlock.LEVEL);
						if (waterLevel < 3) {
							((CauldronBlock) state.getBlock()).setWaterLevel(world, pos, state, waterLevel + 1);
							return stack;
						}
					} else {
						WorldHelper.placeFluid(null, world, pos, Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
						world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
						return stack;
					}
					return super.execute(source, stack);
				}
			});

			// internals unsafe
			ArgumentTypes.register(MODID + ":uuid", UUIDArgument.class, new ArgumentSerializer<>(UUIDArgument::new));
			ArgumentTypes.register(MODID + ":color", ColorArgument.class, new ArgumentSerializer<>(ColorArgument::new));
			ArgumentTypes.register(MODID + ":nss", NSSItemArgument.class, new ArgumentSerializer<>(NSSItemArgument::new));
		});
	}

	private static void registerDispenseBehavior(IDispenseItemBehavior behavior, IItemProvider... items) {
		for (IItemProvider item : items) {
			DispenserBlock.registerBehavior(item, behavior);
		}
	}

	private void imcQueue(InterModEnqueueEvent event) {
		WorldTransmutations.init();
		NSSSerializer.init();
		IntegrationHelper.sendIMCMessages(event);
	}

	private void imcHandle(InterModProcessEvent event) {
		IMCHandler.handleMessages();
	}

	private void onConfigLoad(ModConfig.ModConfigEvent configEvent) {
		//Note: We listen to both the initial load and the reload, so as to make sure that we fix any accidentally
		// cached values from calls before the initial loading
		ModConfig config = configEvent.getConfig();
		//Make sure it is for the same modid as us
		if (config.getModId().equals(MODID) && config instanceof PEModConfig) {
			((PEModConfig) config).clearCache();
		}
	}

	private void addReloadListenersLowest(AddReloadListenerEvent event) {
		//Note: We register our listener for this event on lowest priority so that if other mods register custom NSSTags
		// or other things that need to be sync'd/reloaded they have a chance to go before we do
		event.addListener(new EMCReloadListener(event.getDataPackRegistries()));
	}

	private void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSource> root = Commands.literal("projecte")
				.then(ClearKnowledgeCMD.register())
				.then(DumpMissingEmc.register())
				.then(RemoveEmcCMD.register())
				.then(ResetEmcCMD.register())
				.then(SetEmcCMD.register())
				.then(ShowBagCMD.register());
		event.getDispatcher().register(root);
	}

	private void serverStarting(FMLServerStartingEvent event) {
		if (!ThreadCheckUUID.hasRunServer()) {
			new ThreadCheckUUID(true).start();
		}
	}

	private void serverQuit(FMLServerStoppedEvent event) {
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}
}