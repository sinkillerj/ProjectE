package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerCopySlot;
import org.jetbrains.annotations.NotNull;

public class ComponentSlotGhost extends ItemHandlerCopySlot implements ISlotGhost {

	public ComponentSlotGhost(IItemHandler inv, int slotIndex, int xPos, int yPos) {
		super(inv, slotIndex, xPos, yPos);
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		if (super.mayPlace(stack)) {
			set(stack);
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
	public int getMaxStackSize(@NotNull ItemStack stack) {
		return 1;
	}
}