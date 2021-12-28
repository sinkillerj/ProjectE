package moze_intel.projecte.gameObjs.container;

import java.math.BigInteger;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotConsume;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotInput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotLock;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotUnlearn;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_server.SearchUpdatePKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TransmutationContainer extends PEHandContainer {

	public final TransmutationInventory transmutationInventory;
	private SlotUnlearn unlearn;

	public static TransmutationContainer fromNetwork(int windowId, Inventory invPlayer, FriendlyByteBuf buf) {
		if (buf.readBoolean()) {
			return new TransmutationContainer(windowId, invPlayer, buf.readEnum(InteractionHand.class), buf.readByte());
		}
		return new TransmutationContainer(windowId, invPlayer);
	}

	public TransmutationContainer(int windowId, Inventory invPlayer) {
		super(PEContainerTypes.TRANSMUTATION_CONTAINER, windowId, null, 0);
		//Hand is technically null safe
		this.transmutationInventory = new TransmutationInventory(invPlayer.player);
		initSlots(invPlayer);
	}

	public TransmutationContainer(int windowId, Inventory invPlayer, InteractionHand hand, int selected) {
		super(PEContainerTypes.TRANSMUTATION_CONTAINER, windowId, hand, selected);
		this.transmutationInventory = new TransmutationInventory(invPlayer.player);
		initSlots(invPlayer);
	}

	private void initSlots(Inventory invPlayer) {
		// Transmutation Inventory
		this.addSlot(new SlotInput(transmutationInventory, 0, 43, 23));
		this.addSlot(new SlotInput(transmutationInventory, 1, 34, 41));
		this.addSlot(new SlotInput(transmutationInventory, 2, 52, 41));
		this.addSlot(new SlotInput(transmutationInventory, 3, 16, 50));
		this.addSlot(new SlotInput(transmutationInventory, 4, 70, 50));
		this.addSlot(new SlotInput(transmutationInventory, 5, 34, 59));
		this.addSlot(new SlotInput(transmutationInventory, 6, 52, 59));
		this.addSlot(new SlotInput(transmutationInventory, 7, 43, 77));
		this.addSlot(new SlotLock(transmutationInventory, 8, 158, 50));
		this.addSlot(new SlotConsume(transmutationInventory, 9, 107, 97));
		this.addSlot(unlearn = new SlotUnlearn(transmutationInventory, 10, 89, 97));
		this.addSlot(new SlotOutput(transmutationInventory, 11, 158, 9));
		this.addSlot(new SlotOutput(transmutationInventory, 12, 176, 13));
		this.addSlot(new SlotOutput(transmutationInventory, 13, 193, 30));
		this.addSlot(new SlotOutput(transmutationInventory, 14, 199, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 15, 193, 70));
		this.addSlot(new SlotOutput(transmutationInventory, 16, 176, 87));
		this.addSlot(new SlotOutput(transmutationInventory, 17, 158, 91));
		this.addSlot(new SlotOutput(transmutationInventory, 18, 140, 87));
		this.addSlot(new SlotOutput(transmutationInventory, 19, 123, 70));
		this.addSlot(new SlotOutput(transmutationInventory, 20, 116, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 21, 123, 30));
		this.addSlot(new SlotOutput(transmutationInventory, 22, 140, 13));
		this.addSlot(new SlotOutput(transmutationInventory, 23, 158, 31));
		this.addSlot(new SlotOutput(transmutationInventory, 24, 177, 50));
		this.addSlot(new SlotOutput(transmutationInventory, 25, 158, 69));
		this.addSlot(new SlotOutput(transmutationInventory, 26, 139, 50));
		addPlayerInventory(invPlayer, 35, 117);
	}

	@Override
	public void removed(@Nonnull Player player) {
		super.removed(player);
		if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
			player.drop(unlearn.getItem(), false);
		} else {
			player.getInventory().placeItemBackInInventory(unlearn.getItem());
		}
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull Player player, int slotIndex) {
		if (slotIndex < 9 || slotIndex == 10) {
			//Input Slots, lock slot, and unlearn slot, defer to super (allow basic sneak clicking out of container)
			return super.quickMoveStack(player, slotIndex);
		}
		Slot slot = this.tryGetSlot(slotIndex);
		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		if (slotIndex >= 11 && slotIndex <= 26) {
			ItemStack stack = slot.getItem().copy();
			// Output Slots
			long itemEmc = EMCHelper.getEmcValue(stack);
			//Double-check the item actually has Emc and something didn't just go terribly wrong
			if (itemEmc > 0) {
				//Note: We can just set the size here as newStack is a copy stack used for modifications
				stack.setCount(stack.getMaxStackSize());
				//Check how much we can fit of the stack
				int stackSize = stack.getCount() - ItemHelper.simulateFit(player.getInventory().items, stack);
				if (stackSize > 0) {
					BigInteger availableEMC = transmutationInventory.getAvailableEmc();
					BigInteger emc = BigInteger.valueOf(itemEmc);
					BigInteger totalEmc = emc.multiply(BigInteger.valueOf(stackSize));
					if (totalEmc.compareTo(availableEMC) > 0) {
						//We need more EMC than we have available, so we have to calculate how much we actually can produce
						//Note: We first multiply then compare, as the larger the numbers are the less efficient division becomes
						BigInteger numOperations = availableEMC.divide(emc);
						//Note: Uses intValueExact as we already compared to a multiplication of an int times the number we divided by,
						// so it should fit into an int
						stackSize = numOperations.intValueExact();
						totalEmc = emc.multiply(numOperations);
						if (stackSize <= 0) {
							return ItemStack.EMPTY;
						}
					}
					//Set the stack size to what we found the max value is we have room for (capped at the stack's own max size)
					stack.setCount(stackSize);
					IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
					if (transmutationInventory.isServer()) {
						transmutationInventory.removeEmc(totalEmc);
					}
					ItemHandlerHelper.insertItemStacked(inv, stack, false);
				}
			}
		} else if (slotIndex > 26) {
			ItemStack stack = slot.getItem().copy();
			long emc = EMCHelper.getEmcSellValue(stack);
			if (emc == 0 && !(stack.getItem() instanceof Tome)) {
				return ItemStack.EMPTY;
			}
			if (transmutationInventory.isServer()) {
				BigInteger emcBigInt = BigInteger.valueOf(emc);
				transmutationInventory.handleKnowledge(stack);
				transmutationInventory.addEmc(emcBigInt.multiply(BigInteger.valueOf(stack.getCount())));
			}
			slot.set(ItemStack.EMPTY);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void clickPostValidate(int slotIndex, int dragType, @Nonnull ClickType clickType, @Nonnull Player player) {
		if (player.getCommandSenderWorld().isClientSide && transmutationInventory.getHandlerForSlot(slotIndex) == transmutationInventory.outputs) {
			Slot slot = tryGetSlot(slotIndex);
			if (slot != null) {
				PacketHandler.sendToServer(new SearchUpdatePKT(transmutationInventory.getIndexFromSlot(slotIndex), slot.getItem()));
			}
		}
		super.clickPostValidate(slotIndex, dragType, clickType, player);
	}

	@Override
	public boolean canDragTo(@Nonnull Slot slot) {
		return !(slot instanceof SlotConsume || slot instanceof SlotUnlearn || slot instanceof SlotInput || slot instanceof SlotLock || slot instanceof SlotOutput);
	}
}