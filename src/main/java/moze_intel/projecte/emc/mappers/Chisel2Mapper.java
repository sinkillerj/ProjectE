package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


//Thanks to bdew for a first implementation of this: https://github.com/bdew/ProjectE/blob/f1b08624ff47c6cc716576701024cdb38ff3d297/src/main/java/moze_intel/projecte/emc/ChiselMapper.java
public class Chisel2Mapper implements IEMCMapper<NormalizedSimpleStack, Integer> {

	public final static String[] chiselBlockNames = new String[]{"marble", "limestone", "andesite", "granite", "diorite"};

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
		return Loader.isModLoaded("chisel") && "Chisel 2".equals(FMLCommonHandler.instance().findContainerFor("chisel").getName());
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		Chisel.ICarvingRegistry carvingRegistry;
		try
		{

			Chisel chisel = new Chisel();
			carvingRegistry = chisel.CarvingUtilsGetChiselRegistry();
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		if (carvingRegistry == null) return;
		for (String name: chiselBlockNames) {
			Block block = Block.getBlockFromName("chisel:" + name);
			if (block != null) {
				mapper.setValueBefore(NormalizedSimpleStack.getFor(block), 1);
			}
		}

		try
		{
			for (String name : carvingRegistry.getSortedGroupNames()) {
				handleCarvingGroup(mapper, config, carvingRegistry.getGroup(name));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void handleCarvingGroup(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config, Chisel.ICarvingGroup group) throws Exception {
		//XXX: Generates way too much Configs
		/*if (!config.getBoolean(group.getName(), "enableCarvingGroups", true, "Enable ICarvingGroup with name=" + group.getName() + (group.getOreName() == null ? "" :  " and oreName=" + group.getOreName())) ) {
			return;
		}*/
		List<NormalizedSimpleStack> stacks = new ArrayList<NormalizedSimpleStack>();
		for (Chisel.ICarvingVariation v : group.getVariations()) {
			stacks.add(NormalizedSimpleStack.getFor(Block.getIdFromBlock(v.getBlock()), v.getBlockMeta()));
		}
		if (group.getOreName() != null) {
			for (ItemStack ore : OreDictionary.getOres(group.getOreName())) {
				stacks.add(NormalizedSimpleStack.getFor(ore));
			}
		}
		for (int i = 1; i < stacks.size(); i++) {
			mapper.addConversion(1, stacks.get(0), Arrays.asList(new NormalizedSimpleStack[]{stacks.get(i)}));
			mapper.addConversion(1, stacks.get(i), Arrays.asList(new NormalizedSimpleStack[]{stacks.get(0)}));
		}
	}

	private static class Chisel {
		Class carvingUtilsClass  = Class.forName("com.cricketcraft.chisel.api.carving.CarvingUtils");
		Method getChiselRegistryMethod = carvingUtilsClass.getMethod("getChiselRegistry");
		public ICarvingRegistry CarvingUtilsGetChiselRegistry() throws Exception {
				return new ICarvingRegistry(getChiselRegistryMethod.invoke(null));
		}
		public class ICarvingGroup {
			Class iCarvingGroupClass = Class.forName("com.cricketcraft.chisel.api.carving.ICarvingGroup");
			Object self;
			public ICarvingGroup(Object self) throws Exception {
				this.self = self;
			}

			Method getOreNameMethod = iCarvingGroupClass.getMethod("getOreName");
			public String getOreName() throws Exception {
				return (String)getOreNameMethod.invoke(self);
			}

			Method getVariationsMethod = iCarvingGroupClass.getMethod("getVariations");
			public Collection<ICarvingVariation> getVariations() throws Exception {
				List<ICarvingVariation> variations = Lists.newArrayList();
				for (Object o: (Collection)getVariationsMethod.invoke(self)) {
					variations.add(new ICarvingVariation(o));
				}
				return variations;
			}
		}

		public class ICarvingRegistry
		{
			Class iCarvingRegistryClass = Class.forName("com.cricketcraft.chisel.api.carving.ICarvingRegistry");
			Object self;
			public ICarvingRegistry(Object carvingRegistry) throws Exception {
				this.self = carvingRegistry;
			}

			Method getSortedGroupNamesMethod = iCarvingRegistryClass.getMethod("getSortedGroupNames");
			public Collection<String> getSortedGroupNames() throws Exception {
				return (Collection<String>)getSortedGroupNamesMethod.invoke(self);
			}

			Method getGroupMethod = iCarvingRegistryClass.getMethod("getGroup", String.class);
			public ICarvingGroup getGroup(String name) throws Exception {
				return new ICarvingGroup(getGroupMethod.invoke(self, name));
			}
		}
		public class ICarvingVariation
		{
			Class iCarvingVariation  = Class.forName("com.cricketcraft.chisel.api.carving.ICarvingVariation");
			Object self;

			public ICarvingVariation(Object self) throws Exception
			{
				this.self = self;
			}

			Method getBlockMethod = iCarvingVariation.getMethod("getBlock");
			public Block getBlock() throws Exception {
				return (Block) getBlockMethod.invoke(self);
			}

			Method getBlockMetaMethod = iCarvingVariation.getMethod("getBlockMeta");
			public int getBlockMeta() throws Exception {
				return (Integer) getBlockMetaMethod.invoke(self);
			}
		}

		private Chisel() throws Exception
		{
		}
	}
}
