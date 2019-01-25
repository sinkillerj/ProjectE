package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

// todo 1.13 @Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft")})
public class GemHelmet extends GemArmorBase
{
    public GemHelmet(Builder builder)
    {
        super(EntityEquipmentSlot.HEAD, builder);
    }

    public static boolean isNightVisionEnabled(ItemStack helm)
    {
        return helm.hasTag() && helm.getTag().contains("NightVision") && helm.getTag().getBoolean("NightVision");
    }

    public static void toggleNightVision(ItemStack helm, EntityPlayer player)
    {
        boolean value;

        if (helm.getOrCreateTag().contains("NightVision"))
        {
            helm.getTag().putBoolean("NightVision", !helm.getTag().getBoolean("NightVision"));
            value = helm.getTag().getBoolean("NightVision");
        }
        else
        {
            helm.getTag().putBoolean("NightVision", false);
            value = false;
        }

        TextFormatting e = value ? TextFormatting.GREEN : TextFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.sendMessage(new TextComponentTranslation("pe.gem.nightvision_tooltip").appendText(" ")
                .appendSibling(new TextComponentTranslation(s).setStyle(new Style().setColor(e))));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltips, ITooltipFlag flags)
    {
        tooltips.add(new TextComponentTranslation("pe.gem.helm.lorename"));

        tooltips.add(
                new TextComponentTranslation("pe.gem.nightvision.prompt", ClientKeyHelper.getKeyName(Minecraft.getInstance().gameSettings.keyBindSneak), ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)
        ));

        TextFormatting color = isNightVisionEnabled(stack) ? TextFormatting.GREEN : TextFormatting.RED;
        TextComponentTranslation status = new TextComponentTranslation(isNightVisionEnabled(stack) ? "pe.gem.enabled" : "pe.gem.disabled");
        status.setStyle(new Style().setColor(color));
        tooltips.add(new TextComponentTranslation("pe.gem.nightvision_tooltip").appendText(" ").appendSibling(status));
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            int x = (int) Math.floor(player.posX);
            int y = (int) (player.posY - player.getYOffset());
            int z = (int) Math.floor(player.posZ);
            BlockPos pos = new BlockPos(x, y, z);
            Block b = world.getBlockState(pos.down()).getBlock();

            if (b == Blocks.WATER && world.isAirBlock(pos))
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
            player.getCapability(InternalTimers.CAPABILITY).ifPresent(handler -> {
                handler.activateHeal();
                if (player.getHealth() < player.getMaxHealth() && handler.canHeal())
                {
                    player.heal(2.0F);
                }
            });

            if (isNightVisionEnabled(stack))
            {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false));
            }
            else
            {
                player.removePotionEffect(MobEffects.NIGHT_VISION);
            }

            if (player.isInWater())
            {
                player.setAir(300);
            }
        }
    }

    /* todo 1.13
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
    */

    public void doZap(EntityPlayer player)
    {
        if (ProjectEConfig.difficulty.offensiveAbilities.get())
        {
            BlockPos strikePos = PlayerHelper.getBlockLookingAt(player, 120.0F);
            if (strikePos != null)
			{
				player.getEntityWorld().addWeatherEffect(new EntityLightningBolt(player.getEntityWorld(), strikePos.getX(), strikePos.getY(), strikePos.getZ(), false));
			}
        }
    }
}
