package moze_intel.projecte.gameObjs.container;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.slots.MatterFurnaceOutputSlot;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class DMFurnaceContainer extends PEContainer {

	public final DMFurnaceBlockEntity furnace;

	public DMFurnaceContainer(int windowId, Inventory playerInv, DMFurnaceBlockEntity furnace) {
		this(PEContainerTypes.DM_FURNACE_CONTAINER, windowId, playerInv, furnace);
	}

	protected DMFurnaceContainer(ContainerTypeRegistryObject<? extends DMFurnaceContainer> type, int windowId, Inventory playerInv, DMFurnaceBlockEntity furnace) {
		super(type, windowId, playerInv);
		this.furnace = furnace;
		initSlots();
		addDataSlot(() -> this.furnace.furnaceCookTime, value -> this.furnace.furnaceCookTime = value);
		addDataSlot(() -> this.furnace.furnaceBurnTime, value -> this.furnace.furnaceBurnTime = value);
		addDataSlot(() -> this.furnace.currentItemBurnTime, value -> this.furnace.currentItemBurnTime = value);
	}

	private void addDataSlot(IntSupplier getter, IntConsumer setter) {
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getter.getAsInt();
			}

			@Override
			public void set(int value) {
				setter.accept(value);
			}
		});
	}

	void initSlots() {
		IItemHandler fuel = furnace.getFuel();
		IItemHandler input = furnace.getInput();
		IItemHandler output = furnace.getOutput();

		//Fuel Slot
		this.addSlot(new ValidatedSlot(fuel, 0, 49, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		Predicate<ItemStack> inputPredicate = stack -> !furnace.getSmeltingResult(stack).isEmpty();
		this.addSlot(new ValidatedSlot(input, 0, 49, 17, inputPredicate));

		int counter = 1;
		//Input Storage
		for (int i = 1; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(input, counter++, 13 + i * 18, 8 + j * 18, inputPredicate));
			}
		}

		counter = output.getSlots() - 1;

		//Output
		this.addSlot(new MatterFurnaceOutputSlot(playerInv.player, output, counter--, 109, 35));

		//OutputStorage
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new MatterFurnaceOutputSlot(playerInv.player, output, counter--, 131 + i * 18, 8 + j * 18));
			}
		}

		addPlayerInventory(8, 84);
	}

	protected BlockRegistryObject<MatterFurnace, ?> getValidBlock() {
		return PEBlocks.DARK_MATTER_FURNACE;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return stillValid(player, furnace, getValidBlock());
	}
}