package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class Pedestal extends Block implements ITileEntityProvider {

    public Pedestal() {
        super(Material.rock);
        this.setCreativeTab(ObjHandler.cTab);
        this.setHardness(1.0F);
        setBlockName("pe_dmPedestal");
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onRightClick(World world, EntityPlayer player, int x, int y, int z)
    {
        PELogger.logInfo("Not sneaking");
    }

    private void onShiftRightClick(World world, EntityPlayer player, int x, int y, int z)
    {
        PELogger.logInfo("Sneaking");
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

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent evt)
    {
        Block block = evt.world.getBlock(evt.x, evt.y, evt.z);
        if (evt.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && block == ObjHandler.dmPedestal)
        {
            if (evt.entityPlayer.isSneaking())
			{
                onShiftRightClick(evt.world, evt.entityPlayer, evt.x, evt.y, evt.z);
                evt.useItem = Event.Result.DENY;
            } else
			{
                onRightClick(evt.world, evt.entityPlayer, evt.x, evt.y, evt.z);
			}
        }


    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new DMPedestalTile();
    }
}
