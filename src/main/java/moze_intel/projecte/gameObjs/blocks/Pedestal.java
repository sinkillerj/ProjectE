package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Vincent on 2/9/2015.
 */
public class Pedestal extends Block implements ITileEntityProvider {

    public Pedestal() {
        super(Material.rock);
        this.setCreativeTab(ObjHandler.cTab);
        textureName = "dmPedestal";
        setBlockName("pe_" + textureName);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float f1, float f2, float f3)
    {
        if (world.isRemote || player.getHeldItem() == null)
        {
            return true;
        }
        else
        {
            DMPedestalTile tile = (DMPedestalTile) world.getTileEntity(x, y, z);
            if (player.isSneaking())
            {
                if (tile.currentItem != null)
                {
                    ItemStack item = tile.currentItem;
                    tile.currentItem = null;
                    Utils.spawnEntityItem(world, item, x, y + 1, z);
                }
                else
                {
                    tile.currentItem = player.getHeldItem();
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }
            }
            else
            {
                if (tile.currentItem != null)
                {
                    tile.toggleState();
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        this.blockIcon = register.registerIcon("projecte:"+textureName);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new DMPedestalTile();
    }
}
