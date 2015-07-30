package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.IValueArithmetic;
import moze_intel.projecte.emc.IValueGenerator;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;
import moze_intel.projecte.emc.mappers.customConversions.json.FixedValues;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class DumpToFileCollector extends AbstractMappingCollector<NormalizedSimpleStack, Integer> implements IValueGenerator<NormalizedSimpleStack, Integer>
{
	public static String currentGroupName="default";
	CustomConversionFile out = new CustomConversionFile();
	IValueGenerator<NormalizedSimpleStack, Integer> inner;
	final File file;
	public DumpToFileCollector(File f, IValueGenerator<NormalizedSimpleStack, Integer> inner)
	{
		file = f;
		this.inner = inner;
	}

	@Override
	public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount)
	{
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
		if (something == null || ingredientsWithAmount.containsKey(null)) return;
		if (out.values == null) out.values = new FixedValues();
		if (out.values.conversion == null) out.values.conversion = Lists.newArrayList();
		out.values.conversion.add(CustomConversion.getFor(outnumber, something, ingredientsWithAmount));
	}

	@Override
	public void addConversion(int outnumber, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount);
		if (output == null || ingredientsWithAmount.containsKey(null)) return;
		if (out.groups == null) out.groups = Maps.newHashMap();
		if (!out.groups.containsKey(currentGroupName)) out.groups.put(currentGroupName, new ConversionGroup());
		ConversionGroup group = out.groups.get(currentGroupName);
		group.conversions.add(CustomConversion.getFor(outnumber, output, ingredientsWithAmount));
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, Integer value)
	{
		inner.setValueBefore(something, value);
		if (something == null) return;
		if (out.values == null) out.values = new FixedValues();
		if (out.values.setValueBefore == null) out.values.setValueBefore = Maps.newHashMap();
		out.values.setValueBefore.put(nssToJson(something), value);
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, Integer value)
	{
		inner.setValueAfter(something, value);
		if (something == null) return;
		if (out.values == null) out.values = new FixedValues();
		if (out.values.setValueAfter == null) out.values.setValueAfter = Maps.newHashMap();
		out.values.setValueAfter.put(nssToJson(something), value);
	}

	@Override
	public Map<NormalizedSimpleStack, Integer> generateValues()
	{
		try
		{
			FileWriter fileWriter = new FileWriter(file);
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.setPrettyPrinting().create();
			gson.toJson(out, fileWriter);
			fileWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return inner.generateValues();
	}

	public static String nssToJson(NormalizedSimpleStack stack) {
		if (stack instanceof NormalizedSimpleStack.NSSItem) {
			NormalizedSimpleStack.NSSItem item = (NormalizedSimpleStack.NSSItem) stack;
			Object itemObject = Item.itemRegistry.getObjectById(item.id);
			if (itemObject != null)
			{
				String itemName = Item.itemRegistry.getNameForObject(itemObject);
				if (itemName != null)
				{
					return String.format("%s|%d", itemName, item.damage);
				}
			}
		} else if (stack instanceof NormalizedSimpleStack.NSSOreDictionary) {
			return "OD|" + ((NormalizedSimpleStack.NSSOreDictionary) stack).od;
		} else if (stack instanceof NormalizedSimpleStack.NSSFake) {
			return "FAKE|" + ((NormalizedSimpleStack.NSSFake) stack).counter + " " + ((NormalizedSimpleStack.NSSFake) stack).description;
		} else if (stack instanceof NormalizedSimpleStack.NSSFluid) {
			return "FLUID|" + ((NormalizedSimpleStack.NSSFluid) stack).name;
		}
		throw new IllegalArgumentException("No JSON String representation for " + stack);
	}
}
