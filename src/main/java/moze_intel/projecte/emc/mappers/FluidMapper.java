package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import moze_intel.projecte.emc.arithmetics.FullBigFractionArithmetic;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	private static final List<Pair<NormalizedSimpleStack, FluidStack>> melting = new ArrayList<>();
	private static final List<Pair<NormalizedSimpleStack, Pair<ResourceLocation, Integer>>> meltingAlt = new ArrayList<>();

	private static void addForgeMelting(String itemTagId, String fluidName, int amount) {
		addMelting(new ResourceLocation("forge", itemTagId), new ResourceLocation("forge", fluidName), amount);
	}

	private static void addMelting(ResourceLocation itemTagId, ResourceLocation fluidTag, int amount) {
		addMelting(NSSItem.createTag(itemTagId), fluidTag, amount);
	}

	private static void addMelting(Item item, String fluidName, int amount) {
		addMelting(NSSItem.createItem(item), new ResourceLocation("forge", fluidName), amount);
	}

	private static void addMelting(Block block, String fluidName, int amount) {
		addMelting(NSSItem.createItem(block), new ResourceLocation("forge", fluidName), amount);
	}

	private static void addMelting(NormalizedSimpleStack stack, ResourceLocation fluidTag, int amount) {
		meltingAlt.add(Pair.of(stack, Pair.of(fluidTag, amount)));
	}

	private static void addMelting(NormalizedSimpleStack stack, Fluid fluid, int amount) {
		melting.add(Pair.of(stack, new FluidStack(fluid, amount)));
	}

	static {
		addMelting(Blocks.OBSIDIAN, "molten/obisidan", 288);
		addMelting(Blocks.GLASS, "molten/glass", 1000);
		addMelting(Blocks.GLASS_PANE, "molten/glass", 250);
		addMelting(Items.ENDER_PEARL, "molten/ender", 250);

		addForgeMelting("ingots/iron", "molten/iron", 144);
		addForgeMelting("ingots/gold", "molten/gold", 144);
		addForgeMelting("ingots/copper", "molten/copper", 144);
		addForgeMelting("ingots/tin", "molten/tin", 144);
		addForgeMelting("ingots/silver", "molten/silver", 144);
		addForgeMelting("ingots/lead", "molten/lead", 144);
		addForgeMelting("ingots/nickel", "molten/nickel", 144);
		addForgeMelting("ingots/aluminum", "molten/aluminum", 144);
		addForgeMelting("ingots/ardite", "molten/ardite", 144);
		addForgeMelting("ingots/cobalt", "molten/cobalt", 144);
		addForgeMelting("ingots/platinum", "molten/platinum", 144);
		addForgeMelting("ingots/obsidian", "molten/obsidian", 144);
		addForgeMelting("ingots/electrum", "molten/electrum", 144);
		addForgeMelting("ingots/invar", "molten/invar", 144);
		addForgeMelting("ingots/signalum", "molten/signalum", 144);
		addForgeMelting("ingots/lumium", "molten/lumium", 144);
		addForgeMelting("ingots/enderium", "molten/enderium", 144);
		addForgeMelting("ingots/mithril", "molten/mithril", 144);

		addForgeMelting("ingots/bronze", "molten/bronze", 144);
		addForgeMelting("ingots/aluminum_brass", "molten/aluminum_brass", 144);
		addForgeMelting("ingots/manyullyn", "molten/manyullyn", 144);
		addForgeMelting("ingots/alumite", "molten/alumite", 144);

		addForgeMelting("gems/emerald", "emerald", 640);
		addForgeMelting("dusts/redstone", "redstone", 100);
		addForgeMelting("dusts/glowstone", "glowstone", 250);

		addForgeMelting("dusts/cryotheum", "cryotheum", 100);
		addForgeMelting("dusts/pryotheum", "pryotheum", 100);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, IResourceManager resourceManager) {
		mapper.setValueBefore(NSSFluid.createTag(FluidTags.WATER), Long.MIN_VALUE/*=Free. TODO: Use IntArithmetic*/);
		//1 Bucket of Lava = 1 Block of Obsidian
		mapper.addConversion(1000, NSSFluid.createTag(FluidTags.LAVA), Collections.singletonList(NSSItem.createItem(Blocks.OBSIDIAN)));

		NSSItem bucketNSS = NSSItem.createItem(Items.BUCKET);
		//Add Conversion in case MFR is not present and milk is not an actual fluid
		NormalizedSimpleStack fakeMilkFluid = NSSFake.create("fakeMilkFluid");
		mapper.setValueBefore(fakeMilkFluid, 16L);
		mapper.addConversion(1, NSSItem.createItem(Items.MILK_BUCKET), Arrays.asList(bucketNSS, fakeMilkFluid));

		NSSFluid milkNSS = NSSFluid.createTag(new ResourceLocation("forge", "milk"));
		mapper.addConversion(1000, milkNSS, Collections.singletonList(fakeMilkFluid));

		if (!(mapper instanceof IExtendedMappingCollector))
			throw new RuntimeException("Cannot add Extended Fluid Mappings to mapper!");
		IExtendedMappingCollector emapper = (IExtendedMappingCollector) mapper;
		FullBigFractionArithmetic fluidArithmetic = new FullBigFractionArithmetic();

		for (Pair<NormalizedSimpleStack, FluidStack> pair : melting) {
			emapper.addConversion(pair.getValue().getAmount(), NSSFluid.createFluid(pair.getValue().getFluid()), Collections.singletonList(pair.getKey()), fluidArithmetic);
		}
		for (Pair<NormalizedSimpleStack, Pair<ResourceLocation, Integer>> pair : meltingAlt) {
			emapper.addConversion(pair.getValue().getValue(), NSSFluid.createTag(pair.getValue().getKey()), Collections.singletonList(pair.getKey()), fluidArithmetic);
		}

		// TODO figure out a way to get all containers again since FluidContainerRegistry disappeared after fluid caps
		mapper.addConversion(1, NSSItem.createItem(Items.WATER_BUCKET), ImmutableMap.of(bucketNSS, 1, NSSFluid.createTag(FluidTags.WATER), 1000));
		mapper.addConversion(1, NSSItem.createItem(Items.LAVA_BUCKET), ImmutableMap.of(bucketNSS, 1, NSSFluid.createTag(FluidTags.LAVA), 1000));
		mapper.addConversion(1, NSSItem.createItem(Items.MILK_BUCKET), ImmutableMap.of(bucketNSS, 1, milkNSS, 1000));
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