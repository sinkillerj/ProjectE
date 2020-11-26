package moze_intel.projecte.utils;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PEShearsUtil {
    public static void onUseItem(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockState state = context.getWorld().getBlockState(context.getPos());
        Block block = state.getBlock();

        if (block == Blocks.PUMPKIN) {
            //LOGGER.info("Clicked Pumpkin with Shears");

            ItemStack seedItem = new ItemStack(Items.PUMPKIN_SEEDS);
            seedItem.setCount(4);

            world.removeBlock(context.getPos(), false);
            world.setBlockState(context.getPos(), Blocks.CARVED_PUMPKIN.getDefaultState());
            world.playSound(null, context.getPos(), SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1f, 1f);
            Block.spawnAsEntity(world, context.getPos(), seedItem);

            if (!player.isCreative()) {
                if (context.getItem().isDamageable()) {
                    context.getItem().setDamage(context.getItem().getDamage() + 1);
                }
            }

        } else {

            if (block == Blocks.BEEHIVE || block == Blocks.BEE_NEST) {
                if (state.get(BlockStateProperties.HONEY_LEVEL) >= 5) {
                    world.playSound(player, player.getPosition(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                    //BeehiveBlock.dropHoneyComb(world, event.getPos());  // this does exact same as .dropHoneyComb method
                    Block.spawnAsEntity(world, context.getPos(), new ItemStack(Items.HONEYCOMB, 3));

                    if (!player.isCreative()) {
                        if (context.getItem().isDamageable()) {
                            context.getItem().setDamage(context.getItem().getDamage() + 1);
                        }
                    }

                    BeehiveBlock HIVE = (BeehiveBlock) state.getBlock();
                    BeehiveTileEntity HIVE_TE = (BeehiveTileEntity) world.getTileEntity(context.getPos());

                    if (HIVE_TE != null) {
                        if (!HIVE_TE.hasNoBees()) {
                            HIVE.takeHoney(world, state, context.getPos(), player, BeehiveTileEntity.State.EMERGENCY);
                        }
                    }
                }
            }
        }
    }
}