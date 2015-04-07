package moze_intel.projecte.emc.mappers;

import com.cricketcraft.chisel.api.carving.ICarvingGroup;
import com.cricketcraft.chisel.api.carving.ICarvingVariation;
import com.cricketcraft.chisel.carving.Carving;
import cpw.mods.fml.common.Loader;
import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import com.cricketcraft.chisel.init.ChiselBlocks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//Thanks to bdew for a first implementation of this: https://github.com/bdew/ProjectE/blob/f1b08624ff47c6cc716576701024cdb38ff3d297/src/main/java/moze_intel/projecte/emc/ChiselMapper.java
public class Chisel2Mapper implements IEMCMapper<NormalizedSimpleStack, Integer> {

	@Override
	public String getName() {
		return "Chisel2Mapper";
	}

	@Override
	public String getDescription() {
		return "Add mappings for Blocks that are created with the Chisel2-Chisel.";
	}

	@Override
	public boolean isAvailable() {
		return Loader.isModLoaded("chisel");
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(ChiselBlocks.marble), 1, IMappingCollector.FixedValue.FixAndInherit);
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(ChiselBlocks.limestone), 1, IMappingCollector.FixedValue.FixAndInherit);
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(ChiselBlocks.andesite), 1, IMappingCollector.FixedValue.FixAndInherit);
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(ChiselBlocks.granite), 1, IMappingCollector.FixedValue.FixAndInherit);
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(ChiselBlocks.diorite), 1, IMappingCollector.FixedValue.FixAndInherit);

		for (String name : Carving.chisel.getSortedGroupNames()) {
			handleCarvingGroup(mapper, config, Carving.chisel.getGroup(name));
		}
	}

	protected void handleCarvingGroup(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config, ICarvingGroup group) {
		//XXX: Generates way too much Configs
		/*if (!config.getBoolean(group.getName(), "enableCarvingGroups", true, "Enable ICarvingGroup with name=" + group.getName() + (group.getOreName() == null ? "" :  " and oreName=" + group.getOreName())) ) {
			return;
		}*/
		List<NormalizedSimpleStack> stacks = new ArrayList<NormalizedSimpleStack>();
		for (ICarvingVariation v : group.getVariations()) {
			stacks.add(NormalizedSimpleStack.getNormalizedSimpleStackFor(Block.getIdFromBlock(v.getBlock()), v.getBlockMeta()));
		}
		if (group.getOreName() != null) {
			for (ItemStack ore : OreDictionary.getOres(group.getOreName())) {
				stacks.add(NormalizedSimpleStack.getNormalizedSimpleStackFor(ore));
			}
		}
		for (int i = 1; i < stacks.size(); i++) {
			mapper.addConversion(1, stacks.get(0), Arrays.asList(new NormalizedSimpleStack[]{stacks.get(i)}));
			mapper.addConversion(1, stacks.get(i), Arrays.asList(new NormalizedSimpleStack[]{stacks.get(0)}));
		}
		PELogger.logInfo(String.format("Added %d Blocks for CarvingGroup %s", stacks.size(), group.getName()));
	}
}
