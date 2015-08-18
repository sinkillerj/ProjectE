package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.handlers.PlayerTimers;
import moze_intel.projecte.utils.EnumArmorType;
import moze_intel.projecte.utils.NovaExplosion;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class GemChest extends GemArmorBase implements IFireProtector
{
    public GemChest()
    {
        super(EnumArmorType.CHEST);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean unused)
    {
        tooltips.add(StatCollector.translateToLocal("pe.gem.chest.lorename"));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack chest)
    {
        if (world.isRemote)
        {
            int x = (int) Math.floor(player.posX);
            int y = (int) (player.posY - player.getYOffset());
            int z = (int) Math.floor(player.posZ);
            BlockPos pos = new BlockPos(x, y, z);

            Block b = world.getBlockState(pos.down()).getBlock();

            if ((b == Blocks.lava || b == Blocks.flowing_lava) && world.isAirBlock(pos))
            {
                if (!player.isSneaking())
                {
                    player.motionY = 0.0d;
                    player.fallDistance = 0.0f;
                    player.onGround = true;
                }
            }
        }
        else
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            PlayerTimers.activateFeed(playerMP);

            if (player.getFoodStats().needFood() && PlayerTimers.canFeed(playerMP))
            {
                player.getFoodStats().addStats(2, 10);
            }
        }
    }

    public static void doExplode(EntityPlayer player)
    {
        NovaExplosion explosion = new NovaExplosion(player.worldObj, player, player.posX, player.posY, player.posZ, 9.0F, true, true);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
    }

    @Override
    public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
    {
        return player.getCurrentArmor(2) == stack;
    }
}
