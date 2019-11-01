package moze_intel.projecte.gameObjs.items.tools;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public class RedPick extends DarkPick {

	public RedPick(Properties props) {
		super(props, (byte) 3, EnumMatterType.RED_MATTER, new String[]{"pe.redpick.mode1", "pe.redpick.mode2","pe.redpick.mode3", "pe.redpick.mode4"});
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		Block b = state.getBlock();
		if (b == ObjHandler.rmBlock || b == ObjHandler.rmFurnace) {
			return 1_200_000;
		}
		return super.getDestroySpeed(stack, state);
	}
}