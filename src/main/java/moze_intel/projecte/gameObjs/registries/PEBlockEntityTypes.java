package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.block_entities.AlchBlockEntityChest;
import moze_intel.projecte.gameObjs.block_entities.EmcChestBlockEntity;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK1BlockEntity;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK2BlockEntity;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK3BlockEntity;
import moze_intel.projecte.gameObjs.block_entities.CondenserMK2BlockEntity;
import moze_intel.projecte.gameObjs.block_entities.CondenserBlockEntity;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalBlockEntity;
import moze_intel.projecte.gameObjs.block_entities.InterdictionTorchBlockEntity;
import moze_intel.projecte.gameObjs.block_entities.RMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1BlockEntity;
import moze_intel.projecte.gameObjs.block_entities.RelayMK2BlockEntity;
import moze_intel.projecte.gameObjs.block_entities.RelayMK3BlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;

public class PEBlockEntityTypes {

	public static final BlockEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = new BlockEntityTypeDeferredRegister(PECore.MODID);

	public static final BlockEntityTypeRegistryObject<AlchBlockEntityChest> ALCHEMICAL_CHEST = BLOCK_ENTITY_TYPES.builder(PEBlocks.ALCHEMICAL_CHEST, AlchBlockEntityChest::new).clientTicker(AlchBlockEntityChest::tickClient).serverTicker(AlchBlockEntityChest::tickServer).build();
	public static final BlockEntityTypeRegistryObject<CollectorMK1BlockEntity> COLLECTOR = BLOCK_ENTITY_TYPES.builder(PEBlocks.COLLECTOR, CollectorMK1BlockEntity::new).serverTicker(CollectorMK1BlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<CollectorMK2BlockEntity> COLLECTOR_MK2 = BLOCK_ENTITY_TYPES.builder(PEBlocks.COLLECTOR_MK2, CollectorMK2BlockEntity::new).serverTicker(CollectorMK1BlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<CollectorMK3BlockEntity> COLLECTOR_MK3 = BLOCK_ENTITY_TYPES.builder(PEBlocks.COLLECTOR_MK3, CollectorMK3BlockEntity::new).serverTicker(CollectorMK1BlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<CondenserBlockEntity> CONDENSER = BLOCK_ENTITY_TYPES.builder(PEBlocks.CONDENSER, CondenserBlockEntity::new).clientTicker(EmcChestBlockEntity::lidAnimateTick).serverTicker(CondenserBlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<CondenserMK2BlockEntity> CONDENSER_MK2 = BLOCK_ENTITY_TYPES.builder(PEBlocks.CONDENSER_MK2, CondenserMK2BlockEntity::new).clientTicker(EmcChestBlockEntity::lidAnimateTick).serverTicker(CondenserBlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<RelayMK1BlockEntity> RELAY = BLOCK_ENTITY_TYPES.builder(PEBlocks.RELAY, RelayMK1BlockEntity::new).serverTicker(RelayMK1BlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<RelayMK2BlockEntity> RELAY_MK2 = BLOCK_ENTITY_TYPES.builder(PEBlocks.RELAY_MK2, RelayMK2BlockEntity::new).serverTicker(RelayMK1BlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<RelayMK3BlockEntity> RELAY_MK3 = BLOCK_ENTITY_TYPES.builder(PEBlocks.RELAY_MK3, RelayMK3BlockEntity::new).serverTicker(RelayMK1BlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<DMFurnaceBlockEntity> DARK_MATTER_FURNACE = BLOCK_ENTITY_TYPES.builder(PEBlocks.DARK_MATTER_FURNACE, DMFurnaceBlockEntity::new).serverTicker(DMFurnaceBlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<RMFurnaceBlockEntity> RED_MATTER_FURNACE = BLOCK_ENTITY_TYPES.builder(PEBlocks.RED_MATTER_FURNACE, RMFurnaceBlockEntity::new).serverTicker(DMFurnaceBlockEntity::tickServer).build();
	public static final BlockEntityTypeRegistryObject<InterdictionTorchBlockEntity> INTERDICTION_TORCH = BLOCK_ENTITY_TYPES.builder(PEBlocks.INTERDICTION_TORCH, InterdictionTorchBlockEntity::new).commonTicker(InterdictionTorchBlockEntity::tick).build();
	public static final BlockEntityTypeRegistryObject<DMPedestalBlockEntity> DARK_MATTER_PEDESTAL = BLOCK_ENTITY_TYPES.builder(PEBlocks.DARK_MATTER_PEDESTAL, DMPedestalBlockEntity::new).clientTicker(DMPedestalBlockEntity::tickClient).serverTicker(DMPedestalBlockEntity::tickServer).build();
}