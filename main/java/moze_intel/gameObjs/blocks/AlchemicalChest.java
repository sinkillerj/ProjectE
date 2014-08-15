package moze_intel.gameObjs.blocks;

import java.util.Random;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.tiles.AlchChestTile;
import moze_intel.utils.Constants;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AlchemicalChest extends BlockDirection implements ITileEntityProvider
{
	private Random rand = new Random();
	
	public AlchemicalChest() 
	{
		super(Material.rock);
		this.setBlockName("alchemy_chest");
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		this.setHardness(10.0f);
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
			player.openGui(MozeCore.instance, Constants.ALCH_CHEST_GUI, world, x, y, z);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		return new AlchChestTile();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
		this.blockIcon = register.registerIcon("obsidian");
    }
}
