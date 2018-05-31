package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.item.IAlchBagItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = PECore.MODID)
public class TickEvents
{
	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			IAlchBagProvider provider = event.player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null);

			Set<EnumDyeColor> colorsChanged = EnumSet.noneOf(EnumDyeColor.class);

			for (EnumDyeColor color : getBagColorsPresent(event.player))
			{
				IItemHandler inv = provider.getBag(color);
				for (int i = 0; i < inv.getSlots(); i++)
				{
					ItemStack current = inv.getStackInSlot(i);
					if (!current.isEmpty() && current.getItem() instanceof IAlchBagItem
							&& ((IAlchBagItem) current.getItem()).updateInAlchBag(inv, event.player, current))
					{
						colorsChanged.add(color);
					}
				}
			}

			if (!event.player.getEntityWorld().isRemote)
			{
				for (EnumDyeColor e : colorsChanged)
				{
					if (event.player.openContainer instanceof AlchBagContainer
							&& event.player.getHeldItem(((AlchBagContainer) event.player.openContainer).hand).getItemDamage() == e.getMetadata())
						// Do not sync if this color is open, the container system does it for us
						// and we'll stay out of its way.
						continue;
					else provider.sync(e, (EntityPlayerMP) event.player);
				}

				event.player.getCapability(InternalAbilities.CAPABILITY, null).tick();
				event.player.getCapability(InternalTimers.CAPABILITY, null).tick();
			}
		}
	}

	private static Set<EnumDyeColor> getBagColorsPresent(EntityPlayer player)
	{
		Set<EnumDyeColor> bagsPresent = EnumSet.noneOf(EnumDyeColor.class);

		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == ObjHandler.alchBag)
			{
				bagsPresent.add(EnumDyeColor.byMetadata(stack.getItemDamage()));
			}
		}

		return bagsPresent;
	}
}
