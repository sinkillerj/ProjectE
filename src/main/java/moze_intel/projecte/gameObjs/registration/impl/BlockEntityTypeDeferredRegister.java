package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class BlockEntityTypeDeferredRegister extends WrappedDeferredRegister<BlockEntityType<?>> {

	public BlockEntityTypeDeferredRegister(String modid) {
		super(ForgeRegistries.BLOCK_ENTITY_TYPES, modid);
	}

	public <BE extends BlockEntity> BlockEntityTypeBuilder<BE> builder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
		return new BlockEntityTypeBuilder<>(block, factory);
	}

	public class BlockEntityTypeBuilder<BE extends BlockEntity> {

		private final BlockRegistryObject<?, ?> block;
		private final BlockEntityType.BlockEntitySupplier<? extends BE> factory;
		@Nullable
		private BlockEntityTicker<BE> clientTicker;
		@Nullable
		private BlockEntityTicker<BE> serverTicker;

		private BlockEntityTypeBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
			this.block = block;
			this.factory = factory;
		}

		public BlockEntityTypeBuilder<BE> clientTicker(BlockEntityTicker<BE> ticker) {
			if (clientTicker != null) {
				throw new IllegalStateException("Client ticker may only be set once.");
			}
			this.clientTicker = ticker;
			return this;
		}

		public BlockEntityTypeBuilder<BE> serverTicker(BlockEntityTicker<BE> ticker) {
			if (serverTicker != null) {
				throw new IllegalStateException("Server ticker may only be set once.");
			}
			this.serverTicker = ticker;
			return this;
		}

		public BlockEntityTypeBuilder<BE> commonTicker(BlockEntityTicker<BE> ticker) {
			return clientTicker(ticker).serverTicker(ticker);
		}

		@SuppressWarnings("ConstantConditions")
		public BlockEntityTypeRegistryObject<BE> build() {
			BlockEntityTypeRegistryObject<BE> registryObject = new BlockEntityTypeRegistryObject<>(null);
			registryObject.clientTicker(clientTicker).serverTicker(serverTicker);
			return register(block.getInternalRegistryName(), () -> {
						Block[] validBlocks;
						if (block instanceof WallOrFloorBlockRegistryObject wallOrFloorBlock) {
							validBlocks = new Block[]{block.getBlock(), wallOrFloorBlock.getWallBlock()};
						} else {
							validBlocks = new Block[]{block.getBlock()};
						}
						return BlockEntityType.Builder.<BE>of(factory, validBlocks).build(null);
					}, registryObject::setRegistryObject);
		}
	}
}