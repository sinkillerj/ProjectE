package moze_intel.projecte.gameObjs.blocks;


import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SyncPedestalPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class Pedestal extends Block
{

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.75, 0.8125);

    public Pedestal()
    {
        super(Material.rock);
        this.setCreativeTab(ObjHandler.cTab);
        this.setHardness(1.0F);
        this.setUnlocalizedName("pe_dmPedestal");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return AABB;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
        if (tile.getInventory().getStackInSlot(0) != null)
        {
            WorldHelper.spawnEntityItem(world, tile.getInventory().getStackInSlot(0).copy(), pos);
        }
        tile.invalidate();
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
            if (player.isSneaking())
            {
                player.openGui(PECore.instance, Constants.PEDESTAL_GUI, world, pos.getX(), pos.getY(), pos.getZ());
            }
            else
            {
                if (tile.getInventory().getStackInSlot(0) != null && tile.getInventory().getStackInSlot(0).getItem() instanceof IPedestalItem)
                {
                    tile.setActive(!tile.getActive());
                }
                PELogger.logDebug("Pedestal: " + (tile.getActive() ? "ON" : "OFF"));
            }
            PacketHandler.sendToAllAround(new SyncPedestalPKT(tile), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32));
        }
        return true;
    }

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase ent, ItemStack stack)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("ProjectEBlock") && tile instanceof TileEmc)
		{
			stack.getTagCompound().setInteger("x", pos.getX());
			stack.getTagCompound().setInteger("y", pos.getY());
			stack.getTagCompound().setInteger("z", pos.getZ());

			tile.readFromNBT(stack.getTagCompound());
		}
	}

	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return 12;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DMPedestalTile();
    }
}
