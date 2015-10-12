package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.ComparatorHelper;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class AlchemicalChest extends BlockDirection
{
	public AlchemicalChest() 
	{
		super(Material.rock);
		this.setBlockName("pe_alchemy_chest");
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		this.setHardness(10.0f);
		this.setResistance(6000000.0F);
	}
	
	@Override
	public Item getItemDropped(int par1, Random random, int par2)
	{
		return Item.getItemFromBlock(ObjHandler.alchChest);
	}
	
	@Override
	public boolean renderAsNormalBlock()
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
		return Constants.CHEST_RENDER_ID;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
		{
			player.openGui(PECore.instance, Constants.ALCH_CHEST_GUI, world, x, y, z);
		}
		
		return true;
	}

	@Override
	public boolean hasTileEntity(int meta)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World var1, int meta)
	{
		return new AlchChestTile();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("projecte:alchemy_chest");
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta)
	{
		return ComparatorHelper.getForAlchChest(world, x, y, z);
	}
}
