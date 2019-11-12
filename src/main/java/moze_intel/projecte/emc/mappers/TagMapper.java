package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.Collections;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.AbstractNSSTag;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.IResourceManager;

public class TagMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, IResourceManager resourceManager) {
		AbstractNSSTag.getAllCreatedTags().forEach(stack -> stack.forEachElement(normalizedSimpleStack -> {
			mapper.addConversion(1, stack, Collections.singletonList(normalizedSimpleStack));
			mapper.addConversion(1, normalizedSimpleStack, Collections.singletonList(stack));
		}));
	}

	@Override
	public String getName() {
		return "TagMapper";
	}

	@Override
	public String getDescription() {
		return "Adds back and forth conversions of objects and their Tag variant. (EMC values assigned to tags will not behave properly if this mapper is disabled)";
	}
}