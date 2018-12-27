package moze_intel.projecte.gameObjs.blocks;


import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Pedestal extends Block
{

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.75, 0.8125);

    public Pedestal(Builder builder)
    {
        super(builder /*Material.ROCK*/);
        this.setCreativeTab(ObjHandler.cTab);
        this.setHardness(1.0F);
        this.setTranslationKey("pe_dmPedestal");
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return AABB;
    }

    private void dropItem(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof DMPedestalTile)
        {
            DMPedestalTile tile = (DMPedestalTile) te;
            ItemStack stack = tile.getInventory().getStackInSlot(0);
            if (!stack.isEmpty())
            {
                tile.getInventory().setStackInSlot(0, ItemStack.EMPTY);
                EntityItem ent = new EntityItem(world, pos.getX(), pos.getY() + 0.8, pos.getZ());
                ent.setItem(stack);
                world.spawnEntity(ent);
            }
        }
    }

    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
    {
        dropItem(world, pos);
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockClicked(IBlockState state, World world, BlockPos pos, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            dropItem(world, pos);
            world.notifyBlockUpdate(pos, state, state, 8);
        }
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof DMPedestalTile))
            {
                return true;
            }

            DMPedestalTile tile = ((DMPedestalTile) te);
            ItemStack item = tile.getInventory().getStackInSlot(0);
            ItemStack stack = player.getHeldItem(hand);

            if (stack.isEmpty()
                    && !item.isEmpty()
                    && item.getItem() instanceof IPedestalItem)
            {
                tile.setActive(!tile.getActive());
                world.notifyBlockUpdate(pos, state, state, 8);
            } else if (!stack.isEmpty() && item.isEmpty())
            {
                tile.getInventory().setStackInSlot(0, stack.split(1));
                if (stack.getCount() <= 0)
                {
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }
                world.notifyBlockUpdate(pos, state, state, 8);
            }
        }
        return true;
    }

    // [VanillaCopy] Adapted from BlockNote
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos)
    {
        boolean flag = world.isBlockPowered(pos);
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof DMPedestalTile)
        {
            DMPedestalTile ped = ((DMPedestalTile) te);

            if (ped.previousRedstoneState != flag)
            {
                if (flag && !ped.getInventory().getStackInSlot(0).isEmpty()
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
    public TileEntity createTileEntity(@Nonnull IBlockState state, @Nonnull IBlockReader world) {
        return new DMPedestalTile();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flags)
    {
        tooltip.add(new TextComponentTranslation("pe.pedestal.tooltip1"));
        tooltip.add(new TextComponentTranslation("pe.pedestal.tooltip2"));
    }

}
