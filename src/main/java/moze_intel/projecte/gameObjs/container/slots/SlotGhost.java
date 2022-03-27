package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotGhost extends SlotItemHandler {

	private final Predicate<ItemStack> validator;

	public SlotGhost(IItemHandler inv, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator) {
		super(inv, slotIndex, xPos, yPos);
		this.validator = validator;
	}

	public boolean isValid(@NotNull ItemStack stack) {
		return validator.test(stack);
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		if (!stack.isEmpty() && isValid(stack)) {
			this.set(stack);
		}
		return false;
	}

	@Override
	public void set(@NotNull ItemStack stack) {
		super.set(ItemHelper.getNormalizedStack(stack));
	}

	@Override
	public boolean mayPickup(Player player) {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		return 1;
	}
}