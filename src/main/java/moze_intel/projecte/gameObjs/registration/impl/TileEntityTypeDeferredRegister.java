package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeDeferredRegister extends WrappedDeferredRegister<TileEntityType<?>> {

	public TileEntityTypeDeferredRegister() {
		super(ForgeRegistries.TILE_ENTITIES);
	}

	@SuppressWarnings("ConstantConditions")
	public <TILE extends TileEntity> TileEntityTypeRegistryObject<TILE> register(BlockRegistryObject<?, ?> block, Supplier<? extends TILE> factory) {
		//Note: There is no data fixer type as forge does not currently have a way exposing data fixers to mods yet
		return register(block.getInternalRegistryName(), () -> TileEntityType.Builder.<TILE>create(factory, block.getBlock()).build(null),
				TileEntityTypeRegistryObject::new);
	}

	@SuppressWarnings("ConstantConditions")
	public <TILE extends TileEntity> TileEntityTypeRegistryObject<TILE> register(WallOrFloorBlockRegistryObject<?, ?, ?> block, Supplier<? extends TILE> factory) {
		//Note: There is no data fixer type as forge does not currently have a way exposing data fixers to mods yet
		return register(block.getInternalRegistryName(), () -> TileEntityType.Builder.<TILE>create(factory, block.getBlock(), block.getWallBlock()).build(null),
				TileEntityTypeRegistryObject::new);
	}
}