package moze_intel.projecte.network.packets;

import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemArmorBase;
import moze_intel.projecte.gameObjs.items.armor.GemChest;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.gameObjs.items.armor.GemHelmet;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyPressPKT {
	private final PEKeybind key;

	public KeyPressPKT(PEKeybind key)
	{
		this.key = key;
	}

    public static void encode(KeyPressPKT pkt, PacketBuffer buf)
    {
        buf.writeVarInt(pkt.key.ordinal());
    }

    public static KeyPressPKT decode(PacketBuffer buf)
    {
        return new KeyPressPKT(PEKeybind.values()[buf.readVarInt()]);
    }

	public static class Handler
	{
		public static void handle(final KeyPressPKT message, Supplier<NetworkEvent.Context> ctx)
		{
		    ctx.get().enqueueWork(() -> {
                EntityPlayerMP player = ctx.get().getSender();
                InternalAbilities internalAbilities = player.getCapability(InternalAbilities.CAPABILITY).orElseThrow(NullPointerException::new);

                if (message.key == PEKeybind.ARMOR_TOGGLE)
                {
                    if (player.isSneaking())
                    {
                        ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                        if (!helm.isEmpty() && helm.getItem() == ObjHandler.gemHelmet)
                        {
                            GemHelmet.toggleNightVision(helm, player);
                        }
                    }
                    else
                    {
                        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

                        if (!boots.isEmpty() && boots.getItem() == ObjHandler.gemFeet)
                        {
                            ((GemFeet) ObjHandler.gemFeet).toggleStepAssist(boots, player);
                        }
                    }
                    return;
                }

                for (EnumHand hand : EnumHand.values())
                {
                    ItemStack stack = player.getHeldItem(hand);
                    switch (message.key)
                    {
                        case CHARGE:
                            if (!stack.isEmpty()
                                    && stack.getItem() instanceof IItemCharge
                                    && ((IItemCharge) stack.getItem()).changeCharge(player, stack, hand))
                            {
                                return;
                            } else if (hand == EnumHand.MAIN_HAND && (ProjectEConfig.misc.unsafeKeyBinds || stack.isEmpty()))
                            {
                                if (GemArmorBase.hasAnyPiece(player))
                                {
                                    internalAbilities.setGemState(!internalAbilities.getGemState());
                                    player.sendMessage(new TextComponentTranslation(internalAbilities.getGemState() ? "pe.gem.activate" : "pe.gem.deactivate"));
                                    return;
                                }
                            }
                            break;
                        case EXTRA_FUNCTION:
                            if (!stack.isEmpty()
                                    && stack.getItem() instanceof IExtraFunction
                                    && ((IExtraFunction) stack.getItem()).doExtraFunction(stack, player, hand))
                            {
                                return;
                            } else if (hand == EnumHand.MAIN_HAND && (ProjectEConfig.misc.unsafeKeyBinds || stack.isEmpty()))
                            {
                                if (internalAbilities.getGemState()
                                        && !player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()
                                        && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ObjHandler.gemChest)
                                {
                                    if (internalAbilities.getGemCooldown() <= 0)
                                    {
                                        ((GemChest) ObjHandler.gemChest).doExplode(player);
                                        internalAbilities.resetGemCooldown();
                                        return;
                                    }
                                }
                            }
                            break;
                        case FIRE_PROJECTILE:
                            if (!stack.isEmpty()
                                    && stack.getItem() instanceof IProjectileShooter
                                    && internalAbilities.getProjectileCooldown() <= 0
                                    && ((IProjectileShooter) stack.getItem()).shootProjectile(player, stack, hand))
                            {
                                PlayerHelper.swingItem(player, hand);
                                internalAbilities.resetProjectileCooldown();
                                return;
                            } else if (hand == EnumHand.MAIN_HAND && (ProjectEConfig.misc.unsafeKeyBinds || stack.isEmpty()))
                            {
                                if (internalAbilities.getGemState()
                                        && !player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()
                                        && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ObjHandler.gemHelmet)
                                {
                                    ((GemHelmet) ObjHandler.gemHelmet).doZap(player);
                                    return;
                                }
                            }
                            break;
                        case MODE:
                            if (!stack.isEmpty()
                                    && stack.getItem() instanceof IModeChanger
                                    && ((IModeChanger) stack.getItem()).changeMode(player, stack, hand))
                            {
                                return;
                            }
                            break;
                    }

                }

            });
		}
	}
}
