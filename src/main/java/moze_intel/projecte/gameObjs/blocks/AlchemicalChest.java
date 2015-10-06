package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.ComparatorHelper;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class AlchemicalChest extends BlockDirection
{
	public AlchemicalChest() 
	{
		super(Material.rock);
		this.setUnlocalizedName("pe_alchemy_chest");
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		this.setHardness(10.0f);
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setResistance(6000000.0F);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int par2)
	{
		return Item.getItemFromBlock(ObjHandler.alchChest);
	}
	
	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return 2;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
		{
			player.openGui(PECore.instance, Constants.ALCH_CHEST_GUI, world, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World var1, IBlockState state)
	{
		return new AlchChestTile();
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos)
	{
		return ComparatorHelper.getForAlchChest(world, pos);
	}
}
