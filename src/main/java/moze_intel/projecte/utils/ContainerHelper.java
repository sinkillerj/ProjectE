package moze_intel.projecte.utils;

import java.util.function.Consumer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class ContainerHelper {

	public static void addPlayerInventory(Consumer<Slot> addSlot, IInventory invPlayer, int xStart, int yStart) {
		int rows = 3;
		int columns = 9;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				addSlot.accept(new Slot(invPlayer, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
			}
		}
		yStart = yStart * rows + 4;
		for (int i = 0; i < columns; i++) {
			addSlot.accept(new Slot(invPlayer, i, xStart + i * 18, yStart));
		}
	}
}