/*package moze_intel.projecte.integration.top;

import java.util.function.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

//Registered via IMC
@SuppressWarnings("unused")
public class PEProbeInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
		if (ProjectEConfig.server.misc.hwylaTOPDisplay.get()) {
			long value = EMCHelper.getEmcValue(new ItemStack(blockState.getBlock()));
			if (value > 0) {
				probeInfo.text(EMCHelper.getEmcTextComponent(value, 1).getFormattedText());
			}
		}
	}

	@Override
	public String getID() {
		return PECore.MODID + ":emc";
	}

	@Override
	public Void apply(ITheOneProbe iTheOneProbe) {
		iTheOneProbe.registerProvider(this);
		return null;
	}
}*/