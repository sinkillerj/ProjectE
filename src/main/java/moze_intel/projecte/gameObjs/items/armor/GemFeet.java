package moze_intel.projecte.gameObjs.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.EnumArmorType;
import moze_intel.projecte.utils.PEKeyBind;
import moze_intel.projecte.utils.PlayerHelper;
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
        player.addChatMessage(new ChatComponentTranslation("pe.gem.stepassist_tooltip")
                .appendSibling(ChatHelper.modifyColor(new ChatComponentTranslation(s), e)));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            if (!playerMP.capabilities.allowFlying)
            {
                PlayerHelper.enableFlight(playerMP);
            }

            if (isStepAssistEnabled(stack))
            {
                if (playerMP.stepHeight != 1.0f)
                {
                    playerMP.stepHeight = 1.0f;
                    PlayerHelper.updateClientStepHeight(playerMP, 1.0F);

                    PlayerChecks.addPlayerStepChecks(playerMP);
                }
            }

            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1, 4));

            if (!player.isSneaking())
            {
                player.addPotionEffect(new PotionEffect(Potion.jump.id, 1, 4));
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
}
