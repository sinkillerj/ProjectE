package moze_intel.projecte.api.inventory;

import moze_intel.projecte.gameObjs.block_entities.WrappedItemHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.extension.ExtendWith(net.neoforged.testframework.junit.EphemeralTestServerProvider.class)
class InventoryTransferTest {
    @Test
    void rejectedAndAbortedTransfersDoNotChangeInventory() {
        PEItemStacksHandler inventory = new PEItemStacksHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.is(Items.DIAMOND);
            }
            @Override
            public int getSlotLimit(int slot) {
                return 4;
            }
        };
        try (Transaction tx = Transaction.openRoot()) {
            assertEquals(0, inventory.insert(0, ItemResource.of(Items.DIRT), 8, tx));
            assertEquals(4, inventory.insert(0, ItemResource.of(Items.DIAMOND), 8, tx));
        }
        assertTrue(inventory.getStackInSlot(0).isEmpty());
        try (Transaction tx = Transaction.openRoot()) {
            assertEquals(4, inventory.insert(0, ItemResource.of(Items.DIAMOND), 8, tx));
            tx.commit();
        }
        assertEquals(4, inventory.getStackInSlot(0).getCount());
    }

    @Test
    void combinedAutomationPreservesInputAndOutputRestrictions() {
        PEItemStacksHandler input = new PEItemStacksHandler(1);
        PEItemStacksHandler output = new PEItemStacksHandler(1);
        output.setStackInSlot(0, new ItemStack(Items.DIAMOND, 3));
        PECombinedItemStacks combined = new PECombinedItemStacks(
                new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN),
                new WrappedItemHandler(output, WrappedItemHandler.WriteMode.OUT));
        try (Transaction tx = Transaction.openRoot()) {
            assertEquals(2, combined.insert(0, ItemResource.of(Items.DIRT), 2, tx));
            assertEquals(0, combined.extract(0, ItemResource.of(Items.DIRT), 1, tx));
            assertEquals(0, combined.insert(1, ItemResource.of(Items.DIAMOND), 1, tx));
            assertEquals(1, combined.extract(1, ItemResource.of(Items.DIAMOND), 1, tx));
            tx.commit();
        }
        assertEquals(2, input.getStackInSlot(0).getCount());
        assertEquals(2, output.getStackInSlot(0).getCount());
    }
    @Test
    void removingFromCombinedOutputSlotUpdatesStoredStack() {
        PEItemStacksHandler output = new PEItemStacksHandler(1);
        output.setStackInSlot(0, new ItemStack(Items.DIAMOND, 3));
        PECombinedItemStacks combined = new PECombinedItemStacks(
                new WrappedItemHandler(output, WrappedItemHandler.WriteMode.OUT));
        InventoryContainerSlot slot = new InventoryContainerSlot(combined, 0, 0, 0);
        assertEquals(1, slot.remove(1).getCount());
        assertEquals(2, output.getStackInSlot(0).getCount());
    }

    @Test
    void loadsLegacyInventoryWithoutLosingItems(net.minecraft.server.MinecraftServer server) {
        var ops = server.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
        var item = (net.minecraft.nbt.CompoundTag) ItemStack.CODEC.encodeStart(ops, new ItemStack(Items.DIAMOND, 7)).getOrThrow();
        item.putInt("Slot", 1);
        var entries = new net.minecraft.nbt.ListTag();
        entries.add(item);
        var tag = new net.minecraft.nbt.CompoundTag();
        tag.putInt("Size", 2);
        tag.put("Items", entries);
        var inventory = new PEItemStacksHandler(2);
        inventory.deserialize(net.minecraft.world.level.storage.TagValueInput.create(
                net.minecraft.util.ProblemReporter.DISCARDING, server.registryAccess(), tag));
        assertTrue(inventory.getStackInSlot(0).isEmpty());
        assertTrue(inventory.getStackInSlot(1).is(Items.DIAMOND));
        assertEquals(7, inventory.getStackInSlot(1).getCount());
    }

}
