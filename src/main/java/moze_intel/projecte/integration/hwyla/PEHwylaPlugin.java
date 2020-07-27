package moze_intel.projecte.integration.hwyla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;

@WailaPlugin
public class PEHwylaPlugin implements IWailaPlugin {

	@Override
	public void register(IRegistrar registrar) {
		registrar.registerComponentProvider(HwylaDataProvider.INSTANCE, TooltipPosition.BODY, Block.class);
	}
}