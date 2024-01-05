package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.block_entities.RelayMK1BlockEntity;
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
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class RelayMK1Container extends PEContainer {

	public final RelayMK1BlockEntity relay;
	private final DataSlot kleinChargeProgress = DataSlot.standalone();
	private final DataSlot inputBurnProgress = DataSlot.standalone();
	public final BoxedLong emc = new BoxedLong();

	public RelayMK1Container(int windowId, Inventory playerInv, RelayMK1BlockEntity relay) {
		this(PEContainerTypes.RELAY_MK1_CONTAINER, windowId, playerInv, relay);
	}

	protected RelayMK1Container(ContainerTypeRegistryObject<? extends RelayMK1Container> type, int windowId, Inventory playerInv, RelayMK1BlockEntity relay) {
		super(type, windowId, playerInv);
		this.longFields.add(emc);
		addDataSlot(kleinChargeProgress);
		addDataSlot(inputBurnProgress);
		this.relay = relay;
		initSlots();
	}

	void initSlots() {
		IItemHandler input = relay.getInput();
		IItemHandler output = relay.getOutput();
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
	protected void broadcastPE(boolean all) {
		emc.set(relay.getStoredEmc());
		kleinChargeProgress.set((int) (relay.getItemChargeProportion() * 8000));
		inputBurnProgress.set((int) (relay.getInputBurnProportion() * 8000));
		super.broadcastPE(all);
	}

	protected BlockRegistryObject<Relay, ?> getValidBlock() {
		return PEBlocks.RELAY;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return stillValid(player, relay, getValidBlock());
	}

	public double getKleinChargeProgress() {
		return kleinChargeProgress.get() / 8000.0;
	}

	public double getInputBurnProgress() {
		return inputBurnProgress.get() / 8000.0;
	}
}