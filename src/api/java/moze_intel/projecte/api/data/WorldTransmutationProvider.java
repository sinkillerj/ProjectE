package moze_intel.projecte.api.data;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.world_transmutation.WorldTransmutationFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

/**
 * Base Data Generator Provider class for use in creating world transmutations json data files that ProjectE will read from the data pack.
 */
@ParametersAreNonnullByDefault
public abstract class WorldTransmutationProvider implements DataProvider {

	private final Map<Identifier, ConditionalBuilder> worldTransmutations = new LinkedHashMap<>();
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	private final PathProvider outputProvider;
	private final String modid;

	private final Set<BlockState> seenBlockStates = new ReferenceOpenHashSet<>();
	private final Set<Block> seenBlocks = new ReferenceOpenHashSet<>();

	protected WorldTransmutationProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modid) {
		this.outputProvider = output.createPathProvider(Target.DATA_PACK, "pe_world_transmutations");
		this.lookupProvider = lookupProvider;
		this.modid = modid;
	}

	@Override
	public final String getName() {
		return "World Transmutations: " + modid;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.lookupProvider.thenApply(registries -> {
			worldTransmutations.clear();
			addWorldTransmutations(registries);
			return registries;
		}).thenCompose(registries -> CompletableFuture.allOf(worldTransmutations.entrySet().stream()
				.map(entry -> DataProvider.saveStable(output, registries, WorldTransmutationFile.CONDITIONAL_CODEC,
						entry.getValue().build(), outputProvider.json(entry.getKey())))
				.toArray(CompletableFuture[]::new)
		));
	}

	/**
	 * Implement this method to add any world transmutation files.
	 *
	 * @param registries Access to holder lookups.
	 */
	protected abstract void addWorldTransmutations(HolderLookup.Provider registries);

	/**
	 * Creates and adds a world transmutation builder with the file located by data/modid/pe_world_transmutations/namespace.json
	 *
	 * @param id         modid:namespace
	 * @param conditions Any conditions necessary to have the file load.
	 *
	 * @return Builder
	 */
	protected WorldTransmutationBuilder createTransmutationBuilder(Identifier id, ICondition... conditions) {
		Objects.requireNonNull(id, "World Transmutation Builder ID cannot be null.");
		if (worldTransmutations.containsKey(id)) {
			throw new RuntimeException("World transmutation file '" + id + "' has already been registered.");
		}
		WorldTransmutationBuilder builder = new WorldTransmutationBuilder(seenBlocks, seenBlockStates);
		worldTransmutations.put(id, new ConditionalBuilder(builder, conditions));
		return builder;
	}

	private record ConditionalBuilder(WorldTransmutationBuilder builder, ICondition... conditions) {

		public Optional<WithConditions<WorldTransmutationFile>> build() {
			return Optional.of(new WithConditions<>(builder.build(), conditions));
		}
	}
}
