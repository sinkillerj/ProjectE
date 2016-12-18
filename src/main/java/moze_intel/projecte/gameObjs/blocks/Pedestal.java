package moze_intel.projecte.gameObjs.blocks;


import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class Pedestal extends Block
{

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.75, 0.8125);

    public Pedestal()
    {
        super(Material.ROCK);
        this.setCreativeTab(ObjHandler.cTab);
        this.setHardness(1.0F);
        this.setUnlocalizedName("pe_dmPedestal");
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return AABB;
    }

    private void dropItem(World world, BlockPos pos)
    {
        DMPedestalTile tile = (DMPedestalTile) world.getTileEntity(pos);
        ItemStack stack = tile.getInventory().getStackInSlot(0);
        if (stack != null)
        {
            WorldHelper.spawnEntityItem(world, stack, pos.getX(), pos.getY() + 0.8, pos.getZ());
            tile.getInventory().setStackInSlot(0, null);
        }
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        dropItem(world, pos);
        super.breakBlock(world, pos, state);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            dropItem(world, pos);
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 8);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
            ItemStack item = tile.getInventory().getStackInSlot(0);

            if (stack == null
                    && item != null
                    && item.getItem() instanceof IPedestalItem)
            {
                tile.setActive(!tile.getActive());
                world.notifyBlockUpdate(pos, state, state, 8);
            } else if (stack != null && item == null)
            {
                tile.getInventory().setStackInSlot(0, stack.splitStack(1));
                if (stack.stackSize <= 0)
                {
                    player.setHeldItem(hand, null);
                }
                world.notifyBlockUpdate(pos, state, state, 8);
            }
        }
        return true;
    }

    // [VanillaCopy] Adapted from BlockNote
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbor)
    {
        boolean flag = world.isBlockPowered(pos);
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof DMPedestalTile)
        {
            DMPedestalTile ped = ((DMPedestalTile) te);

            if (ped.previousRedstoneState != flag)
            {
                if (flag && ped.getInventory().getStackInSlot(0) != null
                        && ped.getInventory().getStackInSlot(0).getItem() instanceof IPedestalItem)
                {
                    ped.setActive(!ped.getActive());
                    world.notifyBlockUpdate(pos, state, state, 11);
                }

                ped.previousRedstoneState = flag;
            }
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
    public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos)
    {
        return 12;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new DMPedestalTile();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("pe.pedestal.tooltip1"));
        tooltip.add(I18n.format("pe.pedestal.tooltip2"));
    }

}
