package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.block_entities.CollectorMK1BlockEntity;
import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class CollectorMK1Container extends PEContainer {

	public final CollectorMK1BlockEntity collector;
	public final DataSlot sunLevel = DataSlot.standalone();
	public final BoxedLong emc = new BoxedLong();
	private final DataSlot kleinChargeProgress = DataSlot.standalone();
	private final DataSlot fuelProgress = DataSlot.standalone();
	public final BoxedLong kleinEmc = new BoxedLong();

	public CollectorMK1Container(int windowId, Inventory playerInv, CollectorMK1BlockEntity collector) {
		this(PEContainerTypes.COLLECTOR_MK1_CONTAINER, windowId, playerInv, collector);
	}

	protected CollectorMK1Container(ContainerTypeRegistryObject<? extends CollectorMK1Container> type, int windowId, Inventory playerInv, CollectorMK1BlockEntity collector) {
		super(type, windowId, playerInv);
		this.longFields.add(emc);
		addDataSlot(sunLevel);
		addDataSlot(kleinChargeProgress);
		addDataSlot(fuelProgress);
		this.longFields.add(kleinEmc);
		this.collector = collector;
		initSlots();
	}

	void initSlots() {
		IItemHandler aux = collector.getAux();
		IItemHandler main = collector.getInput();

		//Klein Star Slot
		this.addSlot(new ValidatedSlot(aux, CollectorMK1BlockEntity.UPGRADING_SLOT, 124, 58, SlotPredicates.COLLECTOR_INV));
		int counter = 0;
		//Fuel Upgrade storage
		for (int i = 1; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(main, counter++, 20 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
			}
		}
		//Upgrade Result
		this.addSlot(new ValidatedSlot(aux, CollectorMK1BlockEntity.UPGRADE_SLOT, 124, 13, SlotPredicates.ALWAYS_FALSE));
		//Upgrade Target
		this.addSlot(new SlotGhost(aux, CollectorMK1BlockEntity.LOCK_SLOT, 153, 36, SlotPredicates.COLLECTOR_LOCK));
		addPlayerInventory(8, 84);
	}

	@Override
	public void clicked(int slotID, int button, @NotNull ClickType flag, @NotNull Player player) {
		Slot slot = tryGetSlot(slotID);
		if (slot instanceof SlotGhost && !slot.getItem().isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			super.clicked(slotID, button, flag, player);
		}
	}

	@Override
	protected void broadcastPE(boolean all) {
		emc.set(collector.getStoredEmc());
		sunLevel.set(collector.getSunLevel());
		kleinChargeProgress.set((int) (collector.getItemChargeProportion() * 8000));
		fuelProgress.set((int) (collector.getFuelProgress() * 8000));
		kleinEmc.set(collector.getItemCharge());
		super.broadcastPE(all);
	}

	protected BlockRegistryObject<Collector, ?> getValidBlock() {
		return PEBlocks.COLLECTOR;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return stillValid(player, collector, getValidBlock());
	}

	public double getKleinChargeProgress() {
		return kleinChargeProgress.get() / 8000.0;
	}

	public double getFuelProgress() {
		return fuelProgress.get() / 8000.0;
	}
}