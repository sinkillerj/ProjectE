package moze_intel.projecte.api.world_transmutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

/**
 * @param originState    defines what will match this transmutation.
 * @param result    defines what the normal right-click result of the transmutation will be.
 * @param altResult defines what the shift right-click result will be. May be equal to result.
 */
public record WorldTransmutation(@NotNull BlockState originState, @NotNull BlockState result, @NotNull BlockState altResult) implements IWorldTransmutation {

	static final String ORIGIN_KEY = "origin";
	static final String RESULT_KEY = "result";
	static final String ALT_RESULT_KEY = "alt_result";

	private static final Codec<BlockState> STATE_CODEC = NeoForgeExtraCodecs.withAlternative(BuiltInRegistries.BLOCK.byNameCodec().flatXmap(
			block -> DataResult.success(block.defaultBlockState()),
			state -> {
				if (state.getValues().findAny().isEmpty()) {
					return DataResult.success(state.getBlock());
				}
				return DataResult.error(() -> "Flattened state codec cannot be used for blocks that define any properties.");
			}
	), BlockState.CODEC);
	private static final StreamCodec<ByteBuf, BlockState> STATE_STREAM_CODEC = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY);

	/**
	 * Codec for serializing and deserializing World Transmutations.
	 */
	public static final Codec<WorldTransmutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			STATE_CODEC.fieldOf(ORIGIN_KEY).forGetter(WorldTransmutation::originState),
			STATE_CODEC.fieldOf(RESULT_KEY).forGetter(WorldTransmutation::result),
			STATE_CODEC.optionalFieldOf(ALT_RESULT_KEY).forGetter(entry -> entry.hasAlternate() ? Optional.of(entry.altResult()) : Optional.empty())
	).apply(instance, (origin, result, altResult) -> new WorldTransmutation(origin, result, altResult.orElse(result))));
	/**
	 * Stream codec for serializing and deserializing World Transmutations over the network.
	 */
	public static final StreamCodec<ByteBuf, WorldTransmutation> STREAM_CODEC = new StreamCodec<>() {
		@NotNull
		@Override
		public WorldTransmutation decode(@NotNull ByteBuf buffer) {
			BlockState origin = STATE_STREAM_CODEC.decode(buffer);
			BlockState result = STATE_STREAM_CODEC.decode(buffer);
			if (buffer.readBoolean()) {
				return new WorldTransmutation(origin, result, STATE_STREAM_CODEC.decode(buffer));
			}
			return new WorldTransmutation(origin, result);
		}

		@Override
		public void encode(@NotNull ByteBuf buffer, @NotNull WorldTransmutation transmutation) {
			STATE_STREAM_CODEC.encode(buffer, transmutation.originState());
			STATE_STREAM_CODEC.encode(buffer, transmutation.result());
			boolean hasAlternate = transmutation.hasAlternate();
			buffer.writeBoolean(hasAlternate);
			if (hasAlternate) {
				STATE_STREAM_CODEC.encode(buffer, transmutation.altResult());
			}
		}
	};

	public WorldTransmutation {
		Objects.requireNonNull(originState, "Origin state cannot be null");
		Objects.requireNonNull(result, "Result state cannot be null");
		Objects.requireNonNull(altResult, "Alternate result state cannot be null");
	}

	/**
	 * @param originState defines what will match this transmutation.
	 * @param result defines what the normal right-click result of the transmutation will be.
	 */
	public WorldTransmutation(@NotNull BlockState originState, @NotNull BlockState result) {
		this(originState, result, result);
	}

	/**
	 * Creates a {@link WorldTransmutation} for the given states. If none of the states have any properties this will instead return a {@link SimpleWorldTransmutation}.
	 *
	 * @param origin defines what will match this transmutation.
	 * @param result defines what the normal right-click result of the transmutation will be.
	 */
	@NotNull
	public static IWorldTransmutation of(@NotNull BlockState origin, @NotNull BlockState result) {
		if (origin.getValues().findAny().isEmpty() && result.getValues().findAny().isEmpty()) {
			return new SimpleWorldTransmutation(origin.typeHolder(), result.typeHolder());
		}
		return new WorldTransmutation(origin, result);
	}

	/**
	 * Creates a {@link WorldTransmutation} for the given states. If none of the states have any properties this will instead return a {@link SimpleWorldTransmutation}.
	 *
	 * @param origin    defines what will match this transmutation.
	 * @param result    defines what the normal right-click result of the transmutation will be.
	 * @param altResult defines what the shift right-click result will be. May be equal to result.
	 */
	@NotNull
	public static IWorldTransmutation of(@NotNull BlockState origin, @NotNull BlockState result, @NotNull BlockState altResult) {
		if (origin.getValues().findAny().isEmpty() && result.getValues().findAny().isEmpty() && altResult.getValues().findAny().isEmpty()) {
			return new SimpleWorldTransmutation(origin.typeHolder(), result.typeHolder(), altResult.typeHolder());
		}
		return new WorldTransmutation(origin, result, altResult);
	}

	@Override
	public Holder<Block> origin() {
		return originState.typeHolder();
	}

	@Override
	public boolean hasAlternate() {
		return altResult != result;
	}

	@Override
	public BlockState result(@NotNull BlockState state, boolean isSneaking) {
		if (canTransmute(state)) {
			return isSneaking ? altResult : result;
		}
		return null;
	}

	@Override
	public boolean canTransmute(@NotNull BlockState state) {
		return state == originState;
	}

	@Override
	public String toString() {
		String representation = "World Transmutation from: '" + originState + "' to: '" + result + "'";
		if (hasAlternate()) {
			representation += ", with secondary output of: '" + altResult + "'";
		}
		return representation;
	}
}
