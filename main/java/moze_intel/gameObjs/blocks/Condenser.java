package moze_intel.gameObjs.blocks;

import java.util.Random;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.tiles.CondenserTile;
import moze_intel.utils.Constants;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Condenser extends AlchemicalChest implements ITileEntityProvider
{
	public Condenser() 
	{
		super();
		this.setBlockName("condenser");
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
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		return new CondenserTile();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
			player.openGui(MozeCore.instance, Constants.CONDENSER_GUI, world, x, y, z);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
		this.blockIcon = register.registerIcon("obsidian");
    }
}
