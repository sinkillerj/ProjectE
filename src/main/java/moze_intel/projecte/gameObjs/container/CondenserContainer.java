package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;

public class CondenserContainer extends PEContainer {

	protected final CondenserTile tile;
	public final BoxedLong displayEmc = new BoxedLong();
	public final BoxedLong requiredEmc = new BoxedLong();

	public CondenserContainer(ContainerType<?> type, int windowId, PlayerInventory invPlayer, CondenserTile condenser) {
		super(type, windowId);
		this.longFields.add(displayEmc);
		this.longFields.add(requiredEmc);
		tile = condenser;
		tile.numPlayersUsing++;
		initSlots(invPlayer);
	}

	public static CondenserContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		return new CondenserContainer(ObjHandler.CONDENSER_CONTAINER, windowId, invPlayer, (CondenserTile) GuiHandler.getTeFromBuf(buf));
	}

	protected void initSlots(PlayerInventory invPlayer) {
		this.addSlot(new SlotCondenserLock(tile.getLock(), 0, 12, 6));

		IItemHandler handler = tile.getInput();

		int counter = 0;
		//Condenser Inventory
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 13; j++) {
				this.addSlot(new ValidatedSlot(handler, counter++, 12 + j * 18, 26 + i * 18, s -> SlotPredicates.HAS_EMC.test(s) && !tile.isStackEqualToLock(s)));
			}
		}

		//Player Inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18));
			}
		}

		//Player Hotbar
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(invPlayer, i, 48 + i * 18, 212));
		}
	}

	@Override
	public void detectAndSendChanges() {
		this.displayEmc.set(tile.displayEmc);
		this.requiredEmc.set(tile.requiredEmc);
		super.detectAndSendChanges();
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex <= 91) {
			if (!this.mergeItemStack(stack, 92, 127, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!EMCHelper.doesItemHaveEmc(stack) || !this.mergeItemStack(stack, 1, 91, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		return slot.onTake(player, stack);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player) {
		return player.world.getBlockState(tile.getPos()).getBlock() instanceof Condenser
			   && player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}

	@Override
	public void onContainerClosed(PlayerEntity player) {
		super.onContainerClosed(player);
		tile.numPlayersUsing--;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, @Nonnull ClickType flag, PlayerEntity player) {
		if (slot == 0 && (!tile.getLock().getStackInSlot(0).isEmpty() || MinecraftForge.EVENT_BUS.post(new PlayerAttemptCondenserSetEvent(player, player.inventory.getItemStack())))) {
			if (!player.getEntityWorld().isRemote) {
				tile.getLock().setStackInSlot(0, ItemStack.EMPTY);
				this.detectAndSendChanges();
			}

			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, button, flag, player);
	}

	public int getProgressScaled() {
		if (requiredEmc.get() == 0) {
			return 0;
		}
		if (displayEmc.get() >= requiredEmc.get()) {
			return Constants.MAX_CONDENSER_PROGRESS;
		}
		return (int) (Constants.MAX_CONDENSER_PROGRESS * ((double) displayEmc.get() / requiredEmc.get()));
	}
}