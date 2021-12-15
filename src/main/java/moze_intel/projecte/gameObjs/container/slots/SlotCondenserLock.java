package moze_intel.projecte.gameObjs.container.slots;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.gameObjs.container.CondenserContainer.BoxedItemInfo;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

//Note: We cannot just extend SlotGhost as we use this as a fake slot that doesn't even have an item handler backing it
public class SlotCondenserLock extends Slot {

	private static final IInventory emptyInventory = new Inventory(0);

	private final BoxedItemInfo boxedLockInfo;

	public SlotCondenserLock(BoxedItemInfo boxedLockInfo, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.boxedLockInfo = boxedLockInfo;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		if (!stack.isEmpty() && SlotPredicates.HAS_EMC.test(stack)) {
			this.set(ItemHelper.getNormalizedStack(stack));
		}
		return false;
	}

	@Override
	public boolean mayPickup(@Nonnull PlayerEntity player) {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(@Nonnull ItemStack stack) {
		return 1;
	}

	@Override
	@Nonnull
	public ItemStack getItem() {
		ItemInfo lockInfo = boxedLockInfo.get();
		return lockInfo == null ? ItemStack.EMPTY : lockInfo.createStack();
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
	}

	@Override
	public void onQuickCraft(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		return getItem();
	}
}