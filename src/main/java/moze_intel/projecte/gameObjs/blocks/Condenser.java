package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class Condenser extends AlchemicalChest implements ITileEntityProvider
{
	public Condenser() 
	{
		super();
		this.setUnlocalizedName("pe_condenser");
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int par2)
	{
		return Item.getItemFromBlock(ObjHandler.condenser);
	}
	
	@Override
	public int getRenderType()
	{
		return Constants.CONDENSER_RENDER_ID;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		return new CondenserTile();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
		{
			player.openGui(PECore.instance, Constants.CONDENSER_GUI, world, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		IInventory tile = (IInventory) world.getTileEntity(pos);

		if (tile == null)
		{
			return;
		}

		for (int i = 1; i < tile.getSizeInventory(); i++)
		{
			ItemStack stack = tile.getStackInSlot(i);

			if (stack == null)
			{
				continue;
			}

			WorldHelper.spawnEntityItem(world, stack, pos);
		}

		world.notifyNeighborsOfStateChange(pos, state.getBlock());
		world.removeTileEntity(pos);
	}
}
