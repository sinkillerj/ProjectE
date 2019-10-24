package moze_intel.projecte.emc.mappers;

import java.util.Collection;
import java.util.Collections;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.json.NSSTag;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

//TODO: Should this be a normal "Mapper", currently this is just an extraction of what used to be NormalizedSimpleStack#addMappings
// We really have no reason and probably should not let this mapper be disabled.
public class TagMapper {

	public static <V extends Comparable<V>> void addMappings(IMappingCollector<NormalizedSimpleStack, V> mapper) {
		//TODO: Get all the NormalizedSimpleStacks, should this be part of the mapper?
		Collection<NormalizedSimpleStack> stacks = Collections.emptyList();
		for (NormalizedSimpleStack stack : stacks) {
			if (stack instanceof NSSTag) {
				((NSSTag) stack).forEachElement(normalizedSimpleStack -> {
					mapper.addConversion(1, stack, Collections.singletonList(normalizedSimpleStack));
					mapper.addConversion(1, normalizedSimpleStack, Collections.singletonList(stack));
				});
			}
		}
	}
}