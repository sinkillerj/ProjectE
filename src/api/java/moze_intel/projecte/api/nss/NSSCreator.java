package moze_intel.projecte.api.nss;

import com.google.gson.JsonParseException;
import java.util.function.Function;

public interface NSSCreator extends Function<String, NormalizedSimpleStack> {

	@Override
	NormalizedSimpleStack apply(String t) throws JsonParseException;
}