package moze_intel.projecte.gameObjs.container.slots.condenser;

import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotCondenserLock extends SlotItemHandler
{
	private final CondenserContainer container;

	public SlotCondenserLock(CondenserContainer container, int slotIndex, int xPos, int yPos)
	{
		super(container.tile.getLock(), slotIndex, xPos, yPos);
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if (stack != null && EMCHelper.doesItemHaveEmc(stack) && !container.tile.getWorld().isRemote)
		{
			this.putStack(ItemHelper.getNormalizedStack(stack));
			container.detectAndSendChanges();
		}

		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
