package moze_intel.projecte.gameObjs.registration.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.UnaryOperator;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

public class DataComponentTypeDeferredRegister extends PEDeferredRegister<DataComponentType<?>> {

	private static final Codec<Long> POSITIVE_LONG = Codec.LONG.validate(val -> val > 0 ? DataResult.success(val) : DataResult.error(() -> "Value must be positive: " + val));
	private static final Codec<Double> POSITIVE_DOUBLE = Codec.DOUBLE.validate(val -> val > 0 ? DataResult.success(val) : DataResult.error(() -> "Value must be positive: " + val));

	public DataComponentTypeDeferredRegister(String namespace) {
		super(Registries.DATA_COMPONENT_TYPE, namespace);
	}

	public <TYPE> PEDeferredHolder<DataComponentType<?>, DataComponentType<TYPE>> simple(String name, UnaryOperator<Builder<TYPE>> operator) {
		return register(name, () -> operator.apply(DataComponentType.builder()).build());
	}

	public PEDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> registerBoolean(String name) {
		return simple(name, builder -> builder.persistent(Codec.BOOL)
				.networkSynchronized(ByteBufCodecs.BOOL));
	}

	public PEDeferredHolder<DataComponentType<?>, DataComponentType<Byte>> registerByte(String name, byte min, byte max) {
		return simple(name, builder -> builder.persistent(Codec.BYTE
						.validate(val -> val >= min && val <= max ? DataResult.success(val) : DataResult.error(() -> "Value: " + val + " must be between " + min + " and " + max))
				).networkSynchronized(ByteBufCodecs.BYTE));
	}

	public PEDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerNonNegativeInt(String name) {
		return simple(name, builder -> builder.persistent(ExtraCodecs.POSITIVE_INT)
				.networkSynchronized(ByteBufCodecs.VAR_INT));
	}

	public PEDeferredHolder<DataComponentType<?>, DataComponentType<Long>> registerNonNegativeLong(String name) {
		return simple(name, builder -> builder.persistent(POSITIVE_LONG)
				.networkSynchronized(ByteBufCodecs.VAR_LONG));
	}

	public PEDeferredHolder<DataComponentType<?>, DataComponentType<Double>> registerNonNegativeDouble(String name) {
		return simple(name, builder -> builder.persistent(POSITIVE_DOUBLE)
				.networkSynchronized(ByteBufCodecs.DOUBLE));
	}
}
