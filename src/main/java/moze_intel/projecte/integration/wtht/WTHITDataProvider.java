package moze_intel.projecte.integration.wtht;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.EMCHelper;

public class WTHITDataProvider implements IBlockComponentProvider {

	static final WTHITDataProvider INSTANCE = new WTHITDataProvider();

	@Override
	public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
		if (ProjectEConfig.server.misc.hwylaTOPDisplay.get()) {
			long value = EMCHelper.getEmcValue(accessor.getBlock());
			if (value > 0) {
				tooltip.addLine(EMCHelper.getEmcTextComponent(value, 1));
			}
		}
	}
}