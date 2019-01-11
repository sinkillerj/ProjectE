package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.impl.ConversionProxyImpl;
import net.minecraftforge.common.config.Configuration;

import java.util.List;
import java.util.Map;

public class APICustomConversionMapper implements IEMCMapper<NormalizedSimpleStack,Long>
{
	@Override
	public String getName()
	{
		return "APICustomConversionMapper";
	}

	@Override
	public String getDescription()
	{
		return "Allows other Mods to add Recipes to the EMC Calculation.";
	}

	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config)
	{
		for (Map.Entry<String, List<ConversionProxyImpl.APIConversion>> entry : ConversionProxyImpl.instance.storedConversions.entrySet())
		{
			String modid = entry.getKey();
			String configKey = getName() + ".allow." + modid;
			if (EMCMapper.getOrSetDefault(config, configKey, "Allow this mod to add conversions to the EMC Calculation", true)) {
				for (ConversionProxyImpl.APIConversion apiConversion: entry.getValue()) {
					mapper.addConversion(apiConversion.amount, apiConversion.output, apiConversion.ingredients);
				}
			}
		}

	}
}