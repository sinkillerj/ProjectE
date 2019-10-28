package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotGhost extends SlotItemHandler {

	private final Predicate<ItemStack> validator;

	public SlotGhost(IItemHandler inv, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator) {
		super(inv, slotIndex, xPos, yPos);
		this.validator = validator;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		if (!stack.isEmpty() && validator.test(stack)) {
			this.putStack(ItemHelper.getNormalizedStack(stack));
		}
		return false;
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}