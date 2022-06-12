package moze_intel.projecte.integration.wtht;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import moze_intel.projecte.PECore;
import net.minecraft.world.level.block.Block;

@WailaPlugin(id = PECore.MODID)
public class PEWTHITPlugin implements IWailaPlugin {

	@Override
	public void register(IRegistrar registrar) {
		registrar.addComponent(WTHITDataProvider.INSTANCE, TooltipPosition.BODY, Block.class);
	}
}