package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.api.state.enums.EnumFuelType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class FuelBlock extends Block 
{

	public FuelBlock() 
	{
		super(Material.ROCK);
		this.setTranslationKey("pe_fuel_block");
		this.setCreativeTab(ObjHandler.cTab);
		this.setHardness(0.5f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PEStateProps.FUEL_PROP, EnumFuelType.ALCHEMICAL_COAL));
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.getMetaFromState(state);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(PEStateProps.FUEL_PROP).ordinal();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(PEStateProps.FUEL_PROP, EnumFuelType.values()[meta]);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PEStateProps.FUEL_PROP);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		for (int i = 0; i < 3; i++)
		{
			list.add(new ItemStack(this , 1, i));
		}
	}

}
