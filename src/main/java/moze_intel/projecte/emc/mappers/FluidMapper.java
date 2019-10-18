package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableMap;
import moze_intel.projecte.emc.arithmetics.FullBigFractionArithmetic;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.json.NSSFake;
import moze_intel.projecte.emc.json.NSSFluid;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSTag;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	private static final List<Pair<NormalizedSimpleStack, FluidStack>> melting = new ArrayList<>();

	private static void addForgeMelting(String itemTagId, String fluidName, int amount) {
		addMelting(new ResourceLocation("forge", itemTagId), fluidName, amount);
		addMelting(NSSTag.create(itemTagId.toString()), fluidName, amount);
	}

	private static void addMelting(ResourceLocation itemTagId, String fluidName, int amount) {
		addMelting(NSSTag.create(itemTagId.toString()), fluidName, amount);
	}

	private static void addMelting(Item item, String fluidName, int amount) {
		addMelting(new NSSItem(item), fluidName, amount);
	}

	private static void addMelting(Block block, String fluidName, int amount) {
		addMelting(new NSSItem(block), fluidName, amount);
	}

	private static void addMelting(NormalizedSimpleStack stack, String fluidName, int amount) {
		//TODO: 1.14 fix getting fluids
		/*Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid != null) {
			melting.add(Pair.of(stack, new FluidStack(fluid, amount)));
		} else {
			PECore.LOGGER.warn("Can not get Fluid '{}'", fluidName);
		}*/
	}

	static {
		addMelting(Blocks.OBSIDIAN, "obisidan.molten", 288);
		addMelting(Blocks.GLASS, "glass.molten", 1000);
		addMelting(Blocks.GLASS_PANE, "glass.molten", 250);
		addMelting(Items.ENDER_PEARL, "ender", 250);

		addForgeMelting("ingots/iron", "iron.molten", 144);
		addForgeMelting("ingots/gold", "gold.molten", 144);
		addForgeMelting("ingots/copper", "copper.molten", 144);
		addForgeMelting("ingots/tin", "tin.molten", 144);
		addForgeMelting("ingots/silver", "silver.molten", 144);
		addForgeMelting("ingots/lead", "lead.molten", 144);
		addForgeMelting("ingots/nickel", "nickel.molten", 144);
		addForgeMelting("ingots/aluminum", "aluminum.molten", 144);
		addForgeMelting("ingots/ardite", "ardite.molten", 144);
		addForgeMelting("ingots/cobalt", "cobalt.molten", 144);
		addForgeMelting("ingots/platinum", "platinum.molten", 144);
		addForgeMelting("ingots/obsidian", "obsidian.molten", 144);
		addForgeMelting("ingots/electrum", "electrum.molten", 144);
		addForgeMelting("ingots/invar", "invar.molten", 144);
		addForgeMelting("ingots/signalum", "signalum.molten", 144);
		addForgeMelting("ingots/lumium", "lumium.molten", 144);
		addForgeMelting("ingots/enderium", "enderium.molten", 144);
		addForgeMelting("ingots/mithril", "mithril.molten", 144);

		addForgeMelting("ingots/bronze", "bronze.molten", 144);
		addForgeMelting("ingots/aluminum_brass", "aluminumbrass.molten", 144);
		addForgeMelting("ingots/manyullyn", "manyullyn.molten", 144);
		addForgeMelting("ingots/alumite", "alumite.molten", 144);

		addForgeMelting("gems/emerald", "emerald.liquid", 640);
		addForgeMelting("dusts/redstone", "redstone", 100);
		addForgeMelting("dusts/glowstone", "glowstone", 250);

		addForgeMelting("dusts/cryotheum", "cryotheum", 100);
		addForgeMelting("dusts/pryotheum", "pryotheum", 100);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, IResourceManager resourceManager) {
		mapper.setValueBefore(NSSFluid.create(Fluids.WATER), Long.MIN_VALUE/*=Free. TODO: Use IntArithmetic*/);
		//1 Bucket of Lava = 1 Block of Obsidian
		mapper.addConversion(1000, NSSFluid.create(Fluids.LAVA), Collections.singletonList(new NSSItem(Blocks.OBSIDIAN)));

		//Add Conversion in case MFR is not present and milk is not an actual fluid
		NormalizedSimpleStack fakeMilkFluid = NSSFake.create("fakeMilkFluid");
		mapper.setValueBefore(fakeMilkFluid, 16L);
		mapper.addConversion(1, new NSSItem(Items.MILK_BUCKET), Arrays.asList(new NSSItem(Items.BUCKET), fakeMilkFluid));

		//TODO: 1.14 fix getting fluids
		Fluid milkFluid = null;//FluidRegistry.getFluid("milk");
		if (milkFluid != null) {
			mapper.addConversion(1000, NSSFluid.create(milkFluid), Collections.singletonList(fakeMilkFluid));
		}

		if (!(mapper instanceof IExtendedMappingCollector))
			throw new RuntimeException("Cannot add Extended Fluid Mappings to mapper!");
		IExtendedMappingCollector emapper = (IExtendedMappingCollector) mapper;
		FullBigFractionArithmetic fluidArithmetic = new FullBigFractionArithmetic();

		for (Pair<NormalizedSimpleStack, FluidStack> pair : melting) {
			emapper.addConversion(pair.getValue().getAmount(), NSSFluid.create(pair.getValue().getFluid()), Collections.singletonList(pair.getKey()), fluidArithmetic);
		}

		// TODO figure out a way to get all containers again since FluidContainerRegistry disappeared after fluid caps
		mapper.addConversion(1, new NSSItem(Items.WATER_BUCKET), ImmutableMap.of(new NSSItem(Items.BUCKET), 1, NSSFluid.create(Fluids.WATER), 1000));
		mapper.addConversion(1, new NSSItem(Items.LAVA_BUCKET), ImmutableMap.of(new NSSItem(Items.BUCKET), 1, NSSFluid.create(Fluids.LAVA), 1000));
		if (milkFluid != null) {
			mapper.addConversion(1, new NSSItem(Items.MILK_BUCKET), ImmutableMap.of(new NSSItem(Items.BUCKET), 1, NSSFluid.create(milkFluid), 1000));
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