package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class MatterBlock extends Block
{
	public static final IProperty TIER_PROP = PropertyEnum.create("tier", EnumMatterType.class);
	public MatterBlock() 
	{
		super(Material.iron);
		this.setCreativeTab(ObjHandler.cTab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TIER_PROP, EnumMatterType.DARK_MATTER));
		this.setUnlocalizedName("pe_matter_block");
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
	{
		EnumMatterType type = ((EnumMatterType) world.getBlockState(pos).getValue(TIER_PROP));

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
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		EnumMatterType type = ((EnumMatterType) world.getBlockState(pos).getValue(TIER_PROP));

		if (stack != null)
		{
			if (type == EnumMatterType.RED_MATTER)
			{
				return stack.getItem() == ObjHandler.rmPick;
			}
			else
			{
				return stack.getItem() == ObjHandler.rmPick || stack.getItem() == ObjHandler.dmPick;
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
		return ((EnumMatterType) state.getValue(TIER_PROP)).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TIER_PROP, EnumMatterType.values()[meta]);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, TIER_PROP);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item matterBlock, CreativeTabs cTab, List list)
	{
		for (int i = 0; i <= 1; i++)
		{
			list.add(new ItemStack(matterBlock , 1, i));
		}
	}

	public enum EnumMatterType implements IStringSerializable
	{
		DARK_MATTER("dark_matter"),
		RED_MATTER("red_matter");

		private final String name;

		EnumMatterType(String name)
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
