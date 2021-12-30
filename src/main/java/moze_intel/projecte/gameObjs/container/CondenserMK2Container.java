package moze_intel.projecte.gameObjs.container;

import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.block_entities.CondenserMK2BlockEntity;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class CondenserMK2Container extends CondenserContainer {

	public CondenserMK2Container(int windowId, Inventory playerInv, CondenserMK2BlockEntity condenser) {
		super(PEContainerTypes.CONDENSER_MK2_CONTAINER, windowId, playerInv, condenser);
	}

	@Override
	protected void initSlots() {
		this.addSlot(new SlotCondenserLock(blockEntity::getLockInfo, 0, 12, 6));
		//Inputs
		IItemHandler input = blockEntity.getInput();
		Predicate<ItemStack> validator = s -> SlotPredicates.HAS_EMC.test(s) && !blockEntity.isStackEqualToLock(s);
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.addSlot(new ValidatedSlot(input, j + i * 6, 12 + j * 18, 26 + i * 18, validator));
			}
		}
		//Outputs
		IItemHandler output = blockEntity.getOutput();
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.addSlot(new ValidatedSlot(output, j + i * 6, 138 + j * 18, 26 + i * 18, SlotPredicates.ALWAYS_FALSE));
			}
		}
		addPlayerInventory(48, 154);
	}

	@Override
	protected BlockRegistryObject<? extends Condenser, ?> getValidBlock() {
		return PEBlocks.CONDENSER_MK2;
	}
}