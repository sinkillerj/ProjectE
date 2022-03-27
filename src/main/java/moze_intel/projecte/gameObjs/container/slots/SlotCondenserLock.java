package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Supplier;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//Note: We cannot just extend SlotGhost as we use this as a fake slot that doesn't even have an item handler backing it
public class SlotCondenserLock extends Slot {

	private static final Container emptyInventory = new SimpleContainer(0);

	private final Supplier<ItemInfo> lockInfo;

	public SlotCondenserLock(Supplier<ItemInfo> lockInfo, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.lockInfo = lockInfo;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		if (!stack.isEmpty() && SlotPredicates.HAS_EMC.test(stack)) {
			this.set(ItemHelper.getNormalizedStack(stack));
		}
		return false;
	}

	@Override
	public boolean mayPickup(@NotNull Player player) {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	@NotNull
	public ItemStack getItem() {
		ItemInfo lockInfo = this.lockInfo.get();
		return lockInfo == null ? ItemStack.EMPTY : lockInfo.createStack();
	}

	@Override
	public void set(@NotNull ItemStack stack) {
	}

	@Override
	public void onQuickCraft(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
	}

	@Override
	@NotNull
	public ItemStack remove(int amount) {
		return getItem();
	}
}