package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class BlockEntityTypeRegistryObject<BE extends BlockEntity> extends WrappedRegistryObject<BlockEntityType<BE>> {

	@Nullable
	private BlockEntityTicker<BE> clientTicker;
	@Nullable
	private BlockEntityTicker<BE> serverTicker;

	public BlockEntityTypeRegistryObject(RegistryObject<BlockEntityType<BE>> registryObject) {
		super(registryObject);
	}

	//Internal use only, overwrite the registry object
	BlockEntityTypeRegistryObject<BE> setRegistryObject(RegistryObject<BlockEntityType<BE>> registryObject) {
		this.registryObject = registryObject;
		return this;
	}

	//Internal use only
	BlockEntityTypeRegistryObject<BE> clientTicker(BlockEntityTicker<BE> ticker) {
		clientTicker = ticker;
		return this;
	}

	//Internal use only
	BlockEntityTypeRegistryObject<BE> serverTicker(BlockEntityTicker<BE> ticker) {
		serverTicker = ticker;
		return this;
	}

	@Nullable
	public BlockEntityTicker<BE> getTicker(boolean isClient) {
		return isClient ? clientTicker : serverTicker;
	}
}