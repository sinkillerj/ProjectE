package moze_intel.projecte.utils;

import moze_intel.projecte.emc.SimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.Set;

public final class NBTWhitelist
{
	private static final Set<SimpleStack> STACKS = new HashSet<>();

	public static boolean register(ItemStack stack)
	{
		SimpleStack s = new SimpleStack(stack);
		return s.isValid() && STACKS.add(s.withMeta(OreDictionary.WILDCARD_VALUE));
	}

	public static boolean shouldDupeWithNBT(ItemStack stack)
	{
		SimpleStack s = new SimpleStack(stack);
		return s.isValid() && STACKS.contains(s);
	}
}
