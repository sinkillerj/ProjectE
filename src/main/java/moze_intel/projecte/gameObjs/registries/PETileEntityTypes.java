package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.gameObjs.registration.impl.TileEntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.TileEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;

public class PETileEntityTypes {

	public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister();

	public static final TileEntityTypeRegistryObject<AlchChestTile> ALCHEMICAL_CHEST = TILE_ENTITY_TYPES.register(PEBlocks.ALCHEMICAL_CHEST, AlchChestTile::new);
	public static final TileEntityTypeRegistryObject<CollectorMK1Tile> COLLECTOR = TILE_ENTITY_TYPES.register(PEBlocks.COLLECTOR, CollectorMK1Tile::new);
	public static final TileEntityTypeRegistryObject<CollectorMK2Tile> COLLECTOR_MK2 = TILE_ENTITY_TYPES.register(PEBlocks.COLLECTOR_MK2, CollectorMK2Tile::new);
	public static final TileEntityTypeRegistryObject<CollectorMK3Tile> COLLECTOR_MK3 = TILE_ENTITY_TYPES.register(PEBlocks.COLLECTOR_MK3, CollectorMK3Tile::new);
	public static final TileEntityTypeRegistryObject<CondenserTile> CONDENSER = TILE_ENTITY_TYPES.register(PEBlocks.CONDENSER, CondenserTile::new);
	public static final TileEntityTypeRegistryObject<CondenserMK2Tile> CONDENSER_MK2 = TILE_ENTITY_TYPES.register(PEBlocks.CONDENSER_MK2, CondenserMK2Tile::new);
	public static final TileEntityTypeRegistryObject<RelayMK1Tile> RELAY = TILE_ENTITY_TYPES.register(PEBlocks.RELAY, RelayMK1Tile::new);
	public static final TileEntityTypeRegistryObject<RelayMK2Tile> RELAY_MK2 = TILE_ENTITY_TYPES.register(PEBlocks.RELAY_MK2, RelayMK2Tile::new);
	public static final TileEntityTypeRegistryObject<RelayMK3Tile> RELAY_MK3 = TILE_ENTITY_TYPES.register(PEBlocks.RELAY_MK3, RelayMK3Tile::new);
	public static final TileEntityTypeRegistryObject<DMFurnaceTile> DARK_MATTER_FURNACE = TILE_ENTITY_TYPES.register(PEBlocks.DARK_MATTER_FURNACE, DMFurnaceTile::new);
	public static final TileEntityTypeRegistryObject<RMFurnaceTile> RED_MATTER_FURNACE = TILE_ENTITY_TYPES.register(PEBlocks.RED_MATTER_FURNACE, RMFurnaceTile::new);
	public static final TileEntityTypeRegistryObject<InterdictionTile> INTERDICTION_TORCH = TILE_ENTITY_TYPES.register(PEBlocks.INTERDICTION_TORCH, InterdictionTile::new);
	public static final TileEntityTypeRegistryObject<DMPedestalTile> DARK_MATTER_PEDESTAL = TILE_ENTITY_TYPES.register(PEBlocks.DARK_MATTER_PEDESTAL, DMPedestalTile::new);
}