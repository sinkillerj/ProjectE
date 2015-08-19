package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.IValueGenerator;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import java.io.File;
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
		out.values.conversion.add(CustomConversion.getFor(outnumber, something, ingredientsWithAmount));
	}

	@Override
	public void addConversion(int outnumber, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount);
		if (output == null || ingredientsWithAmount.containsKey(null)) return;
		if (!out.groups.containsKey(currentGroupName)) out.groups.put(currentGroupName, new ConversionGroup());
		ConversionGroup group = out.groups.get(currentGroupName);
		group.conversions.add(CustomConversion.getFor(outnumber, output, ingredientsWithAmount));
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, Integer value)
	{
		inner.setValueBefore(something, value);
		if (something == null) return;
		out.values.setValueBefore.put(something.json(), value);
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, Integer value)
	{
		inner.setValueAfter(something, value);
		if (something == null) return;
		out.values.setValueAfter.put(something.json(), value);
	}

	@Override
	public Map<NormalizedSimpleStack, Integer> generateValues()
	{
		try
		{
			out.write(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return inner.generateValues();
	}
}
