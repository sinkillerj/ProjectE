package moze_intel.projecte.gameObjs.container.inventory;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.IItemHandlerModifiable;

public class AlchBagInventory implements IItemHandlerModifiable
{
	private final ItemStack invItem;
	private final IItemHandlerModifiable compose;
	public final EnumHand hand;
	
	public AlchBagInventory(EntityPlayer player, ItemStack stack, EnumHand hand)
	{
		invItem = stack;
		compose = (IItemHandlerModifiable) player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null)
				.getBag(EnumDyeColor.byMetadata(stack.getMetadata()));
		this.hand = hand;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		compose.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		return compose.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return compose.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack == invItem)
			return stack; // Cannot put the bag into itself
		else return compose.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return compose.extractItem(slot, amount, simulate);
	}
}
