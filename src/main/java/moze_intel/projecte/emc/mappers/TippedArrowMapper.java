package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

@EMCMapper
public class TippedArrowMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		int recipeCount = 0;
		NSSItem nssArrow = NSSItem.createItem(Items.ARROW);
		for (Potion potionType : ForgeRegistries.POTIONS.getValues()) {
			Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
			ingredientsWithAmount.put(nssArrow, 8);
			ingredientsWithAmount.put(NSSItem.createItem(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potionType)), 1);
			mapper.addConversion(8, NSSItem.createItem(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), potionType)), ingredientsWithAmount);
			recipeCount++;
		}
		PECore.debugLog("TippedArrowMapper Statistics:");
		PECore.debugLog("Found {} Tipped Arrow Recipes", recipeCount);
	}

	@Override
	public String getName() {
		return "TippedArrowMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for all lingering potions to arrow recipes";
	}
}