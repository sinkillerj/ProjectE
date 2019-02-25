package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.api.state.enums.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class MatterBlock extends Block
{
	
	public MatterBlock() 
	{
		super(Material.IRON);
		this.setCreativeTab(ObjHandler.cTab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PEStateProps.TIER_PROP, EnumMatterType.DARK_MATTER));
		this.setTranslationKey("pe_matter_block");
		this.setHardness(1000000F);
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
	{
		EnumMatterType type = state.getValue(PEStateProps.TIER_PROP);

		if (type == EnumMatterType.DARK_MATTER)
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
		EnumMatterType type = world.getBlockState(pos).getValue(PEStateProps.TIER_PROP);

		if (!stack.isEmpty())
		{
			if (type == EnumMatterType.RED_MATTER)
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
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.getMetaFromState(state);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(PEStateProps.TIER_PROP).ordinal();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(PEStateProps.TIER_PROP, EnumMatterType.values()[meta]);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PEStateProps.TIER_PROP);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		for (int i = 0; i <= 1; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}

}
