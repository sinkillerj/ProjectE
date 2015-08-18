package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.ComparatorHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class Condenser extends AlchemicalChest
{
	public Condenser() 
	{
		super();
		this.setBlockName("pe_condenser");
	}
	
	@Override
	public Item getItemDropped(int par1, Random random, int par2)
	{
		return Item.getItemFromBlock(ObjHandler.condenser);
	}
	
	@Override
	public int getRenderType()
	{
		return Constants.CONDENSER_RENDER_ID;
	}

	@Override
	public boolean hasTileEntity(int meta)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		return new CondenserTile();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
		{
			player.openGui(PECore.instance, Constants.CONDENSER_GUI, world, x, y, z);
		}
		
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int noclue)
	{
		IInventory tile = (IInventory) world.getTileEntity(x, y, z);

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

			WorldHelper.spawnEntityItem(world, stack, x, y, z);
		}

		world.func_147453_f(x, y, z, block);
		world.removeTileEntity(x, y, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("projecte:condenser");
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta)
	{
		return ComparatorHelper.getForCondenser(world, x, y, z);
	}
}
