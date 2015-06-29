package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.handlers.PlayerTimers;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.EnumArmorType;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

import java.util.List;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.nodes.IRevealer", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.IGoggles", modid = "Thaumcraft")})
public class GemHelmet extends GemArmorBase implements IGoggles, IRevealer
{
    public GemHelmet()
    {
        super(EnumArmorType.HEAD);
    }

    public static boolean isNightVisionEnabled(ItemStack helm)
    {
        return !helm.hasTagCompound() || !helm.getTagCompound().hasKey("NightVision") || helm.getTagCompound().getBoolean("NightVision");

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

        EnumChatFormatting e = value ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.addChatMessage(new ChatComponentTranslation("pe.gem.nightvision_tooltip").appendText(" ")
                .appendSibling(ChatHelper.modifyColor(new ChatComponentTranslation(s), e)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean unused)
    {
        tooltips.add(StatCollector.translateToLocal("pe.gem.helm.lorename"));

        tooltips.add(String.format(
                StatCollector.translateToLocal("pe.gem.nightvision.prompt"), ClientKeyHelper.getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSneak), ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)
        ));

        EnumChatFormatting e = isNightVisionEnabled(stack) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        String s = isNightVisionEnabled(stack) ? "pe.gem.enabled" : "pe.gem.disabled";
        tooltips.add(StatCollector.translateToLocal("pe.gem.nightvision_tooltip") + " "
                + e + StatCollector.translateToLocal(s));
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
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 220, 0));
            }
            else
            {
                player.removePotionEffect(Potion.nightVision.id);
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

    public static void doZap(EntityPlayer player)
    {
        BlockPos strikePos = PlayerHelper.getBlockLookingAt(player, 120.0F);
        if (strikePos != null)
        {
            player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, strikePos.getX(), strikePos.getY(), strikePos.getZ()));
        }
    }
}
