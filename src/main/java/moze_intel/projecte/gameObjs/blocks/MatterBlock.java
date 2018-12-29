package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class MatterBlock extends Block
{
	public final EnumMatterType matterType;
	public MatterBlock(Builder builder, EnumMatterType type)
	{
		super(builder/*Material.IRON*/);
		this.matterType = type;
		this.setCreativeTab(ObjHandler.cTab);
		this.setTranslationKey("pe_matter_block");
		this.setHardness(1000000F);
	}

	@Override
	public float getBlockHardness(IBlockState state, IBlockReader world, BlockPos pos)
	{
		if (matterType == EnumMatterType.DARK_MATTER)
		{
			return 1000000.0F;
		}
		else
		{
			return 2000000.0F;
		}
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player)
	{
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		if (!stack.isEmpty())
		{
			if (matterType == EnumMatterType.RED_MATTER)
			{
				return stack.getItem() == ObjHandler.rmPick || stack.getItem() == ObjHandler.rmStar || stack.getItem() == ObjHandler.rmHammer;
			}
			else
			{
				return stack.getItem() == ObjHandler.rmPick || stack.getItem() == ObjHandler.dmPick || stack.getItem() == ObjHandler.rmStar || stack.getItem() == ObjHandler.dmHammer || stack.getItem() == ObjHandler.rmHammer;
			}
		}
		
		return false;
	}
}
