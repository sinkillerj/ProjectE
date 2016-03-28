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

                    switch (message.key)
                    {
                        case ARMOR_TOGGLE:
                            if (player.isSneaking())
                            {
                                ItemStack helm = player.inventory.armorItemInSlot(3);

                                if (helm != null && helm.getItem() == ObjHandler.gemHelmet)
                                {
                                    GemHelmet.toggleNightVision(helm, player);
                                }
                            }
                            else
                            {
                                ItemStack boots = player.inventory.armorItemInSlot(0);

                                if (boots != null && boots.getItem() == ObjHandler.gemFeet)
                                {
                                    ((GemFeet) ObjHandler.gemFeet).toggleStepAssist(boots, player);
                                }
                            }
                            break;
                        case CHARGE:
                            Pair<EnumHand, ItemStack> charge = getMainOrOff(player, IItemCharge.class);

                            if (charge != null)
                            {
                                ((IItemCharge) charge.getRight().getItem()).changeCharge(player, charge.getRight(), charge.getLeft());
                            }
                            else if (ProjectEConfig.unsafeKeyBinds)
                            {
                                if (GemArmorBase.hasAnyPiece(player))
                                {
                                    PlayerChecks.setGemState(player, !PlayerChecks.getGemState(player));
                                    player.addChatMessage(new TextComponentTranslation(PlayerChecks.getGemState(player) ? "pe.gem.activate" : "pe.gem.deactivate"));
                                }
                            }
                            break;
                        case EXTRA_FUNCTION:
                            Pair<EnumHand, ItemStack> extra = getMainOrOff(player, IExtraFunction.class);

                            if (extra != null)
                            {
                                ((IExtraFunction) extra.getRight().getItem()).doExtraFunction(extra.getRight(), player, extra.getLeft());
                            } else if (ProjectEConfig.unsafeKeyBinds)
                            {
                                if (PlayerChecks.getGemState(player) && player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() == ObjHandler.gemChest)
                                {
                                    if (PlayerChecks.getGemCooldown(player) <= 0)
                                    {
                                        ((GemChest) ObjHandler.gemChest).doExplode(player);
                                        PlayerChecks.resetGemCooldown(player);
                                    }
                                }
                            }
                            break;
                        case FIRE_PROJECTILE:
                            Pair<EnumHand, ItemStack> projectile = getMainOrOff(player, IProjectileShooter.class);

                            if (projectile != null)
                            {
                                if (PlayerChecks.getProjectileCooldown(player) <= 0) {
                                    if (((IProjectileShooter) projectile.getRight().getItem()).shootProjectile(player, projectile.getRight(), projectile.getLeft()))
                                    {
                                        PlayerHelper.swingItem((player));
                                    }
                                    PlayerChecks.resetProjectileCooldown(player);
                                }
                            } else if (ProjectEConfig.unsafeKeyBinds)
                            {
                                if (PlayerChecks.getGemState(player) && player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() == ObjHandler.gemHelmet)
                                {
                                    ((GemHelmet) ObjHandler.gemHelmet).doZap(player);
                                }
                            }
                            break;
                        case MODE:
                            Pair<EnumHand, ItemStack> modeChange = getMainOrOff(player, IModeChanger.class);
                            if (modeChange != null)
                            {
                                ((IModeChanger) modeChange.getRight().getItem()).changeMode(player, modeChange.getRight(), modeChange.getLeft());
                            }
                            break;
                    }
                }
            });
			return null;
		}

        private Pair<EnumHand, ItemStack> getMainOrOff(EntityPlayer player, Class<?> clazz)
        {
            for (EnumHand e : EnumHand.values())
            {
                ItemStack stack = player.getHeldItem(e);
                if (stack != null && clazz.isAssignableFrom(stack.getItem().getClass()))
                {
                    return Pair.of(e, stack);
                }
            }
            return null;
        }
	}
}
