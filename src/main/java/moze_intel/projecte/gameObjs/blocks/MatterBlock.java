package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class MatterBlock extends Block {

	public final EnumMatterType matterType;

	public MatterBlock(Properties props, EnumMatterType type) {
		super(props);
		this.matterType = type;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
		if (!stack.isEmpty()) {
			if (matterType == EnumMatterType.RED_MATTER) {
				return stack.getItem() == ObjHandler.rmPick || stack.getItem() == ObjHandler.rmStar || stack.getItem() == ObjHandler.rmHammer;
			} else {
				return stack.getItem() == ObjHandler.rmPick || stack.getItem() == ObjHandler.dmPick || stack.getItem() == ObjHandler.rmStar || stack.getItem() == ObjHandler.dmHammer || stack.getItem() == ObjHandler.rmHammer;
			}
		}
		return false;
	}
}