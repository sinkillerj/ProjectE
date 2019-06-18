package moze_intel.projecte.gameObjs.blocks;


import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Pedestal extends Block implements ITileEntityProvider
{
    // todo 1.13 fancify
    private static final VoxelShape SHAPE = Block.makeCuboidShape(3, 0, 3, 13, 12, 13);

    public Pedestal(Properties props)
    {
        super(props);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos)
    {
        return SHAPE;
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
                ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY() + 0.8, pos.getZ());
                ent.setItem(stack);
                world.spawnEntity(ent);
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        dropItem(world, pos);
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        if (!world.isRemote)
        {
            dropItem(world, pos);
            world.notifyBlockUpdate(pos, state, state, 8);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ)
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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos)
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
    public boolean isFullCube(BlockState state)
    {
        return false;
    }

    @Nonnull
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader world) {
        return new DMPedestalTile();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flags)
    {
        tooltip.add(new TranslationTextComponent("pe.pedestal.tooltip1"));
        tooltip.add(new TranslationTextComponent("pe.pedestal.tooltip2"));
    }

}
