package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1Tile;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.items.IItemHandler;

public class RelayMK1Container extends PEContainer {

	public final RelayMK1Tile tile;
	private final DataSlot kleinChargeProgress = DataSlot.standalone();
	private final DataSlot inputBurnProgress = DataSlot.standalone();
	public final BoxedLong emc = new BoxedLong();

	public RelayMK1Container(int windowId, Inventory playerInv, RelayMK1Tile relay) {
		this(PEContainerTypes.RELAY_MK1_CONTAINER, windowId, playerInv, relay);
	}

	protected RelayMK1Container(ContainerTypeRegistryObject<? extends RelayMK1Container> type, int windowId, Inventory playerInv, RelayMK1Tile relay) {
		super(type, windowId, playerInv);
		this.longFields.add(emc);
		addDataSlot(kleinChargeProgress);
		addDataSlot(inputBurnProgress);
		this.tile = relay;
		initSlots();
	}

	void initSlots() {
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();
		//Klein Star charge slot
		this.addSlot(new ValidatedSlot(output, 0, 127, 43, SlotPredicates.EMC_HOLDER));
		//Burning slot
		this.addSlot(new ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV));
		int counter = 1;
		//Main Relay inventory
		for (int i = 1; i >= 0; i--) {
			for (int j = 2; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(input, counter++, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV));
			}
		}
		addPlayerInventory(8, 95);
	}

	@Override
	public void broadcastChanges() {
		emc.set(tile.getStoredEmc());
		kleinChargeProgress.set((int) (tile.getItemChargeProportion() * 8000));
		inputBurnProgress.set((int) (tile.getInputBurnProportion() * 8000));
		super.broadcastChanges();
	}

	protected BlockRegistryObject<Relay, ?> getValidBlock() {
		return PEBlocks.RELAY;
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return stillValid(player, tile, getValidBlock());
	}

	public double getKleinChargeProgress() {
		return kleinChargeProgress.get() / 8000.0;
	}

	public double getInputBurnProgress() {
		return inputBurnProgress.get() / 8000.0;
	}
}