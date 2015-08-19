package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryMapper extends LazyMapper {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		this.mapper = mapper;
		if (config.getBoolean("blacklistOresAndDusts", "", true, "Set EMC=0 for everything that has an OD Name that starts with `ore`, `dust` or `crushed` besides `dustPlastic`")) {
			//Black-list all ores/dusts
			for (String s : OreDictionary.getOreNames()) {
				if (s.startsWith("ore") || s.startsWith("dust") || s.startsWith("crushed")) {
					//Some exceptions in the black-listing
					if (s.equals("dustPlastic")) {
						continue;
					}

					for (ItemStack stack : ItemHelper.getODItems(s)) {
						if (stack == null) {
							continue;
						}

						mapper.setValueBefore(NormalizedSimpleStack.getFor(stack), 0);
						mapper.setValueAfter(NormalizedSimpleStack.getFor(stack), 0);
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "OreDictionaryMapper";
	}

	@Override
	public String getDescription() {
		return "Blacklist some OreDictionary names from getting an EMC value";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
