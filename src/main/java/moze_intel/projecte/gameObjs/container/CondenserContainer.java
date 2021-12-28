package moze_intel.projecte.gameObjs.container;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.gameObjs.block_entities.CondenserTile;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.network.packets.to_client.UpdateCondenserLockPKT;
import moze_intel.projecte.utils.Constants;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class CondenserContainer extends ChestTileEmcContainer<CondenserTile> {

	public final BoxedLong displayEmc = new BoxedLong();
	public final BoxedLong requiredEmc = new BoxedLong();
	@Nullable
	private ItemInfo lastLockInfo;

	public CondenserContainer(int windowId, Inventory playerInv, CondenserTile condenser) {
		this(PEContainerTypes.CONDENSER_CONTAINER, windowId, playerInv, condenser);
	}

	protected CondenserContainer(ContainerTypeRegistryObject<? extends CondenserContainer> type, int windowId, Inventory playerInv, CondenserTile condenser) {
		super(type, windowId, playerInv, condenser);
		this.longFields.add(displayEmc);
		this.longFields.add(requiredEmc);
		initSlots();
	}

	protected void initSlots() {
		this.addSlot(new SlotCondenserLock(tile::getLockInfo, 0, 12, 6));
		Predicate<ItemStack> validator = s -> SlotPredicates.HAS_EMC.test(s) && !tile.isStackEqualToLock(s);
		IItemHandler handler = tile.getInput();
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 13; j++) {
				this.addSlot(new ValidatedSlot(handler,  j + i * 13, 12 + j * 18, 26 + i * 18, validator));
			}
		}
		addPlayerInventory(48, 154);
	}

	@Override
	public void broadcastChanges() {
		this.displayEmc.set(tile.displayEmc);
		this.requiredEmc.set(tile.requiredEmc);
		ItemInfo lockInfo = tile.getLockInfo();
		if (!Objects.equals(lockInfo, lastLockInfo)) {
			lastLockInfo = lockInfo;
			syncDataChange(new UpdateCondenserLockPKT((short) containerId, lockInfo));
		}
		super.broadcastChanges();
	}

	protected BlockRegistryObject<? extends Condenser, ?> getValidBlock() {
		return PEBlocks.CONDENSER;
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return stillValid(player, tile, getValidBlock());
	}

	@Override
	public void clicked(int slot, int button, @Nonnull ClickType flag, @Nonnull Player player) {
		if (slot == 0) {
			if (tile.attemptCondenserSet(player)) {
				this.broadcastChanges();
			}
		} else {
			super.clicked(slot, button, flag, player);
		}
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

	public void updateLockInfo(@Nullable ItemInfo lockInfo) {
		tile.setLockInfoFromPacket(lockInfo);
	}
}