package moze_intel.projecte.gameObjs.registries;

import java.util.function.Function;
import java.util.function.ToIntFunction;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.EnumFuelType;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.blocks.AlchemicalChest;
import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.blocks.CondenserMK2;
import moze_intel.projecte.gameObjs.blocks.InterdictionTorch;
import moze_intel.projecte.gameObjs.blocks.InterdictionTorchWall;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.blocks.Pedestal;
import moze_intel.projecte.gameObjs.blocks.ProjectETNT;
import moze_intel.projecte.gameObjs.blocks.ProjectETNT.TNTEntityCreator;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.blocks.TransmutationStone;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.items.blocks.CollectorItem;
import moze_intel.projecte.gameObjs.items.blocks.ItemFuelBlock;
import moze_intel.projecte.gameObjs.items.blocks.RelayItem;
import moze_intel.projecte.gameObjs.registration.impl.BlockDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.WallOrFloorItem;

public class PEBlocks {

	public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister();

	public static final BlockRegistryObject<AlchemicalChest, BlockItem> ALCHEMICAL_CHEST = BLOCKS.register("alchemical_chest", () -> new AlchemicalChest(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(10, 3_600_000)));
	public static final BlockRegistryObject<Block, ItemFuelBlock> ALCHEMICAL_COAL = registerFuelBlock("alchemical_coal_block", EnumFuelType.ALCHEMICAL_COAL);
	public static final BlockRegistryObject<Block, ItemFuelBlock> MOBIUS_FUEL = registerFuelBlock("mobius_fuel_block", EnumFuelType.MOBIUS_FUEL);
	public static final BlockRegistryObject<Block, ItemFuelBlock> AETERNALIS_FUEL = registerFuelBlock("aeternalis_fuel_block", EnumFuelType.AETERNALIS_FUEL);
	public static final BlockRegistryObject<Collector, CollectorItem> COLLECTOR = registerCollector("collector_mk1", EnumCollectorTier.MK1, state -> 7);
	public static final BlockRegistryObject<Collector, CollectorItem> COLLECTOR_MK2 = registerCollector("collector_mk2", EnumCollectorTier.MK2, state -> 11);
	public static final BlockRegistryObject<Collector, CollectorItem> COLLECTOR_MK3 = registerCollector("collector_mk3", EnumCollectorTier.MK3, state -> 15);
	public static final BlockRegistryObject<Condenser, BlockItem> CONDENSER = registerCondenser("condenser_mk1", Condenser::new);
	public static final BlockRegistryObject<CondenserMK2, BlockItem> CONDENSER_MK2 = registerCondenser("condenser_mk2", CondenserMK2::new);
	public static final BlockRegistryObject<Pedestal, BlockItem> DARK_MATTER_PEDESTAL = BLOCKS.register("dm_pedestal", () -> new Pedestal(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(1, 3).setLightLevel(state -> 12)));
	public static final BlockRegistryObject<MatterFurnace, BlockItem> DARK_MATTER_FURNACE = registerFurnace("dm_furnace", EnumMatterType.DARK_MATTER, 1_000_000, 3_000_000);
	public static final BlockRegistryObject<MatterFurnace, BlockItem> RED_MATTER_FURNACE = registerFurnace("rm_furnace", EnumMatterType.RED_MATTER, 2_000_000, 6_000_000);
	public static final BlockRegistryObject<MatterBlock, BlockItem> DARK_MATTER = registerMatterBlock("dark_matter_block", EnumMatterType.DARK_MATTER, 1_000_000, 3_000_000);
	public static final BlockRegistryObject<MatterBlock, BlockItem> RED_MATTER = registerMatterBlock("red_matter_block", EnumMatterType.RED_MATTER, 2_000_000, 6_000_000);
	public static final WallOrFloorBlockRegistryObject<InterdictionTorch, InterdictionTorchWall, WallOrFloorItem> INTERDICTION_TORCH = BLOCKS.registerWallOrFloorItem("interdiction_torch", InterdictionTorch::new, InterdictionTorchWall::new, AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0).setLightLevel(state -> 14).tickRandomly());
	public static final BlockRegistryObject<ProjectETNT, BlockItem> NOVA_CATALYST = registerExplosive("nova_catalyst", EntityNovaCatalystPrimed::new);
	public static final BlockRegistryObject<ProjectETNT, BlockItem> NOVA_CATACLYSM = registerExplosive("nova_cataclysm", EntityNovaCataclysmPrimed::new);
	public static final BlockRegistryObject<TransmutationStone, BlockItem> TRANSMUTATION_TABLE = BLOCKS.register("transmutation_table", () -> new TransmutationStone(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(10, 30)));
	public static final BlockRegistryObject<Relay, RelayItem> RELAY = registerRelay("relay_mk1", EnumRelayTier.MK1, state -> 7);
	public static final BlockRegistryObject<Relay, RelayItem> RELAY_MK2 = registerRelay("relay_mk2", EnumRelayTier.MK2, state -> 11);
	public static final BlockRegistryObject<Relay, RelayItem> RELAY_MK3 = registerRelay("relay_mk3", EnumRelayTier.MK3, state -> 15);

	private static BlockRegistryObject<Block, ItemFuelBlock> registerFuelBlock(String name, EnumFuelType fuelType) {
		return BLOCKS.registerDefaultProperties(name, () -> new Block(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool()
				.hardnessAndResistance(0.5F, 1.5F)), (block, properties) -> new ItemFuelBlock(block, properties, fuelType));
	}

	private static BlockRegistryObject<Collector, CollectorItem> registerCollector(String name, EnumCollectorTier collectorTier, ToIntFunction<BlockState> lightLevel) {
		//TODO - 1.16: Make this require a pickaxe??
		return BLOCKS.registerDefaultProperties(name, () -> new Collector(collectorTier, AbstractBlock.Properties.create(Material.GLASS)
				.hardnessAndResistance(0.3F, 0.9F).setLightLevel(lightLevel)), CollectorItem::new);
	}

	private static <CONDENSER extends Condenser> BlockRegistryObject<CONDENSER, BlockItem> registerCondenser(String name,
			Function<AbstractBlock.Properties, CONDENSER> condenserFunction) {
		return BLOCKS.register(name, () -> condenserFunction.apply(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool()
				.hardnessAndResistance(10, 3_600_000)));
	}

	private static BlockRegistryObject<Relay, RelayItem> registerRelay(String name, EnumRelayTier relayTier, ToIntFunction<BlockState> lightLevel) {
		return BLOCKS.registerDefaultProperties(name, () -> new Relay(relayTier, AbstractBlock.Properties.create(Material.ROCK).setRequiresTool()
				.hardnessAndResistance(10, 30).setLightLevel(lightLevel)), RelayItem::new);
	}

	private static BlockRegistryObject<ProjectETNT, BlockItem> registerExplosive(String name, TNTEntityCreator tntEntityCreator) {
		return BLOCKS.register(name, () -> new ProjectETNT(AbstractBlock.Properties.create(Material.TNT).hardnessAndResistance(0), tntEntityCreator));
	}

	private static BlockRegistryObject<MatterFurnace, BlockItem> registerFurnace(String name, EnumMatterType matterType, float hardness, float resistance) {
		return BLOCKS.register(name, () -> new MatterFurnace(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(hardness, resistance)
				.setLightLevel(state -> 14), matterType));
	}

	private static BlockRegistryObject<MatterBlock, BlockItem> registerMatterBlock(String name, EnumMatterType matterType, float hardness, float resistance) {
		return BLOCKS.register(name, () -> new MatterBlock(AbstractBlock.Properties.create(Material.IRON).setRequiresTool().hardnessAndResistance(hardness, resistance)
				.setLightLevel(state -> 14), matterType));
	}
}