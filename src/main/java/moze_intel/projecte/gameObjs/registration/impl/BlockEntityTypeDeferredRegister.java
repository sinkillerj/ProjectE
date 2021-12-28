package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockEntityTypeDeferredRegister extends WrappedDeferredRegister<BlockEntityType<?>> {

	public BlockEntityTypeDeferredRegister() {
		super(ForgeRegistries.BLOCK_ENTITIES);
	}

	public <TILE extends BlockEntity> BlockEntityTypeBuilder<TILE> builder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends TILE> factory) {
		return new BlockEntityTypeBuilder<>(block, factory);
	}

	public class BlockEntityTypeBuilder<TILE extends BlockEntity> {

		private final BlockRegistryObject<?, ?> block;
		private final BlockEntityType.BlockEntitySupplier<? extends TILE> factory;
		@Nullable
		private BlockEntityTicker<TILE> clientTicker;
		@Nullable
		private BlockEntityTicker<TILE> serverTicker;

		private BlockEntityTypeBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends TILE> factory) {
			this.block = block;
			this.factory = factory;
		}

		public BlockEntityTypeBuilder<TILE> clientTicker(BlockEntityTicker<TILE> ticker) {
			this.clientTicker = ticker;
			return this;
		}

		public BlockEntityTypeBuilder<TILE> serverTicker(BlockEntityTicker<TILE> ticker) {
			this.serverTicker = ticker;
			return this;
		}

		public BlockEntityTypeBuilder<TILE> commonTicker(BlockEntityTicker<TILE> ticker) {
			return clientTicker(ticker).serverTicker(ticker);
		}

		@SuppressWarnings("ConstantConditions")
		public BlockEntityTypeRegistryObject<TILE> build() {
			BlockEntityTypeRegistryObject<TILE> registryObject = new BlockEntityTypeRegistryObject<>(null);
			registryObject.clientTicker(clientTicker).serverTicker(serverTicker);
			return register(block.getInternalRegistryName(), () -> {
						Block[] validBlocks;
						if (block instanceof WallOrFloorBlockRegistryObject wallOrFloorBlock) {
							validBlocks = new Block[]{block.getBlock(), wallOrFloorBlock.getWallBlock()};
						} else {
							validBlocks = new Block[]{block.getBlock()};
						}
						return BlockEntityType.Builder.<TILE>of(factory, validBlocks).build(null);
					}, registryObject::setRegistryObject);
		}
	}
}