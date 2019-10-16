package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.potion.Effects;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

// todo 1.13 @Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft")})
public class GemHelmet extends GemArmorBase
{
    public GemHelmet(Properties props)
    {
        super(EquipmentSlotType.HEAD, props);
    }

    public static boolean isNightVisionEnabled(ItemStack helm)
    {
        return helm.hasTag() && helm.getTag().contains("NightVision") && helm.getTag().getBoolean("NightVision");
    }

    public static void toggleNightVision(ItemStack helm, PlayerEntity player)
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
        player.sendMessage(new TranslationTextComponent("pe.gem.nightvision_tooltip").appendText(" ")
                .appendSibling(new TranslationTextComponent(s).setStyle(new Style().setColor(e))));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltips, ITooltipFlag flags)
    {
        tooltips.add(new TranslationTextComponent("pe.gem.helm.lorename"));

        tooltips.add(
                new TranslationTextComponent("pe.gem.nightvision.prompt", Minecraft.getInstance().gameSettings.keyBindSneak.getTranslationKey(), ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)
        ));

        TextFormatting color = isNightVisionEnabled(stack) ? TextFormatting.GREEN : TextFormatting.RED;
        TranslationTextComponent status = new TranslationTextComponent(isNightVisionEnabled(stack) ? "pe.gem.enabled" : "pe.gem.disabled");
        status.setStyle(new Style().setColor(color));
        tooltips.add(new TranslationTextComponent("pe.gem.nightvision_tooltip").appendText(" ").appendSibling(status));
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
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
                    player.setMotion(player.getMotion().mul(1, 0, 1));
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
                player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, true, false));
            }
            else
            {
                player.removePotionEffect(Effects.NIGHT_VISION);
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

    public void doZap(PlayerEntity player)
    {
        if (ProjectEConfig.difficulty.offensiveAbilities.get())
        {
            BlockPos strikePos = PlayerHelper.getBlockLookingAt(player, 120.0F);
            if (strikePos != null)
			{
                ((ServerWorld) player.getEntityWorld()).addLightningBolt(new LightningBoltEntity(player.getEntityWorld(), strikePos.getX(), strikePos.getY(), strikePos.getZ(), false));
			}
        }
    }
}
