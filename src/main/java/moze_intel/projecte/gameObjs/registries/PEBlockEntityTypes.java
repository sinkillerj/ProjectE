package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.gameObjs.block_entities.AlchChestTile;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK3Tile;
import moze_intel.projecte.gameObjs.block_entities.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.block_entities.CondenserTile;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceTile;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalTile;
import moze_intel.projecte.gameObjs.block_entities.InterdictionTile;
import moze_intel.projecte.gameObjs.block_entities.RMFurnaceTile;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1Tile;
import moze_intel.projecte.gameObjs.block_entities.RelayMK2Tile;
import moze_intel.projecte.gameObjs.block_entities.RelayMK3Tile;
import moze_intel.projecte.gameObjs.block_entities.TileEmc;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;

public class PEBlockEntityTypes {

	public static final BlockEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = new BlockEntityTypeDeferredRegister();

	public static final BlockEntityTypeRegistryObject<AlchChestTile> ALCHEMICAL_CHEST = BLOCK_ENTITY_TYPES.builder(PEBlocks.ALCHEMICAL_CHEST, AlchChestTile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<CollectorMK1Tile> COLLECTOR = BLOCK_ENTITY_TYPES.builder(PEBlocks.COLLECTOR, CollectorMK1Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<CollectorMK2Tile> COLLECTOR_MK2 = BLOCK_ENTITY_TYPES.builder(PEBlocks.COLLECTOR_MK2, CollectorMK2Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<CollectorMK3Tile> COLLECTOR_MK3 = BLOCK_ENTITY_TYPES.builder(PEBlocks.COLLECTOR_MK3, CollectorMK3Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<CondenserTile> CONDENSER = BLOCK_ENTITY_TYPES.builder(PEBlocks.CONDENSER, CondenserTile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<CondenserMK2Tile> CONDENSER_MK2 = BLOCK_ENTITY_TYPES.builder(PEBlocks.CONDENSER_MK2, CondenserMK2Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<RelayMK1Tile> RELAY = BLOCK_ENTITY_TYPES.builder(PEBlocks.RELAY, RelayMK1Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<RelayMK2Tile> RELAY_MK2 = BLOCK_ENTITY_TYPES.builder(PEBlocks.RELAY_MK2, RelayMK2Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<RelayMK3Tile> RELAY_MK3 = BLOCK_ENTITY_TYPES.builder(PEBlocks.RELAY_MK3, RelayMK3Tile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<DMFurnaceTile> DARK_MATTER_FURNACE = BLOCK_ENTITY_TYPES.builder(PEBlocks.DARK_MATTER_FURNACE, DMFurnaceTile::new).commonTicker(TileEmc::tick).build();
	public static final BlockEntityTypeRegistryObject<RMFurnaceTile> RED_MATTER_FURNACE = BLOCK_ENTITY_TYPES.builder(PEBlocks.RED_MATTER_FURNACE, RMFurnaceTile::new).commonTicker(TileEmc::tick).build();
	//TODO - 1.18: Can this be a server ticker or does it need to be common
	public static final BlockEntityTypeRegistryObject<InterdictionTile> INTERDICTION_TORCH = BLOCK_ENTITY_TYPES.builder(PEBlocks.INTERDICTION_TORCH, InterdictionTile::new).commonTicker(InterdictionTile::tick).build();
	public static final BlockEntityTypeRegistryObject<DMPedestalTile> DARK_MATTER_PEDESTAL = BLOCK_ENTITY_TYPES.builder(PEBlocks.DARK_MATTER_PEDESTAL, DMPedestalTile::new).commonTicker(TileEmc::tick).build();
}