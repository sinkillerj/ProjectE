package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack> {
    private IMappingCollector<NormalizedSimpleStack> mapper;
    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack> mapper) {
        mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(FluidRegistry.WATER), Double.NaN, IMappingCollector.FixedValue.FixAndInherit);
        mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(FluidRegistry.LAVA), 64, IMappingCollector.FixedValue.FixAndInherit);
        Map<String, Double> fixValue = new HashMap<String, Double>();
        fixValue.put("milk", 16.0);
        for (Map.Entry<String, Double> entry: fixValue.entrySet()) {
            Fluid f = FluidRegistry.getFluid(entry.getKey());
            if (f != null) {
                mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(f), entry.getValue(), IMappingCollector.FixedValue.FixAndInherit);
            }
        }

        Map<String, NormalizedSimpleStack> molten = new HashMap<String, NormalizedSimpleStack>();
        molten.put("obsidian.molten", NormalizedSimpleStack.getNormalizedSimpleStackFor(Blocks.obsidian));
        molten.put("glass.molten", NormalizedSimpleStack.getNormalizedSimpleStackFor(Blocks.glass));
        molten.put("ender", NormalizedSimpleStack.getNormalizedSimpleStackFor(Items.ender_pearl));

        for (Map.Entry<String, NormalizedSimpleStack> entry: molten.entrySet()) {
            Fluid f = FluidRegistry.getFluid(entry.getKey());
            if (f != null) {
                mapper.addConversion(1, NormalizedSimpleStack.getNormalizedSimpleStackFor(f), Arrays.asList(entry.getValue()));
            }
        }

        for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            Fluid fluid = data.fluid.getFluid();
            mapper.addConversion(1, NormalizedSimpleStack.getNormalizedSimpleStackFor(data.filledContainer), Arrays.asList(NormalizedSimpleStack.getNormalizedSimpleStackFor(data.emptyContainer),NormalizedSimpleStack.getNormalizedSimpleStackFor(fluid)));
        }
    }
}
