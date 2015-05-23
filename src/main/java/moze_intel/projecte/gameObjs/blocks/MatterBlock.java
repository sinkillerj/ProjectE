package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class MatterBlock extends Block
{
	public static final IProperty TIER_PROP = PropertyEnum.create("tier", EnumMatterBlockType.class);
	public MatterBlock() 
	{
		super(Material.iron);
		this.setCreativeTab(ObjHandler.cTab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TIER_PROP, EnumMatterBlockType.DARK_MATTER));
	}

	@Override
	public float getBlockHardness(World world, BlockPos pos)
	{
		EnumMatterBlockType type = ((EnumMatterBlockType) world.getBlockState(pos).getValue(TIER_PROP));

		if (type == EnumMatterBlockType.DARK_MATTER)
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
		ItemStack stack = player.getHeldItem();
		EnumMatterBlockType type = ((EnumMatterBlockType) world.getBlockState(pos).getValue(TIER_PROP));

		if (stack != null)
		{
			if (type == EnumMatterBlockType.RED_MATTER)
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
		return ((EnumMatterBlockType) state.getValue(TIER_PROP)).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TIER_PROP, EnumMatterBlockType.values()[meta]);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, TIER_PROP);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs cTab, List list)
	{
		for (int i = 0; i <= 1; i++)
		{
			list.add(new ItemStack(item , 1, i));
		}
	}

	public enum EnumMatterBlockType implements IStringSerializable
	{
		DARK_MATTER("dark_matter"),
		RED_MATTER("red_matter");

		private final String name;

		EnumMatterBlockType(String name)
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
