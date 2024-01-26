package moze_intel.projecte.gameObjs.registration.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject.CapabilityData;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEntityTypeDeferredRegister extends PEDeferredRegister<BlockEntityType<?>> {

	public BlockEntityTypeDeferredRegister(String modid) {
		super(Registries.BLOCK_ENTITY_TYPE, modid, BlockEntityTypeRegistryObject::new);
	}

	public <BE extends BlockEntity> BlockEntityTypeBuilder<BE> builder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
		return new BlockEntityTypeBuilder<>(block, factory);
	}

	@SuppressWarnings("unchecked")
	private <BE extends BlockEntity> BlockEntityTypeRegistryObject<BE> registerPE(String name, Supplier<? extends BlockEntityType<BE>> sup) {
		return (BlockEntityTypeRegistryObject<BE>) super.register(name, sup);
	}

	@Override
	public void register(@NotNull IEventBus bus) {
		super.register(bus);
		bus.addListener(this::registerCapabilities);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> entry : getEntries()) {
			//Note: All entries should be of this type
			if (entry instanceof BlockEntityTypeRegistryObject<?> beRO) {
				beRO.registerCapabilityProviders(event);
			} else if (!FMLEnvironment.production) {
				throw new IllegalStateException("Expected entry to be a BlockEntityTypeRegistryObject");
			}
		}
	}

	public class BlockEntityTypeBuilder<BE extends BlockEntity> {

		private static final ICapabilityProvider<?, ?, ?> SIMPLE_PROVIDER = (obj, context) -> obj;

		private final BlockRegistryObject<?, ?> block;
		private final BlockEntityType.BlockEntitySupplier<? extends BE> factory;
		private final List<CapabilityData<BE, ?, ?>> capabilityProviders = new ArrayList<>();
		@Nullable
		private BlockEntityTicker<BE> clientTicker;
		@Nullable
		private BlockEntityTicker<BE> serverTicker;

		BlockEntityTypeBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
			this.block = block;
			this.factory = factory;
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
			capabilityProviders.add(new CapabilityData<>(capability, provider, shouldApply));
			return this;
		}

		public BlockEntityTypeBuilder<BE> without(BlockCapability<?, ?>... capabilities) {
			for (BlockCapability<?, ?> capability : capabilities) {
				capabilityProviders.removeIf(data -> data.capability() == capability);
			}
			return this;
		}

		public BlockEntityTypeBuilder<BE> without(Collection<? extends BlockCapability<?, ?>> capabilities) {
			capabilityProviders.removeIf(data -> capabilities.contains(data.capability()));
			return this;
		}

		public BlockEntityTypeBuilder<BE> clientTicker(BlockEntityTicker<BE> ticker) {
			Preconditions.checkState(clientTicker == null, "Client ticker may only be set once.");
			clientTicker = ticker;
			return this;
		}

		public BlockEntityTypeBuilder<BE> serverTicker(BlockEntityTicker<BE> ticker) {
			Preconditions.checkState(serverTicker == null, "Server ticker may only be set once.");
			serverTicker = ticker;
			return this;
		}

		public BlockEntityTypeBuilder<BE> commonTicker(BlockEntityTicker<BE> ticker) {
			return clientTicker(ticker)
					.serverTicker(ticker);
		}

		@SuppressWarnings("ConstantConditions")
		public BlockEntityTypeRegistryObject<BE> build() {
			//Note: There is no data fixer type as forge does not currently have a way exposing data fixers to mods yet
			BlockEntityTypeRegistryObject<BE> holder = registerPE(block.getName(), () -> BlockEntityType.Builder.<BE>of(factory, block.getBlocks()).build(null));
			holder.tickers(clientTicker, serverTicker);
			holder.capabilities(capabilityProviders.isEmpty() ? null : capabilityProviders);
			return holder;
		}
	}
}