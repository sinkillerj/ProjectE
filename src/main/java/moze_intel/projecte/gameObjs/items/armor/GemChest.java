package moze_intel.projecte.gameObjs.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.handlers.PlayerTimers;
import moze_intel.projecte.utils.EnumArmorType;
import moze_intel.projecte.utils.NovaExplosion;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class GemChest extends GemArmorBase
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

            Block b = world.getBlock(x, y - 1, z);

            if ((b == Blocks.lava || b == Blocks.flowing_lava) && world.getBlock(x, y, z).equals(Blocks.air))
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

            if (!player.isImmuneToFire())
            {
                PlayerHelper.setPlayerFireImmunity(player, true);
                PlayerChecks.addPlayerFireChecks(playerMP);
            }
        }
    }

    public static void doExplode(EntityPlayer player)
    {
        if (ProjectEConfig.gemArmorOffensiveAbilities)
        {
            NovaExplosion explosion = new NovaExplosion(player.worldObj, player, player.posX, player.posY, player.posZ, 9.0F);
            explosion.isFlaming = true;
            explosion.isSmoking = true;
            explosion.doExplosionA();
            explosion.doExplosionB(true);
        }
    }
}
