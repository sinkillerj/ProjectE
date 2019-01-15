package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.impl.ConversionProxyImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class APICustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	public static final APICustomEMCMapper instance = new APICustomEMCMapper();
	private static final int PRIORITY_MIN_VALUE = 0;
	private static final int PRIORITY_MAX_VALUE = 512;
	private static final int PRIORITY_DEFAULT_VALUE = 1;
	private APICustomEMCMapper() {}

	private final Map<String, Map<NormalizedSimpleStack, Long>> customEMCforMod = new HashMap<>();

	public void registerCustomEMC(String modid, Object o, long emcValue) {
		NormalizedSimpleStack stack = ConversionProxyImpl.instance.objectToNSS(o);
		if (stack == null) return;
		if (emcValue < 0) emcValue = 0;
		customEMCforMod.computeIfAbsent(modid, k -> new HashMap<>()).put(stack, emcValue);
	}

	@Override
	public String getName() {
		return "APICustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows other mods to set EMC values using the ProjectEAPI";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, IResourceManager resourceManager) {
		Map<String, Integer> priorityMap = new HashMap<>();

		for (String modId: customEMCforMod.keySet()) {
			String configKey = getName() + ".priority." + (modId == null ? "__no_modid" : modId);
			int priority = EMCMapper.getOrSetDefault(config, configKey, "Priority for this mod", PRIORITY_DEFAULT_VALUE);
			priorityMap.put(modId, priority);
		}

		List<String> modIds = new ArrayList<>(customEMCforMod.keySet());
		modIds.sort(Comparator.comparingInt(priorityMap::get).reversed());

		for(String modId : modIds) {
			String modIdOrUnknown = modId == null ? "unknown mod" : modId;
			if (customEMCforMod.containsKey(modId))
			{
				for (Map.Entry<NormalizedSimpleStack, Long> entry : customEMCforMod.get(modId).entrySet())
				{
					NormalizedSimpleStack normStack = entry.getKey();
					if (isAllowedToSet(modId, normStack, entry.getValue(), config))
					{
						mapper.setValueBefore(normStack, entry.getValue());
						PECore.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
					}
					else
					{
						PECore.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
					}
				}
			}
		}
	}

	private boolean isAllowedToSet(String modId, NormalizedSimpleStack stack, Long value, CommentedFileConfig config) {
		String itemName;
		if (stack instanceof NSSItem)
		{
			NSSItem item = (NSSItem)stack;
			itemName = item.itemName.toString();
		} else {
			itemName = "IntermediateFakeItemsUsedInRecipes:";
		}
		String modForItem = itemName.substring(0, itemName.indexOf(':'));
		String permission = "none"; // todo 1.13 get rid of this system? config.getString(modForItem,"permissions."+modId,"both", String.format("Allow '%s' to set and or remove values for '%s'. Options: [both, set, remove, none]", modId, modForItem), new String[]{"both", "set", "remove", "none"});
		if (permission.equals("both"))
		{
			return true;
		}
		if (value == 0)
		{
			return permission.equals("remove");
		}
		else
		{
			return permission.equals("set");
		}
	}
}