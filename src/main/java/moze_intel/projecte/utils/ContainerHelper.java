package moze_intel.projecte.utils;

import java.util.function.Consumer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class ContainerHelper {

	public static void addPlayerInventory(Consumer<Slot> addSlot, IInventory invPlayer, int xStart, int yStart) {
		addPlayerInventory(addSlot, invPlayer, xStart, yStart, Slot::new);
	}

	public static void addPlayerInventory(Consumer<Slot> addSlot, IInventory invPlayer, int xStart, int yStart, SlotCreator slotCreator) {
		int slotSize = 18;
		int rows = 3;
		//Main Inventory
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot.accept(slotCreator.create(invPlayer, j + i * 9 + 9, xStart + j * slotSize, yStart + i * slotSize));
			}
		}
		yStart = yStart + slotSize * rows + 4;
		//Hot Bar
		for (int i = 0; i < PlayerInventory.getSelectionSize(); i++) {
			addSlot.accept(slotCreator.create(invPlayer, i, xStart + i * slotSize, yStart));
		}
	}

	@FunctionalInterface
	public interface SlotCreator {

		Slot create(IInventory inventory, int index, int x, int y);
	}
}