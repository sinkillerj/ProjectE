package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DumpToFileCollector<A extends IValueArithmetic> extends AbstractMappingCollector<NormalizedSimpleStack, Integer, A>
{
	public static String currentGroupName="default";
	private final CustomConversionFile out = new CustomConversionFile();
	private final IExtendedMappingCollector<NormalizedSimpleStack, Integer, A> inner;
	private final File file;

	public DumpToFileCollector(File f, IExtendedMappingCollector<NormalizedSimpleStack, Integer, A> inner)
	{
		super(inner.getArithmetic());
		this.file = f;
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
	public void addConversion(int outnumber, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount, A arithmeticForConversion)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
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
	public void finishCollection()
	{
		try
		{
			out.write(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		inner.finishCollection();
	}
}
