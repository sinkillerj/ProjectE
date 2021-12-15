package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.ContainerHelper;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AlchChestContainer extends Container {

	private final AlchChestTile tile;

	public AlchChestContainer(int windowId, PlayerInventory invPlayer, AlchChestTile tile) {
		super(PEContainerTypes.ALCH_CHEST_CONTAINER.get(), windowId);
		this.tile = tile;
		tile.numPlayersUsing++;

		IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
		//Chest Inventory
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 13; j++) {
				this.addSlot(new SlotItemHandler(inv, j + i * 13, 12 + j * 18, 5 + i * 18));
			}
		}

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 48, 152);
	}

	public static AlchChestContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		return new AlchChestContainer(windowId, invPlayer,
				(AlchChestTile) GuiHandler.getTeFromBuf(buf));
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return player.level.getBlockState(tile.getBlockPos()).getBlock() == PEBlocks.ALCHEMICAL_CHEST.getBlock()
			   && player.distanceToSqr(tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5) <= 64.0;
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

		if (slotIndex < 104) {
			if (!this.moveItemStackTo(stack, 104, this.slots.size(), false)) {
				return ItemStack.EMPTY;
			}
		} else if (!this.moveItemStackTo(stack, 0, 104, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return newStack;
	}

	@Override
	public void removed(@Nonnull PlayerEntity player) {
		super.removed(player);
		tile.numPlayersUsing--;
	}
}