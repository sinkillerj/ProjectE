package moze_intel.projecte.gameObjs.container.slots.condenser;

import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCondenserMK2Lock extends Slot
{
	private CondenserMK2Container container;

	public SlotCondenserMK2Lock(CondenserMK2Container container, int slotIndex, int xPos, int yPos)
	{
		super(container.tile, slotIndex, xPos, yPos);
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if (stack != null && EMCHelper.doesItemHaveEmc(stack) && !container.tile.getWorldObj().isRemote)
		{
			this.putStack(ItemHelper.getNormalizedStack(stack));
			container.tile.checkLockAndUpdate();
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
