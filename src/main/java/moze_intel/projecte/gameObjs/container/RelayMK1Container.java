package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.utils.ContainerHelper;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.IItemHandler;

public class RelayMK1Container extends PEContainer {

	public final RelayMK1Tile tile;
	private final IntReferenceHolder kleinChargeProgress = IntReferenceHolder.standalone();
	private final IntReferenceHolder inputBurnProgress = IntReferenceHolder.standalone();
	public final BoxedLong emc = new BoxedLong();

	public static RelayMK1Container fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		return new RelayMK1Container(windowId, invPlayer, (RelayMK1Tile) GuiHandler.getTeFromBuf(buf));
	}

	public RelayMK1Container(int windowId, PlayerInventory invPlayer, RelayMK1Tile relay) {
		this(PEContainerTypes.RELAY_MK1_CONTAINER, windowId, invPlayer, relay);
	}

	protected RelayMK1Container(ContainerTypeRegistryObject<?> type, int windowId, PlayerInventory invPlayer, RelayMK1Tile relay) {
		super(type.get(), windowId);
		this.longFields.add(emc);
		this.intFields.add(kleinChargeProgress);
		this.intFields.add(inputBurnProgress);
		this.tile = relay;
		initSlots(invPlayer);
	}

	void initSlots(PlayerInventory invPlayer) {
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Klein Star charge slot
		this.addSlot(new ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV));

		int counter = input.getSlots() - 1;
		//Main Relay inventory
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new ValidatedSlot(input, counter--, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV));
			}
		}

		//Burning slot
		this.addSlot(new ValidatedSlot(output, 0, 127, 43, SlotPredicates.EMC_HOLDER));

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 8, 95);
	}

	@Override
	public void broadcastChanges() {
		emc.set(tile.getStoredEmc());
		kleinChargeProgress.set((int) (tile.getItemChargeProportion() * 8000));
		inputBurnProgress.set((int) (tile.getInputBurnProportion() * 8000));
		super.broadcastChanges();
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack newStack = stack.copy();

		if (slotIndex < 8) {
			if (!this.moveItemStackTo(stack, 8, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
			slot.setChanged();
		} else if (!this.moveItemStackTo(stack, 0, 7, false)) {
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return slot.onTake(player, newStack);
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return player.level.getBlockState(tile.getBlockPos()).getBlock() == PEBlocks.RELAY.getBlock()
			   && player.distanceToSqr(tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5) <= 64.0;
	}

	public double getKleinChargeProgress() {
		return kleinChargeProgress.get() / 8000.0;
	}

	public double getInputBurnProgress() {
		return inputBurnProgress.get() / 8000.0;
	}
}