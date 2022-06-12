package moze_intel.projecte.integration.jade;

import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PEJadePlugin implements IWailaPlugin {

	@Override
	public void registerClient(IWailaClientRegistration registrar) {
		registrar.registerBlockComponent(JadeDataProvider.INSTANCE, Block.class);
	}
}