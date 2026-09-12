package moze_intel.projecte.api.mapper.collector;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.HolderLookup;

/**
 * Test collector with no-op implementations so focused mapper tests only override the callbacks they need.
 */
public class NoOpMappingCollector<T, V extends Comparable<V>> implements IMappingCollector<T, V> {

	@Override
	public void addConversion(int outnumber, T output, Object2IntMap<T> ingredientsWithAmount) {
	}

	@Override
	public void addConversion(int outnumber, T output, Iterable<T> ingredients) {
	}

	@Override
	public void setValueBefore(T something, V value) {
	}

	@Override
	public void setValueAfter(T something, V value) {
	}

	@Override
	public void setValueFromConversion(int outnumber, T something, Iterable<T> ingredients) {
	}

	@Override
	public void setValueFromConversion(int outnumber, T something, Object2IntMap<T> ingredientsWithAmount) {
	}

	@Override
	public void finishCollection(HolderLookup.Provider registries) {
	}
}
