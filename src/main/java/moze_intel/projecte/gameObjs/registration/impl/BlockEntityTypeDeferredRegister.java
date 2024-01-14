package moze_intel.projecte.gameObjs.registration.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject.WallOrFloorBlockRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

public class BlockEntityTypeDeferredRegister extends PEDeferredRegister<BlockEntityType<?>> {

	private final List<BlockEntityTypeRegistryObject<?>> allBlockEntities = new ArrayList<>();

	public BlockEntityTypeDeferredRegister(String modid) {
		//Note: We intentionally don't pass a more restrictive type for holder creation as we ignore the holder that gets created
		// in favor of one we create ourselves
		//TODO - 1.20.4: Re-evaluate
		super(Registries.BLOCK_ENTITY_TYPE, modid);
	}

	public <BE extends BlockEntity> BlockEntityTypeBuilder<BE> builder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
		return new BlockEntityTypeBuilder<>(block, factory);
	}

	@Override
	public void register(@NotNull IEventBus bus) {
		super.register(bus);
		bus.addListener(this::registerCapabilities);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (BlockEntityTypeRegistryObject<?> beRO : allBlockEntities) {
			beRO.registerCapabilityProviders(event);
		}
	}

	public class BlockEntityTypeBuilder<BE extends BlockEntity> {

		private static final ICapabilityProvider<?, ?, ?> SIMPLE_PROVIDER = (obj, context) -> obj;

		private final BlockRegistryObject<?, ?> block;
		private final BlockEntityType.BlockEntitySupplier<? extends BE> factory;
		private final BlockEntityTypeRegistryObject<BE> registryObject;

		BlockEntityTypeBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
			this.block = block;
			this.factory = factory;
			this.registryObject = new BlockEntityTypeRegistryObject<>(new ResourceLocation(getNamespace(), block.getName()));
		}

		public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> withSimple(BlockCapability<CAP, CONTEXT> capability) {
			return withSimple(capability, () -> true);
		}

		@SuppressWarnings("unchecked")
		public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> withSimple(BlockCapability<CAP, CONTEXT> capability, BooleanSupplier shouldApply) {
			return with(capability, (ICapabilityProvider<? super BE, CONTEXT, CAP>) SIMPLE_PROVIDER, shouldApply);
		}

		public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability,
				Function<BlockCapability<CAP, CONTEXT>, ICapabilityProvider<? super BE, CONTEXT, CAP>> provider) {
			return with(capability, provider.apply(capability));
		}

		public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider) {
			return with(capability, provider, () -> true);
		}

		/**
		 * @param shouldApply Determines whether the provider actually be attached to this block entity type. Useful for cases when we want to conditionally apply it
		 *                    based on loaded mods or a block's attributes.
		 */
		public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider,
				BooleanSupplier shouldApply) {
			registryObject.addCapability(capability, provider, shouldApply);
			return this;
		}

		public BlockEntityTypeBuilder<BE> without(BlockCapability<?, ?>... capabilities) {
			for (BlockCapability<?, ?> capability : capabilities) {
				registryObject.removeCapability(capability);
			}
			return this;
		}

		public BlockEntityTypeBuilder<BE> without(Collection<? extends BlockCapability<?, ?>> capabilities) {
			for (BlockCapability<?, ?> capability : capabilities) {
				registryObject.removeCapability(capability);
			}
			return this;
		}

		public BlockEntityTypeBuilder<BE> clientTicker(BlockEntityTicker<BE> ticker) {
			registryObject.clientTicker(ticker);
			return this;
		}

		public BlockEntityTypeBuilder<BE> serverTicker(BlockEntityTicker<BE> ticker) {
			registryObject.serverTicker(ticker);
			return this;
		}

		public BlockEntityTypeBuilder<BE> commonTicker(BlockEntityTicker<BE> ticker) {
			return clientTicker(ticker)
					.serverTicker(ticker);
		}

		@SuppressWarnings("ConstantConditions")
		public BlockEntityTypeRegistryObject<BE> build() {
			//Register the BE, but don't care about the returned holder as we already made the holder ourselves so that we could add extra data to it
			//Note: There is no data fixer type as forge does not currently have a way exposing data fixers to mods yet
			register(block.getName(), () -> {
				Block[] validBlocks;
				if (block instanceof WallOrFloorBlockRegistryObject<?, ?, ?> wallOrFloorBlock) {
					validBlocks = new Block[]{block.getBlock(), wallOrFloorBlock.getWallBlock()};
				} else {
					validBlocks = new Block[]{block.getBlock()};
				}
				return BlockEntityType.Builder.<BE>of(factory, validBlocks).build(null);
			});
			allBlockEntities.add(registryObject);
			return registryObject;
		}
	}
}