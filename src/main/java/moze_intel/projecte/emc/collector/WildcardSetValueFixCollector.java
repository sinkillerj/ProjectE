package moze_intel.projecte.emc.collector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;

/**
 * This Collector catches all setValueBefore, setValueAfter and setValueFromConversion calls that use Wildcard-Metadata.
 * These are then delayed until finishCollection() and will be expanded to all metadata that has been found.
 */
public class WildcardSetValueFixCollector<V extends Comparable<V>, A extends IValueArithmetic> extends AbstractMappingCollector<NormalizedSimpleStack, V, A> {
	IExtendedMappingCollector<NormalizedSimpleStack, V, A> inner;
	public WildcardSetValueFixCollector(IExtendedMappingCollector<NormalizedSimpleStack, V, A> inner) {
		super(inner.getArithmetic());
		this.inner = inner;
	}

	Map<NormalizedSimpleStack.NSSItem, V> setValueBeforeMap = Maps.newHashMap();
	Map<NormalizedSimpleStack.NSSItem, V> setValueAfterMap = Maps.newHashMap();
	List<CustomConversion> setValueConversionList = Lists.newArrayList();
	private boolean isWildCard(NormalizedSimpleStack nss) {
		return nss instanceof NormalizedSimpleStack.NSSItem && ((NormalizedSimpleStack.NSSItem) nss).damage == OreDictionary.WILDCARD_VALUE;
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, V value) {
		if (this.isWildCard(something)) {
			setValueBeforeMap.put((NormalizedSimpleStack.NSSItem) something, value);
		} else {
			inner.setValueBefore(something, value);
		}
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, V value) {
		if (this.isWildCard(something)) {
			setValueAfterMap.put((NormalizedSimpleStack.NSSItem) something, value);
		} else {
			inner.setValueAfter(something, value);
		}
	}

	@Override
	public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount) {
		if (this.isWildCard(something)) {
			setValueConversionList.add(CustomConversion.getFor(outnumber, something, ingredientsWithAmount));
		} else {
			inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
		}
	}

	@Override
	public void addConversion(int outnumber, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount, A arithmeticForConversion) {
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);

	}

	@Override
	public void finishCollection() {
		for (Map.Entry<NormalizedSimpleStack.NSSItem, V> entry: setValueBeforeMap.entrySet()) {
			for (Integer meta: NormalizedSimpleStack.getUsedMetadata(entry.getKey())) {
				if (meta == OreDictionary.WILDCARD_VALUE) continue;
				MappingCollector.debugFormat("Inserting Wildcard SetValueBefore %s:%d to %s", entry.getKey().itemName, meta, entry.getValue());
				inner.setValueBefore(NormalizedSimpleStack.getFor(entry.getKey().itemName, meta), entry.getValue());
			}
		}

		for (Map.Entry<NormalizedSimpleStack.NSSItem, V> entry: setValueAfterMap.entrySet()) {
			for (Integer meta: NormalizedSimpleStack.getUsedMetadata(entry.getKey())) {
				if (meta == OreDictionary.WILDCARD_VALUE) continue;
				inner.setValueAfter(NormalizedSimpleStack.getFor(entry.getKey().itemName, meta), entry.getValue());
				MappingCollector.debugFormat("Inserting Wildcard SetValueAfter: %s:%d to %s", entry.getKey().itemName, meta, entry.getValue());
			}
		}

		for (CustomConversion conversion: setValueConversionList) {
			for (Integer meta: NormalizedSimpleStack.getUsedMetadata(conversion.output)) {
				if (meta == OreDictionary.WILDCARD_VALUE) continue;
				MappingCollector.debugFormat("Inserting Wildcard SetValueFromConversion %s:%d to %s", conversion.output, meta, conversion);
				inner.setValueFromConversion(conversion.count, NormalizedSimpleStack.getFor(conversion.output, meta), ingredientMapFromStringMap(conversion.ingredients));
			}
		}
		inner.finishCollection();
	}

	private Map<NormalizedSimpleStack, Integer> ingredientMapFromStringMap(Map<String, Integer> map) {
		Map<NormalizedSimpleStack, Integer> out = Maps.newHashMap();
		for (Map.Entry<String, Integer> entry: map.entrySet()) {
			out.put(NormalizedSimpleStack.fromSerializedItem(entry.getKey()), entry.getValue());
		}
		return out;
	}
}
