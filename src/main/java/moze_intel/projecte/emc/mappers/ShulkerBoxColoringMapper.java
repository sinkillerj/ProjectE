package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.Arrays;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;

@EMCMapper
public class ShulkerBoxColoringMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries, IResourceManager resourceManager) {
		int recipeCount = 0;
		NSSItem nssBox = NSSItem.createItem(Blocks.SHULKER_BOX);
		for (DyeColor color : DyeColor.values()) {
			Block coloredShulkerBox = ShulkerBoxBlock.getBlockByColor(color);
			if (coloredShulkerBox != Blocks.SHULKER_BOX) {
				mapper.addConversion(1, NSSItem.createItem(coloredShulkerBox), Arrays.asList(nssBox, NSSItem.createTag(color.getTag())));
				recipeCount++;
			}
		}
		PECore.debugLog("ShulkerBoxColoringMapper Statistics:");
		PECore.debugLog("Found {} Shulker Box Coloring Recipes", recipeCount);
	}

	@Override
	public String getName() {
		return "ShulkerBoxColoringMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for the recipes that recolor shulker boxes.";
	}
}