package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.utils.ContainerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemHandlerHelper;

public class EternalDensityContainer extends Container {

	public final EternalDensityInventory inventory;

	public static EternalDensityContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer data) {
		return new EternalDensityContainer(windowId, invPlayer, new EternalDensityInventory(data.readItem()));
	}

	public EternalDensityContainer(int windowId, PlayerInventory invPlayer, EternalDensityInventory gemInv) {
		super(PEContainerTypes.ETERNAL_DENSITY_CONTAINER.get(), windowId);
		inventory = gemInv;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlot(new SlotGhost(gemInv, j + i * 3, 62 + j * 18, 26 + i * 18, SlotPredicates.HAS_EMC));
			}
		}

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 8, 93);
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = getSlot(slotIndex);
		if (slotIndex > 8) {
			ItemStack toSet = slot.getItem().copy();
			toSet.setCount(1);
			ItemHandlerHelper.insertItem(inventory, toSet, false);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack clicked(int slot, int button, @Nonnull ClickType flag, @Nonnull PlayerEntity player) {
		if (slot >= 0 && getSlot(slot).getItem() == inventory.invItem) {
			return ItemStack.EMPTY;
		}
		if (slot >= 0 && slot < PlayerInventory.getSelectionSize()) {
			inventory.setStackInSlot(slot, ItemStack.EMPTY);
		}
		return super.clicked(slot, button, flag, player);
	}

	@Override
	public boolean canDragTo(@Nonnull Slot slot) {
		return false;
	}
}