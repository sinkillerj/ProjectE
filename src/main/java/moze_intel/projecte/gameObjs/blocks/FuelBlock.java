package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class FuelBlock extends Block 
{
	public static final IProperty FUEL_PROP = PropertyEnum.create("fueltype", EnumFuelType.class);
	public FuelBlock() 
	{
		super(Material.rock);
		this.setUnlocalizedName("pe_fuel_block");
		this.setCreativeTab(ObjHandler.cTab);
		this.setHardness(0.5f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FUEL_PROP, EnumFuelType.ALCHEMICAL_COAL));
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.getMetaFromState(state);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFuelType) state.getValue(FUEL_PROP)).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FUEL_PROP, EnumFuelType.values()[meta]);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FUEL_PROP);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item fuelBlock, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 3; i++)
		{
			list.add(new ItemStack(fuelBlock , 1, i));
		}
	}

	public enum EnumFuelType implements IStringSerializable
	{
		ALCHEMICAL_COAL("alchemical_coal"),
		MOBIUS_FUEL("mobius_fuel"),
		AETERNALIS_FUEL("aeternalis_fuel");

		private final String name;

		EnumFuelType(String name)
		{
			this.name = name;
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
