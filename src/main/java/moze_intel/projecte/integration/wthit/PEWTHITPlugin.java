package moze_intel.projecte.integration.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class PEWTHITPlugin implements IWailaPlugin {

	@Override
	public void register(IRegistrar registrar) {
		registrar.addComponent(WTHITDataProvider.INSTANCE, TooltipPosition.BODY, Block.class);
	}
}