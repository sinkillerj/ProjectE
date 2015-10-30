package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.arithmetics.FullFractionArithmetic;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.PELogger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {
	private static List<Pair<NormalizedSimpleStack, FluidStack>> melting = Lists.newArrayList();

	public static void addMelting(String odName, String fluidName, int amount) {
		addMelting(NormalizedSimpleStack.forOreDictionary(odName), fluidName, amount);
	}
	public static void addMelting(Item item, String fluidName, int amount) {
		addMelting(NormalizedSimpleStack.getFor(item), fluidName, amount);
	}
	public static void addMelting(Block block, String fluidName, int amount) {
		addMelting(NormalizedSimpleStack.getFor(block), fluidName, amount);
	}
	public static void addMelting(NormalizedSimpleStack stack, String fluidName, int amount) {
		Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid != null) {
			melting.add(Pair.of(stack, new FluidStack(fluid, amount)));
		} else {
			PELogger.logWarn("Can not get Fluid '%s'", fluidName);
		}
	}
	static {
		addMelting(Blocks.obsidian, "obisidan.molten", 288);
		addMelting(Blocks.glass, "glass.molten", 1000);
		addMelting(Blocks.glass_pane, "glass.molten", 250);
		addMelting(Items.ender_pearl, "ender", 250);

		addMelting("ingotIron", "iron.molten", 144);
		addMelting("ingotGold", "gold.molten", 144);
		addMelting("ingotCopper", "copper.molten", 144);
		addMelting("ingotTin", "tin.molten", 144);
		addMelting("ingotSilver", "silver.molten", 144);
		addMelting("ingotLead", "lead.molten", 144);
		addMelting("ingotNickel", "nickel.molten", 144);
		addMelting("ingotAluminum", "aluminum.molten", 144);
		addMelting("ingotArdite", "ardite.molten", 144);
		addMelting("ingotCobalt", "cobalt.molten", 144);
		addMelting("ingotPlatinum", "platinum.molten", 144);
		addMelting("ingotObsidian", "obsidian.molten", 144);
		addMelting("ingotElectrum", "electrum.molten", 144);
		addMelting("ingotInvar", "invar.molten", 144);
		addMelting("ingotSignalum", "signalum.molten", 144);
		addMelting("ingotLumium", "lumium.molten", 144);
		addMelting("ingotEnderium", "enderium.molten", 144);
		addMelting("ingotMithril", "mithril.molten", 144);

		addMelting("ingotBronze", "bronze.molten", 144);
		addMelting("ingotAluminumBrass", "aluminumbrass.molten", 144);
		addMelting("ingotManyullyn", "manyullyn.molten", 144);
		addMelting("ingotAlumite", "alumite.molten", 144);

		addMelting("gemEmerald", "emerald.liquid", 640);
		addMelting("dustRedstone", "redstone", 100);
		addMelting("dustGlowstone", "glowstone", 250);

		addMelting("dustCryotheum", "cryotheum", 100);
		addMelting("dustPryotheum", "pryotheum", 100);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		mapper.setValueBefore(NormalizedSimpleStack.getFor(FluidRegistry.WATER), Integer.MIN_VALUE/*=Free. TODO: Use IntArithmetic*/);
		//1 Bucket of Lava = 1 Block of Obsidian
		mapper.addConversion(1000, NormalizedSimpleStack.getFor(FluidRegistry.LAVA), Arrays.asList(NormalizedSimpleStack.getFor(Blocks.obsidian)));

		//Add Conversion in case MFR is not present and milk is not an actual fluid
		NormalizedSimpleStack fakeMilkFluid = NormalizedSimpleStack.createFake("fakeMilkFluid");
		mapper.setValueBefore(fakeMilkFluid, 16);
		mapper.addConversion(1, NormalizedSimpleStack.getFor(Items.milk_bucket), Arrays.asList(NormalizedSimpleStack.getFor(Items.bucket), fakeMilkFluid));

		Fluid milkFluid = FluidRegistry.getFluid("milk");
		if (milkFluid != null) {
			mapper.addConversion(1000, NormalizedSimpleStack.getFor(milkFluid), Arrays.asList(fakeMilkFluid));
		}

		if (!(mapper instanceof IExtendedMappingCollector)) throw new RuntimeException("Cannot add Extended Fluid Mappings to mapper!");
		IExtendedMappingCollector emapper = (IExtendedMappingCollector) mapper;
		FullFractionArithmetic fluidArithmetic = new FullFractionArithmetic();

		for (Pair<NormalizedSimpleStack, FluidStack> pair: melting) {
			emapper.addConversion(pair.getValue().amount, NormalizedSimpleStack.getFor(pair.getValue().getFluid()), Arrays.asList(pair.getKey()), fluidArithmetic);
		}

		for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			Fluid fluid = data.fluid.getFluid();
			mapper.addConversion(1, NormalizedSimpleStack.getFor(data.filledContainer),
					ImmutableMap.of(NormalizedSimpleStack.getFor(data.emptyContainer), 1, NormalizedSimpleStack.getFor(fluid), data.fluid.amount)
			);
		}
	}

	@Override
	public String getName() {
		return "FluidMapper";
	}

	@Override
	public String getDescription() {
		return "Adds Conversions for fluid container items and fluids.";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}