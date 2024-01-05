package moze_intel.projecte.gameObjs.container;

import java.util.Objects;
import moze_intel.projecte.gameObjs.block_entities.AlchBlockEntityChest;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class AlchChestContainer extends EmcChestBlockEntityContainer<AlchBlockEntityChest> {

	public AlchChestContainer(int windowId, Inventory playerInv, AlchBlockEntityChest chest) {
		super(PEContainerTypes.ALCH_CHEST_CONTAINER, windowId, playerInv, chest);
		//TODO - 1.20.4: Re-evaluate this
		IItemHandler inv = Objects.requireNonNull(this.blockEntity.getLevel().getCapability(ItemHandler.BLOCK, this.blockEntity.getBlockPos(),
				null, this.blockEntity, null));
		//Chest Inventory
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 13; j++) {
				this.addSlot(new InventoryContainerSlot(inv, j + i * 13, 12 + j * 18, 5 + i * 18));
			}
		}
		addPlayerInventory(48, 152);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return stillValid(player, blockEntity, PEBlocks.ALCHEMICAL_CHEST);
	}
}