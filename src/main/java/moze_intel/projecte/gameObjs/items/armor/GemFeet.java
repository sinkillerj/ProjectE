package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.collect.Multimap;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class GemFeet extends GemArmorBase implements IFlightProvider, IStepAssister
{

    private static final UUID MODIFIER = UUID.randomUUID();

    public GemFeet()
    {
        super(EntityEquipmentSlot.FEET);
    }

    public static boolean isStepAssistEnabled(ItemStack boots)
    {
        return !boots.hasTagCompound() || !boots.getTagCompound().hasKey("StepAssist") || boots.getTagCompound().getBoolean("StepAssist");

    }

    public void toggleStepAssist(ItemStack boots, EntityPlayer player)
    {
        boolean value;

        if (ItemHelper.getOrCreateCompound(boots).hasKey("StepAssist"))
        {
            boots.getTagCompound().setBoolean("StepAssist", !boots.getTagCompound().getBoolean("StepAssist"));
            value = boots.getTagCompound().getBoolean("StepAssist");
        }
        else
        {
            boots.getTagCompound().setBoolean("StepAssist", false);
            value = false;
        }

        TextFormatting e = value ? TextFormatting.GREEN : TextFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.sendMessage(new TextComponentTranslation("pe.gem.stepassist_tooltip").appendText(" ")
                .appendSibling(new TextComponentTranslation(s).setStyle(new Style().setColor(e))));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            playerMP.fallDistance = 0;
        }
        else
        {
            if (!player.capabilities.isFlying && PECore.proxy.isJumpPressed())
            {
                player.motionY += 0.1;
            }

            if (!player.onGround)
            {
                if (player.motionY <= 0)
                {
                    player.motionY *= 0.90;
                }
                if (!player.capabilities.isFlying)
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltips, ITooltipFlag flags)
    {
        tooltips.add(I18n.format("pe.gem.feet.lorename"));
        tooltips.add(I18n.format("pe.gem.stepassist.prompt", ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)));

        TextFormatting e = canStep(stack) ? TextFormatting.GREEN : TextFormatting.RED;
        String s = canStep(stack) ? "pe.gem.enabled" : "pe.gem.disabled";
        tooltips.add(I18n.format("pe.gem.stepassist_tooltip") + " "
                + e + I18n.format(s));
    }

    private boolean canStep(ItemStack stack)
    {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("StepAssist") && stack.getTagCompound().getBoolean("StepAssist");
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack)
    {
        if (slot != EntityEquipmentSlot.FEET) return super.getAttributeModifiers(slot, stack);
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(MODIFIER, "Armor modifier", 1.0, 2));
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
