package moze_intel.projecte.gameObjs.blocks;


import moze_intel.projecte.PECore;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncPedestalPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Pedestal extends Block implements ITileEntityProvider {

    public Pedestal() {
        super(Material.rock);
        this.setCreativeTab(ObjHandler.cTab);
        this.setHardness(1.0F);
        setBlockName("pe_dmPedestal");
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
        if (tile.getItemStack() != null)
        {
            Utils.spawnEntityItem(world, tile.getItemStack().copy(), x, y, z);
        }
        tile.invalidate();
        super.breakBlock(world, x, y, z, block, meta);
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
            if (player.isSneaking())
            {
                player.openGui(PECore.instance, Constants.DMPEDESTAL_GUI, world, x, y, z);
            }
            else
            {
                if (tile.getItemStack().getItem() instanceof IPedestalItem)
                {
                    tile.isActive = !tile.isActive;
                }
                PELogger.logDebug("Pedestal: " + (tile.isActive ? "ON" : "OFF"));
            }
            PacketHandler.sendTo(new ClientSyncPedestalPKT(tile), ((EntityPlayerMP) player));
        }
        return true;
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
        return Constants.PEDESTAL_RENDER_ID;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return 12;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new DMPedestalTile();
    }
}
