package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
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
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.Pair;

public class KeyPressPKT implements IMessage
{
	private PEKeybind key;

	public KeyPressPKT() {}

	public KeyPressPKT(PEKeybind key)
	{
		this.key = key;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		key = PEKeybind.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(key.ordinal());
	}

	public static class Handler implements IMessageHandler<KeyPressPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final KeyPressPKT message, final MessageContext ctx)
		{
			ctx.getServerHandler().playerEntity.mcServer.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayerMP player = ctx.getServerHandler().playerEntity;

                    if (message.key == PEKeybind.ARMOR_TOGGLE)
                    {
                        if (player.isSneaking())
                        {
                            ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                            if (helm != null && helm.getItem() == ObjHandler.gemHelmet)
                            {
                                GemHelmet.toggleNightVision(helm, player);
                            }
                        }
                        else
                        {
                            ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

                            if (boots != null && boots.getItem() == ObjHandler.gemFeet)
                            {
                                ((GemFeet) ObjHandler.gemFeet).toggleStepAssist(boots, player);
                            }
                        }
                        return;
                    }

                    for (EnumHand hand : EnumHand.values())
                    {
                        ItemStack stack = player.getHeldItem(hand);
                        if (stack != null)
                        {
                            switch (message.key)
                            {
                                case CHARGE:
                                    if (stack.getItem() instanceof IItemCharge
                                            && ((IItemCharge) stack.getItem()).changeCharge(player, stack, hand))
                                    {
                                        return;
                                    } else if (ProjectEConfig.unsafeKeyBinds)
                                    {
                                        if (GemArmorBase.hasAnyPiece(player))
                                        {
                                            PlayerChecks.setGemState(player, !PlayerChecks.getGemState(player));
                                            player.addChatMessage(new TextComponentTranslation(PlayerChecks.getGemState(player) ? "pe.gem.activate" : "pe.gem.deactivate"));
                                            return;
                                        }
                                    }
                                    break;
                                case EXTRA_FUNCTION:
                                    if (stack.getItem() instanceof IExtraFunction
                                            && ((IExtraFunction) stack.getItem()).doExtraFunction(stack, player, hand))
                                    {
                                        return;
                                    } else if (ProjectEConfig.unsafeKeyBinds)
                                    {
                                        if (PlayerChecks.getGemState(player) && player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() == ObjHandler.gemChest)
                                        {
                                            if (PlayerChecks.getGemCooldown(player) <= 0)
                                            {
                                                ((GemChest) ObjHandler.gemChest).doExplode(player);
                                                PlayerChecks.resetGemCooldown(player);
                                                return;
                                            }
                                        }
                                    }
                                    break;
                                case FIRE_PROJECTILE:
                                    if (stack.getItem() instanceof IProjectileShooter
                                            && PlayerChecks.getProjectileCooldown(player) <= 0
                                            && ((IProjectileShooter) stack.getItem()).shootProjectile(player, stack, hand))
                                    {
                                        PlayerHelper.swingItem((player));
                                        PlayerChecks.resetProjectileCooldown(player);
                                        return;
                                    } else if (ProjectEConfig.unsafeKeyBinds)
                                    {
                                        if (PlayerChecks.getGemState(player)
                                                && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null
                                                && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ObjHandler.gemHelmet)
                                        {
                                            ((GemHelmet) ObjHandler.gemHelmet).doZap(player);
                                            return;
                                        }
                                    }
                                    break;
                                case MODE:
                                    if (stack.getItem() instanceof IModeChanger
                                            && ((IModeChanger) stack.getItem()).changeMode(player, stack, hand))
                                    {
                                        return;
                                    }
                                    break;
                            }
                        }
                    }

                }
            });
			return null;
		}
	}
}
