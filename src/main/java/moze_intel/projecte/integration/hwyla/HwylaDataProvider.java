package moze_intel.projecte.integration.hwyla;

import java.util.List;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.util.text.ITextComponent;

public class HwylaDataProvider implements IComponentProvider {

	static final HwylaDataProvider INSTANCE = new HwylaDataProvider();

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (ProjectEConfig.server.misc.hwylaTOPDisplay.get()) {
			long value = EMCHelper.getEmcValue(accessor.getStack());
			if (value > 0) {
				tooltip.add(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}
}