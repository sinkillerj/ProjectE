package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.collect.Multimap;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class GemFeet extends GemArmorBase implements IFlightProvider, IStepAssister
{

    private static final UUID MODIFIER = UUID.randomUUID();

    public GemFeet(Builder builder)
    {
        super(EntityEquipmentSlot.FEET, builder);
    }

    public static boolean isStepAssistEnabled(ItemStack boots)
    {
        return !boots.hasTag() || !boots.getTag().contains("StepAssist") || boots.getTag().getBoolean("StepAssist");

    }

    public void toggleStepAssist(ItemStack boots, EntityPlayer player)
    {
        boolean value;

        if (boots.getOrCreateTag().contains("StepAssist"))
        {
            boots.getTag().putBoolean("StepAssist", !boots.getTag().getBoolean("StepAssist"));
            value = boots.getTag().getBoolean("StepAssist");
        }
        else
        {
            boots.getTag().putBoolean("StepAssist", false);
            value = false;
        }

        TextFormatting e = value ? TextFormatting.GREEN : TextFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.sendMessage(new TextComponentTranslation("pe.gem.stepassist_tooltip").appendText(" ")
                .appendSibling(new TextComponentTranslation(s).setStyle(new Style().setColor(e))));
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            playerMP.fallDistance = 0;
        }
        else
        {
            if (!player.abilities.isFlying && PECore.proxy.isJumpPressed())
            {
                player.motionY += 0.1;
            }

            if (!player.onGround)
            {
                if (player.motionY <= 0)
                {
                    player.motionY *= 0.90;
                }
                if (!player.abilities.isFlying)
                {
                    if (player.moveForward < 0)
                    {
                        player.motionX *= 0.9;
                        player.motionZ *= 0.9;
                    } else if (player.moveForward > 0 && player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ < 3)
                    {
                        player.motionX *= 1.1;
                        player.motionZ *= 1.1;
                    }
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltips, ITooltipFlag flags)
    {
        tooltips.add(new TextComponentTranslation("pe.gem.feet.lorename"));
        tooltips.add(new TextComponentTranslation("pe.gem.stepassist.prompt", ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)));

        TextFormatting color = canStep(stack) ? TextFormatting.GREEN : TextFormatting.RED;
        TextComponentTranslation status = new TextComponentTranslation(canStep(stack) ? "pe.gem.enabled" : "pe.gem.disabled");
        status.setStyle(new Style().setColor(color));
        tooltips.add(new TextComponentTranslation("pe.gem.stepassist_tooltip").appendText(" ").appendSibling(status));
    }

    private boolean canStep(ItemStack stack)
    {
        return stack.getTag() != null && stack.getTag().contains("StepAssist") && stack.getTag().getBoolean("StepAssist");
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack)
    {
        if (slot != EntityEquipmentSlot.FEET) return super.getAttributeModifiers(slot, stack);
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(MODIFIER, "Armor modifier", 1.0, 2).setSaved(false));
        return multimap;
    }

    @Override
    public boolean canProvideFlight(ItemStack stack, EntityPlayerMP player)
    {
        return player.getItemStackFromSlot(EntityEquipmentSlot.FEET) == stack;
    }

    @Override
    public boolean canAssistStep(ItemStack stack, EntityPlayerMP player)
    {
        return player.getItemStackFromSlot(EntityEquipmentSlot.FEET) == stack
                && canStep(stack);
    }
}
