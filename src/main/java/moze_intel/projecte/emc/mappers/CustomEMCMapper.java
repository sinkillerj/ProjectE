package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;
import org.lwjgl.util.glu.Project;

import java.util.Map;

public class CustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper) {
		for (Map.Entry<NormalizedSimpleStack,Integer> entry : CustomEMCParser.userValues.entrySet()) {
			PELogger.logInfo("Adding custom EMC value for " + entry.getKey() + ": " + entry.getValue());
			mapper.setValue(entry.getKey(), entry.getValue(), IMappingCollector.FixedValue.FixAndInherit);
		}
	}
}
