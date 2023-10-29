package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class EternalDensityContainer extends PEHandContainer {

	public final EternalDensityInventory inventory;

	public static EternalDensityContainer fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf data) {
		return new EternalDensityContainer(windowId, playerInv, data.readEnum(InteractionHand.class), data.readByte(), null);
	}

	public EternalDensityContainer(int windowId, Inventory playerInv, InteractionHand hand, int selected, EternalDensityInventory gemInv) {
		super(PEContainerTypes.ETERNAL_DENSITY_CONTAINER, windowId, playerInv, hand, selected);
		inventory = gemInv == null ?  new EternalDensityInventory(this.stack) : gemInv;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlot(new SlotGhost(inventory, j + i * 3, 62 + j * 18, 26 + i * 18, SlotPredicates.HAS_EMC));
			}
		}
		addPlayerInventory(8, 93);
	}

	@NotNull
	@Override
	public ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
		if (slotIndex > 8) {
			Slot slot = tryGetSlot(slotIndex);
			if (slot != null) {
				ItemHandlerHelper.insertItem(inventory, ItemHelper.getNormalizedStack(slot.getItem()), false);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void clickPostValidate(int slotIndex, int button, @NotNull ClickType flag, @NotNull Player player) {
		Slot slot = tryGetSlot(slotIndex);
		if (slot instanceof SlotGhost && !slot.getItem().isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			super.clickPostValidate(slotIndex, button, flag, player);
		}
	}

	@Override
	public boolean canDragTo(@NotNull Slot slot) {
		return false;
	}
}