package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.base.Predicates;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GemLegs extends GemArmorBase
{
    public GemLegs(Properties props)
    {
        super(EquipmentSlotType.LEGS, props);
        MinecraftForge.EVENT_BUS.addListener(this::onJump);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag advanced)
    {
        list.add(new TranslationTextComponent("pe.gem.legs.lorename"));
    }

    private final Map<Integer, Long> lastJumpTracker = new HashMap<>();

    private void onJump(LivingEvent.LivingJumpEvent evt)
    {
        if (evt.getEntityLiving() instanceof PlayerEntity && evt.getEntityLiving().getEntityWorld().isRemote)
        {
            lastJumpTracker.put(evt.getEntityLiving().getEntityId(), evt.getEntityLiving().getEntityWorld().getGameTime());
        }
    }

    private boolean jumpedRecently(PlayerEntity player)
    {
        return lastJumpTracker.containsKey(player.getEntityId())
            && player.getEntityWorld().getGameTime() - lastJumpTracker.get(player.getEntityId()) < 5;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
    {
        if (world.isRemote)
        {
            if (player.isSneaking() && !player.onGround && player.getMotion().getY() > -8 && !jumpedRecently(player))
            {
                player.setMotion(player.getMotion().add(0, -0.32F, 0));
            }
        }

        if (player.isSneaking())
        {
            AxisAlignedBB box = new AxisAlignedBB(player.posX - 3.5, player.posY - 3.5, player.posZ - 3.5, player.posX + 3.5, player.posY + 3.5, player.posZ + 3.5);
            WorldHelper.repelEntitiesInAABBFromPoint(world, box, player.posX, player.posY, player.posZ, true);

            if (!world.isRemote && player.getMotion().getY() < -0.08)
            {
                List<Entity> entities = player.getEntityWorld().getEntitiesInAABBexcluding(player,
                        player.getBoundingBox().offset(player.getMotion()).grow(2.0D),
                        Predicates.instanceOf(LivingEntity.class));

                for (Entity e : entities)
                {
                    if (e.canBeCollidedWith())
                    {
                        e.attackEntityFrom(DamageSource.causePlayerDamage(player), (float) -player.getMotion().getY() * 6F);
                    }
                }
            }
        }
    }
}