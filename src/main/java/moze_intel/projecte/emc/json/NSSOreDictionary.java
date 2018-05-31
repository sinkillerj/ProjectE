package moze_intel.projecte.emc.json;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NSSOreDictionary implements NormalizedSimpleStack {
	static final Map<String, NormalizedSimpleStack> oreDictStacks = new HashMap<>();

	public final String od;

	private NSSOreDictionary(String od) {
		this.od = od;
	}

	@Nullable
	public static NormalizedSimpleStack create(String oreDictionaryName) {
		return oreDictStacks.computeIfAbsent(oreDictionaryName, NSSOreDictionary::new);
	}

	@Override
	public int hashCode() {
		return od.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NSSOreDictionary && this.od.equals(((NSSOreDictionary) o).od);
	}

	@Override
	public String json() {
		return "OD|" + this.od;
	}

	@Override
	public String toString() {
		return "OD: " + od;
	}
}
