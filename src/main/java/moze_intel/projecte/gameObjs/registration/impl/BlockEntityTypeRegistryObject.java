package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityTypeRegistryObject<TILE extends BlockEntity> extends WrappedRegistryObject<BlockEntityType<TILE>> {

	@Nullable
	private BlockEntityTicker<TILE> clientTicker;
	@Nullable
	private BlockEntityTicker<TILE> serverTicker;

	public BlockEntityTypeRegistryObject(RegistryObject<BlockEntityType<TILE>> registryObject) {
		super(registryObject);
	}

	//Internal use only, overwrite the registry object
	BlockEntityTypeRegistryObject<TILE> setRegistryObject(RegistryObject<BlockEntityType<TILE>> registryObject) {
		this.registryObject = registryObject;
		return this;
	}

	//Internal use only
	BlockEntityTypeRegistryObject<TILE> clientTicker(BlockEntityTicker<TILE> ticker) {
		clientTicker = ticker;
		return this;
	}

	//Internal use only
	BlockEntityTypeRegistryObject<TILE> serverTicker(BlockEntityTicker<TILE> ticker) {
		serverTicker = ticker;
		return this;
	}

	@Nullable
	public BlockEntityTicker<TILE> getTicker(boolean isClient) {
		return isClient ? clientTicker : serverTicker;
	}
}