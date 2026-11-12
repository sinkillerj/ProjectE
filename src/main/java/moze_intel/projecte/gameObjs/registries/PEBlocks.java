package moze_intel.projecte.gameObjs.registries;



import java.util.function.BiFunction;
import java.util.function.Function;

import java.util.function.ToIntFunction;

import moze_intel.projecte.PECore;

import moze_intel.projecte.gameObjs.EnumCollectorTier;

import moze_intel.projecte.gameObjs.EnumMatterType;

import moze_intel.projecte.gameObjs.EnumRelayTier;

import moze_intel.projecte.gameObjs.blocks.AlchemicalChest;

import moze_intel.projecte.gameObjs.blocks.Collector;

import moze_intel.projecte.gameObjs.blocks.Condenser;

import moze_intel.projecte.gameObjs.blocks.CondenserMK2;

import moze_intel.projecte.gameObjs.blocks.InterdictionTorchEntityBlock.InterdictionTorch;

import moze_intel.projecte.gameObjs.blocks.InterdictionTorchEntityBlock.InterdictionTorchWall;

import moze_intel.projecte.gameObjs.blocks.MatterBlock;

import moze_intel.projecte.gameObjs.blocks.MatterFurnace;

import moze_intel.projecte.gameObjs.blocks.Pedestal;

import moze_intel.projecte.gameObjs.blocks.ProjectETNT;

import moze_intel.projecte.gameObjs.blocks.ProjectETNT.TNTEntityCreator;

import moze_intel.projecte.gameObjs.blocks.Relay;

import moze_intel.projecte.gameObjs.blocks.TransmutationStone;

import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;

import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;

import moze_intel.projecte.gameObjs.registration.PERegistryUtil;

import moze_intel.projecte.gameObjs.registration.impl.BlockDeferredRegister;

import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;

import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;

import moze_intel.projecte.gameObjs.items.PEBlockItem;

import net.minecraft.world.item.BlockItem;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.StandingAndWallBlockItem;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.SoundType;

import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.material.PushReaction;



public class PEBlocks {



	public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(PECore.MODID);



	public static final BlockRegistryObject<AlchemicalChest, BlockItem> ALCHEMICAL_CHEST = BLOCKS.register("alchemical_chest", id -> new AlchemicalChest(PERegistryUtil.blockProps(id).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(10, 3_600_000)));

	public static final BlockRegistryObject<Block, BlockItem> ALCHEMICAL_COAL = registerFuelBlock("alchemical_coal_block", MapColor.COLOR_RED);

	public static final BlockRegistryObject<Block, BlockItem> MOBIUS_FUEL = registerFuelBlock("mobius_fuel_block", MapColor.COLOR_RED);

	public static final BlockRegistryObject<Block, BlockItem> AETERNALIS_FUEL = registerFuelBlock("aeternalis_fuel_block", MapColor.COLOR_LIGHT_GRAY);

	public static final BlockRegistryObject<Collector, PEBlockItem> COLLECTOR = registerCollector("collector_mk1", EnumCollectorTier.MK1, state -> 7);

	public static final BlockRegistryObject<Collector, PEBlockItem> COLLECTOR_MK2 = registerCollector("collector_mk2", EnumCollectorTier.MK2, state -> 11);

	public static final BlockRegistryObject<Collector, PEBlockItem> COLLECTOR_MK3 = registerCollector("collector_mk3", EnumCollectorTier.MK3, state -> 15);

	public static final BlockRegistryObject<Condenser, BlockItem> CONDENSER = registerCondenser("condenser_mk1", Condenser::new, (block, props) -> new BlockItem(block, props));

	public static final BlockRegistryObject<CondenserMK2, BlockItem> CONDENSER_MK2 = registerCondenser("condenser_mk2", CondenserMK2::new, (block, props) -> new BlockItem(block, props.fireResistant()));

	public static final BlockRegistryObject<Pedestal, PEBlockItem> DARK_MATTER_PEDESTAL = BLOCKS.register("dm_pedestal", id -> new Pedestal(PERegistryUtil.blockProps(id).mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1_000_000, 3_000_000).lightLevel(state -> 12)), (block, props) -> new PEBlockItem(block, props.fireResistant(), (stack, tooltip, flags) -> block.addTooltip(stack, tooltip)));

	public static final BlockRegistryObject<MatterFurnace, BlockItem> DARK_MATTER_FURNACE = registerFurnace("dm_furnace", EnumMatterType.DARK_MATTER, 1_000_000, 3_000_000);

	public static final BlockRegistryObject<MatterFurnace, BlockItem> RED_MATTER_FURNACE = registerFurnace("rm_furnace", EnumMatterType.RED_MATTER, 2_000_000, 6_000_000);

	public static final BlockRegistryObject<MatterBlock, BlockItem> DARK_MATTER = registerMatterBlock("dark_matter_block", EnumMatterType.DARK_MATTER, 1_000_000, 3_000_000);

	public static final BlockRegistryObject<MatterBlock, BlockItem> RED_MATTER = registerMatterBlock("red_matter_block", EnumMatterType.RED_MATTER, 2_000_000, 6_000_000);

	public static final WallOrFloorBlockRegistryObject<InterdictionTorch, InterdictionTorchWall, StandingAndWallBlockItem> INTERDICTION_TORCH = BLOCKS.registerWallOrFloorItem("interdiction_torch", InterdictionTorch::new, InterdictionTorchWall::new, id -> PERegistryUtil.blockProps(id).pushReaction(PushReaction.POPPED).noCollision().instabreak().strength(0).lightLevel(state -> 14).randomTicks());

	public static final BlockRegistryObject<ProjectETNT, BlockItem> NOVA_CATALYST = registerExplosive("nova_catalyst", EntityNovaCatalystPrimed::new);

	public static final BlockRegistryObject<ProjectETNT, BlockItem> NOVA_CATACLYSM = registerExplosive("nova_cataclysm", EntityNovaCataclysmPrimed::new);

	public static final BlockRegistryObject<TransmutationStone, BlockItem> TRANSMUTATION_TABLE = BLOCKS.register("transmutation_table", id -> new TransmutationStone(PERegistryUtil.blockProps(id).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(10, 30)));

	public static final BlockRegistryObject<Relay, PEBlockItem> RELAY = registerRelay("relay_mk1", EnumRelayTier.MK1, state -> 7);

	public static final BlockRegistryObject<Relay, PEBlockItem> RELAY_MK2 = registerRelay("relay_mk2", EnumRelayTier.MK2, state -> 11);

	public static final BlockRegistryObject<Relay, PEBlockItem> RELAY_MK3 = registerRelay("relay_mk3", EnumRelayTier.MK3, state -> 15);



	private static BlockRegistryObject<Block, BlockItem> registerFuelBlock(String name, MapColor mapColor) {

		return BLOCKS.register(name, id -> new Block(PERegistryUtil.blockProps(id).mapColor(mapColor).instrument(NoteBlockInstrument.BASEDRUM)

				.requiresCorrectToolForDrops().strength(0.5F, 1.5F)),
                (block, props) -> new BlockItem(block, PEItems.fuelProperties(props, switch (name) {
                    case "alchemical_coal_block" -> 6400 * 9;
                    case "mobius_fuel_block" -> 25600 * 9;
                    default -> 102400 * 9;
                })));

	}



	private static BlockRegistryObject<Collector, PEBlockItem> registerCollector(String name, EnumCollectorTier collectorTier, ToIntFunction<BlockState> lightLevel) {

		return BLOCKS.register(name, id -> new Collector(collectorTier, PERegistryUtil.blockProps(id).mapColor(MapColor.SAND)

				.instrument(NoteBlockInstrument.PLING).sound(SoundType.GLASS).requiresCorrectToolForDrops().strength(0.3F, 0.9F)

				.lightLevel(lightLevel)), (block, props) -> new PEBlockItem(block, props, (stack, tooltip, flags) -> block.addTooltip(stack, tooltip)));

	}



	private static <CONDENSER extends Condenser> BlockRegistryObject<CONDENSER, BlockItem> registerCondenser(String name,
			Function<BlockBehaviour.Properties, CONDENSER> condenserFunction, BiFunction<CONDENSER, Item.Properties, BlockItem> itemCreator) {
		return BLOCKS.register(name, id -> condenserFunction.apply(PERegistryUtil.blockProps(id).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
				.requiresCorrectToolForDrops().strength(10, 3_600_000)), itemCreator);
	}



	private static BlockRegistryObject<Relay, PEBlockItem> registerRelay(String name, EnumRelayTier relayTier, ToIntFunction<BlockState> lightLevel) {

		return BLOCKS.register(name, id -> new Relay(relayTier, PERegistryUtil.blockProps(id).mapColor(MapColor.COLOR_BLACK)

				.instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()

				.strength(10, 30).lightLevel(lightLevel)), (block, props) -> new PEBlockItem(block, props, (stack, tooltip, flags) -> block.addTooltip(stack, tooltip)));

	}



	private static BlockRegistryObject<ProjectETNT, BlockItem> registerExplosive(String name, TNTEntityCreator tntEntityCreator) {

		return BLOCKS.register(name, id -> new ProjectETNT(PERegistryUtil.blockProps(id).mapColor(MapColor.FIRE).strength(0).instabreak()

				.sound(SoundType.GRASS).ignitedByLava().isRedstoneConductor((state, getter, pos) -> false), tntEntityCreator));

	}



	private static BlockRegistryObject<MatterFurnace, BlockItem> registerFurnace(String name, EnumMatterType matterType, float hardness, float resistance) {

		return BLOCKS.register(name, id -> new MatterFurnace(PERegistryUtil.blockProps(id).requiresCorrectToolForDrops().strength(hardness, resistance)

				.mapColor(matterType.getMapColor()).instrument(NoteBlockInstrument.BASEDRUM).lightLevel(state -> 14), matterType),

				(block, props) -> new BlockItem(block, props.fireResistant()));

	}



	private static BlockRegistryObject<MatterBlock, BlockItem> registerMatterBlock(String name, EnumMatterType matterType, float hardness, float resistance) {

		return BLOCKS.register(name, id -> new MatterBlock(PERegistryUtil.blockProps(id).requiresCorrectToolForDrops().strength(hardness, resistance)

				.mapColor(matterType.getMapColor()).instrument(NoteBlockInstrument.BASEDRUM).lightLevel(state -> 14), matterType),

				(block, props) -> new BlockItem(block, props.fireResistant()));

	}

}
