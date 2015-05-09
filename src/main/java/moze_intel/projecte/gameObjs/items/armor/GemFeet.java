package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.collect.Multimap;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.EnumArmorType;
import moze_intel.projecte.utils.PEKeyBind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GemFeet extends GemArmorBase
{
    public GemFeet()
    {
        super(EnumArmorType.FEET);
    }

    public static boolean isStepAssistEnabled(ItemStack boots)
    {
        return !boots.hasTagCompound() || !boots.stackTagCompound.hasKey("StepAssist") || boots.stackTagCompound.getBoolean("StepAssist");

    }

    public static void toggleStepAssist(ItemStack boots, EntityPlayer player)
    {
        if (!boots.hasTagCompound())
        {
            boots.setTagCompound(new NBTTagCompound());
        }

        boolean value;

        if (boots.stackTagCompound.hasKey("StepAssist"))
        {
            boots.stackTagCompound.setBoolean("StepAssist", !boots.stackTagCompound.getBoolean("StepAssist"));
            value = boots.stackTagCompound.getBoolean("StepAssist");
        }
        else
        {
            boots.stackTagCompound.setBoolean("StepAssist", false);
            value = false;
        }

        EnumChatFormatting e = value ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.addChatMessage(new ChatComponentTranslation("pe.gem.stepassist_tooltip").appendText(" ")
                .appendSibling(ChatHelper.modifyColor(new ChatComponentTranslation(s), e)));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            playerMP.fallDistance = 0;

            if (isStepAssistEnabled(stack))
            {
                if (playerMP.stepHeight != 1.0f)
                {
                    playerMP.stepHeight = 1.0f;
                    PlayerHelper.updateClientStepHeight(playerMP, 1.0F);
                    PlayerChecks.addPlayerStepChecks(playerMP);
                }
            }

            if (!player.capabilities.allowFlying)
            {
                PlayerHelper.enableFlight(playerMP);
            }

            if (!player.isSneaking())
            {
                player.addPotionEffect(new PotionEffect(Potion.jump.id, 1, 4));
            }
        }
        else
        {
            if (!player.onGround)
            {
                if (FMLClientHandler.instance().getClient().gameSettings.keyBindJump.getIsKeyPressed() && !player.capabilities.isFlying)
                {
                    player.motionY += 0.18;
                }
                if (player.motionY <= 0)
                {
                    player.motionY *= 0.90;
                }
                if (!player.capabilities.isFlying && (player.moveStrafing > 0 || player.moveForward > 0))
                {
                    player.motionX *= 1.1;
                    player.motionZ *= 1.1;
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean unused)
    {
        tooltips.add(StatCollector.translateToLocal("pe.gem.feet.lorename"));
        int keyCode = PEKeyBind.ARMOR_TOGGLE.keyCode;
        if (keyCode >= 0 && keyCode < Keyboard.getKeyCount())
        {
            tooltips.add(String.format(
                    StatCollector.translateToLocal("pe.gem.stepassist.prompt"), Keyboard.getKeyName(keyCode)));
        }

        EnumChatFormatting e = isStepAssistEnabled(stack) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        String s = isStepAssistEnabled(stack) ? "pe.gem.enabled" : "pe.gem.disabled";
        tooltips.add(StatCollector.translateToLocal("pe.gem.stepassist_tooltip") + " "
                + e + StatCollector.translateToLocal(s));
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Armor modifier", 1.0, 2));
        return multimap;
    }
}
