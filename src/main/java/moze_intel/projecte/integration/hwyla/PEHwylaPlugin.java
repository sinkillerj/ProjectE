package moze_intel.projecte.integration.hwyla;

import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.world.level.block.Block;

@WailaPlugin
public class PEHwylaPlugin implements IWailaPlugin {

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void registerClient(IWailaClientRegistration registrar) {
		registrar.registerComponentProvider(HwylaDataProvider.INSTANCE, TooltipPosition.BODY, Block.class);
	}
}