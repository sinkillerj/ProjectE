package moze_intel.projecte.api.nss;

import java.util.function.Consumer;

//TODO: Name this better/add java docs explaining this is for ones that also support tags
public interface NSSTag extends NormalizedSimpleStack {

	//TODO: JavaDoc, Only does things if we are currently a "tag"
	void forEachElement(Consumer<NormalizedSimpleStack> consumer);
}