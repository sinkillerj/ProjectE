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

	public boolean isValid(@Nonnull ItemStack stack) {
		return validator.test(stack);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		if (!stack.isEmpty() && isValid(stack)) {
			this.set(stack);
		}
		return false;
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
		super.set(ItemHelper.getNormalizedStack(stack));
	}

	@Override
	public boolean mayPickup(PlayerEntity player) {
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
}