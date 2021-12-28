package moze_intel.projecte.gameObjs.container;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.slots.MatterFurnaceOutputSlot;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceTile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.items.IItemHandler;

public class DMFurnaceContainer extends PEContainer {

	public final DMFurnaceTile tile;

	protected DMFurnaceContainer(ContainerTypeRegistryObject<? extends DMFurnaceContainer> type, int windowId, Inventory invPlayer, DMFurnaceTile tile) {
		super(type, windowId);
		this.tile = tile;
		initSlots(invPlayer);
		addDataSlot(() -> this.tile.furnaceCookTime, value -> this.tile.furnaceCookTime = value);
		addDataSlot(() -> this.tile.furnaceBurnTime, value -> this.tile.furnaceBurnTime = value);
		addDataSlot(() -> this.tile.currentItemBurnTime, value -> this.tile.currentItemBurnTime = value);
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

	public DMFurnaceContainer(int windowId, Inventory invPlayer, DMFurnaceTile tile) {
		this(PEContainerTypes.DM_FURNACE_CONTAINER, windowId, invPlayer, tile);
	}

	void initSlots(Inventory invPlayer) {
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel Slot
		this.addSlot(new ValidatedSlot(fuel, 0, 49, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		Predicate<ItemStack> inputPredicate = stack -> !tile.getSmeltingResult(stack).isEmpty();
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
		this.addSlot(new MatterFurnaceOutputSlot(invPlayer.player, output, counter--, 109, 35));

		//OutputStorage
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new MatterFurnaceOutputSlot(invPlayer.player, output, counter--, 131 + i * 18, 8 + j * 18));
			}
		}

		addPlayerInventory(invPlayer, 8, 84);
	}

	protected BlockRegistryObject<MatterFurnace, ?> getValidBlock() {
		return PEBlocks.DARK_MATTER_FURNACE;
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return stillValid(player, tile, getValidBlock());
	}
}