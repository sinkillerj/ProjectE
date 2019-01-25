package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class GemChest extends GemArmorBase implements IFireProtector
{
    public GemChest(Builder builder)
    {
        super(EntityEquipmentSlot.CHEST, builder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flags)
    {
        tooltips.add(new TextComponentTranslation("pe.gem.chest.lorename"));
    }

    @Override
    public void onArmorTick(ItemStack chest, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            int x = (int) Math.floor(player.posX);
            int y = (int) (player.posY - player.getYOffset());
            int z = (int) Math.floor(player.posZ);
            BlockPos pos = new BlockPos(x, y, z);

            Block b = world.getBlockState(pos.down()).getBlock();

            if (b == Blocks.LAVA && world.isAirBlock(pos))
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
            player.getCapability(InternalTimers.CAPABILITY).ifPresent(timers -> {
                timers.activateFeed();
                if (player.getFoodStats().needFood() && timers.canFeed())
                {
                    player.getFoodStats().addStats(2, 10);
                }
            });
        }
    }

    public void doExplode(EntityPlayer player)
    {
        if (ProjectEConfig.difficulty.offensiveAbilities.get())
        {
            WorldHelper.createNovaExplosion(player.getEntityWorld(), player, player.posX, player.posY, player.posZ, 9.0F);
        }
    }

    @Override
    public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
    {
        return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == stack;
    }
}
