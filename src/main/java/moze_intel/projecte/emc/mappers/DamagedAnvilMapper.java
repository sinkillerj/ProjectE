package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.Collections;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.block.Blocks;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;

@EMCMapper
public class DamagedAnvilMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		NSSItem anvil = NSSItem.createItem(Blocks.ANVIL);
		//Rough values based on how we factor damage into item EMC based on an average of ~8.3 uses before
		// it degrades a tier, we use 9 so the numbers are slightly worse for how efficiently it translates down
		mapper.addConversion(25, NSSItem.createItem(Blocks.CHIPPED_ANVIL), Collections.singletonMap(anvil, 16));
		mapper.addConversion(25, NSSItem.createItem(Blocks.DAMAGED_ANVIL), Collections.singletonMap(anvil, 7));
	}

	@Override
	public String getName() {
		return "DamagedAnvilMapper";
	}

	@Override
	public String getDescription() {
		//Anvils survive on average for 25 uses https://minecraft.gamepedia.com/Anvil#Becoming_damaged
		return "Calculates values for chipped and damaged anvils based on the average of surviving for 25 uses.";
	}
}