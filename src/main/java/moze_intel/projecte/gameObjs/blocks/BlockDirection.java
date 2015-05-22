package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.gameObjs.tiles.TileEmcDirection;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockDirection extends BlockContainer
{
	public static final IProperty FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public BlockDirection(Material material)
	{
		super(material);
		this.setCreativeTab(ObjHandler.cTab);
	}

	@Override
	public BlockState createBlockState()
	{
		return new BlockState(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack)
	{
		setFacingMeta(world, pos, ((EntityPlayer) entityLiving));
		TileEntity tile = world.getTileEntity(pos);
		
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("ProjectEBlock") && tile instanceof TileEmc)
		{
			stack.getTagCompound().setInteger("x", pos.getX());
			stack.getTagCompound().setInteger("y", pos.getY());
			stack.getTagCompound().setInteger("z", pos.getZ());
			
			tile.readFromNBT(stack.getTagCompound());
		}
		
		if (tile instanceof TileEmcDirection)
		{
			((TileEmcDirection) tile).setRelativeOrientation(entityLiving, false);
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		IInventory tile = (IInventory) world.getTileEntity(pos);
		
		if (tile == null)
		{
			return;
		}
		
		for (int i = 0; i < tile.getSizeInventory(); i++)
		{
			ItemStack stack = tile.getStackInSlot(i);
			
			if (stack == null)
			{
				continue;
			}
			
			WorldHelper.spawnEntityItem(world, stack, pos);
		}
		
		world.notifyNeighborsOfStateChange(pos, state.getBlock());
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return;
		}
		
		ItemStack stack = player.getHeldItem();
		
		if (stack != null && stack.getItem() == ObjHandler.philosStone)
		{
			TileEntity tile = world.getTileEntity(pos);
			
			if (tile instanceof TileEmcDirection)
			{
				((TileEmcDirection) tile).setRelativeOrientation(player, true); // TODO 1.8 TE's really should be using the blockstate too for rotation...
			}
			setFacingMeta(world, pos, player);
		}
	}

	protected void setFacingMeta(World world, BlockPos pos, EntityPlayer player)
	{
		world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, player.getHorizontalFacing().getOpposite()));
	}

}
