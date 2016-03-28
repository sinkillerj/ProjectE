package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.PlayerTimers;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;

import java.util.List;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft")})
public class GemHelmet extends GemArmorBase implements IGoggles, IRevealer
{
    public GemHelmet()
    {
        super(EntityEquipmentSlot.HEAD);
    }

    public static boolean isNightVisionEnabled(ItemStack helm)
    {
        return helm.hasTagCompound() && helm.getTagCompound().hasKey("NightVision") && helm.getTagCompound().getBoolean("NightVision");
    }

    public static void toggleNightVision(ItemStack helm, EntityPlayer player)
    {
        if (!helm.hasTagCompound())
        {
            helm.setTagCompound(new NBTTagCompound());
        }

        boolean value;

        if (helm.getTagCompound().hasKey("NightVision"))
        {
            helm.getTagCompound().setBoolean("NightVision", !helm.getTagCompound().getBoolean("NightVision"));
            value = helm.getTagCompound().getBoolean("NightVision");
        }
        else
        {
            helm.getTagCompound().setBoolean("NightVision", false);
            value = false;
        }

        TextFormatting e = value ? TextFormatting.GREEN : TextFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.addChatMessage(new TextComponentTranslation("pe.gem.nightvision_tooltip").appendText(" ")
                .appendSibling(ChatHelper.modifyColor(new TextComponentTranslation(s), e)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean unused)
    {
        tooltips.add(I18n.translateToLocal("pe.gem.helm.lorename"));

        tooltips.add(String.format(
                I18n.translateToLocal("pe.gem.nightvision.prompt"), ClientKeyHelper.getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSneak), ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)
        ));

        TextFormatting e = isNightVisionEnabled(stack) ? TextFormatting.GREEN : TextFormatting.RED;
        String s = isNightVisionEnabled(stack) ? "pe.gem.enabled" : "pe.gem.disabled";
        tooltips.add(I18n.translateToLocal("pe.gem.nightvision_tooltip") + " "
                + e + I18n.translateToLocal(s));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if (world.isRemote)
        {
            int x = (int) Math.floor(player.posX);
            int y = (int) (player.posY - player.getYOffset());
            int z = (int) Math.floor(player.posZ);
            BlockPos pos = new BlockPos(x, y, z);
            Block b = world.getBlockState(pos.down()).getBlock();

            if ((b == Blocks.water || b == Blocks.flowing_water) && world.isAirBlock(pos))
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
            PlayerTimers.activateHeal((player));

            if (player.getHealth() < player.getMaxHealth() && PlayerTimers.canHeal((player)))
            {
                player.heal(2.0F);
            }

            if (isNightVisionEnabled(stack))
            {
                player.addPotionEffect(new PotionEffect(MobEffects.nightVision, 220, 0));
            }
            else
            {
                player.removePotionEffect(MobEffects.nightVision);
            }

            if (player.isInWater())
            {
                player.setAir(300);
            }
        }
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player)
    {
        return true;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean showNodes(ItemStack stack, EntityLivingBase player)
    {
        return true;
    }

    public void doZap(EntityPlayer player)
    {
        if (ProjectEConfig.offensiveAbilities)
        {
            BlockPos strikePos = PlayerHelper.getBlockLookingAt(player, 120.0F);
            if (strikePos != null)
			{
				player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, strikePos.getX(), strikePos.getY(), strikePos.getZ(), false));
			}
        }
    }
}
