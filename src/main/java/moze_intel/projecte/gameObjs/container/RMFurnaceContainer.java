package moze_intel.projecte.gameObjs.container;

import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.block_entities.RMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.container.slots.MatterFurnaceOutputSlot;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class RMFurnaceContainer extends DMFurnaceContainer {

	public RMFurnaceContainer(int windowId, Inventory playerInv, RMFurnaceBlockEntity furnace) {
		super(PEContainerTypes.RM_FURNACE_CONTAINER, windowId, playerInv, furnace);
	}

	@Override
	void initSlots() {
		IItemHandler fuel = furnace.getFuel();
		IItemHandler input = furnace.getInput();
		IItemHandler output = furnace.getOutput();

		//Fuel
		this.addSlot(new ValidatedSlot(fuel, 0, 65, 53, SlotPredicates.FURNACE_FUEL));

		Predicate<ItemStack> inputPredicate = stack -> !furnace.getSmeltingResult(stack).isEmpty();
		//Input(0)
		this.addSlot(new ValidatedSlot(input, 0, 65, 17, inputPredicate));

		int counter = 1;
		//Input Storage
		for (int i = 2; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(input, counter++, 11 + i * 18, 8 + j * 18, inputPredicate));
			}
		}

		counter = output.getSlots() - 1;

		//Output(0)
		this.addSlot(new MatterFurnaceOutputSlot(playerInv.player, output, counter--, 125, 35));

		//Output Storage
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new MatterFurnaceOutputSlot(playerInv.player, output, counter--, 147 + i * 18, 8 + j * 18));
			}
		}

		addPlayerInventory(24, 84);
	}
}