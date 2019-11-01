package moze_intel.projecte.gameObjs.items.tools;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public class RedHammer extends DarkHammer {

	public RedHammer(Properties props) {
		super(props, (byte) 3, EnumMatterType.RED_MATTER);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		Block block = state.getBlock();
		if (block == ObjHandler.rmBlock || block == ObjHandler.rmFurnace) {
			return 1_200_000;
		}
		return super.getDestroySpeed(stack, state);
	}
}