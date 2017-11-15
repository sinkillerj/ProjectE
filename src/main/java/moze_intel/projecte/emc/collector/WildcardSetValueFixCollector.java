package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

/**
 * This Collector catches all setValueBefore, setValueAfter and setValueFromConversion calls
 * that use Wildcard-Metadata on a NSSItem and "explodes" that NSSItem into all of its variants
 */
public class WildcardSetValueFixCollector<V extends Comparable<V>, A extends IValueArithmetic> extends AbstractMappingCollector<NormalizedSimpleStack, V, A> {
	private final IExtendedMappingCollector<NormalizedSimpleStack, V, A> inner;

	public WildcardSetValueFixCollector(IExtendedMappingCollector<NormalizedSimpleStack, V, A> inner) {
		super(inner.getArithmetic());
		this.inner = inner;
	}

	private boolean isWildCard(NormalizedSimpleStack nss) {
		return nss instanceof NSSItem && ((NSSItem) nss).damage == OreDictionary.WILDCARD_VALUE;
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, V value) {
		if (this.isWildCard(something)) {
			for (NormalizedSimpleStack nss : NormalizedSimpleStack.getVariants(((NSSItem) something).itemName)) {
				inner.setValueBefore(nss, value);
			}
		} else {
			inner.setValueBefore(something, value);
		}
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, V value) {
		if (this.isWildCard(something)) {
			for (NormalizedSimpleStack nss : NormalizedSimpleStack.getVariants(((NSSItem) something).itemName)) {
				inner.setValueAfter(nss, value);
			}
		} else {
			inner.setValueAfter(something, value);
		}
	}

	@Override
	public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount) {
		if (this.isWildCard(something)) {
			for (NormalizedSimpleStack nss : NormalizedSimpleStack.getVariants(((NSSItem) something).itemName)) {
				inner.setValueFromConversion(outnumber, nss, ingredientsWithAmount);
			}
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
		inner.finishCollection();
	}
}
