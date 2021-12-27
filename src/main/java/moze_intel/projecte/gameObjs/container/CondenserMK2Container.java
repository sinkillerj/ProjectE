package moze_intel.projecte.gameObjs.container;

import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.blocks.Condenser;
import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class CondenserMK2Container extends CondenserContainer {

	public CondenserMK2Container(int windowId, PlayerInventory invPlayer, CondenserMK2Tile condenser) {
		super(PEContainerTypes.CONDENSER_MK2_CONTAINER, windowId, invPlayer, condenser);
	}

	@Override
	protected void initSlots(PlayerInventory invPlayer) {
		this.addSlot(new SlotCondenserLock(tile::getLockInfo, 0, 12, 6));
		//Inputs
		IItemHandler input = tile.getInput();
		Predicate<ItemStack> validator = s -> SlotPredicates.HAS_EMC.test(s) && !tile.isStackEqualToLock(s);
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.addSlot(new ValidatedSlot(input, j + i * 6, 12 + j * 18, 26 + i * 18, validator));
			}
		}
		//Outputs
		IItemHandler output = tile.getOutput();
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.addSlot(new ValidatedSlot(output, j + i * 6, 138 + j * 18, 26 + i * 18, SlotPredicates.ALWAYS_FALSE));
			}
		}
		addPlayerInventory(invPlayer, 48, 154);
	}

	@Override
	protected BlockRegistryObject<? extends Condenser, ?> getValidBlock() {
		return PEBlocks.CONDENSER_MK2;
	}
}