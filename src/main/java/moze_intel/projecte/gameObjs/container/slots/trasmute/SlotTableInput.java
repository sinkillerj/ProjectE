package moze_intel.projecte.gameObjs.container.slots.trasmute;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTableInput extends Slot {
	private TransmuteTile tile;

	public SlotTableInput(TransmuteTile tile, int par2, int par3, int par4) {
		super(tile, par2, par3, par4);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return !this.getHasStack() && !tile.hasMaxedEmc() && Utils.doesItemHaveEmc(stack);
	}

	@Override
	public void putStack(ItemStack stack) {
		if (stack == null) {
			return;
		}

		if (stack.getItem() == ObjHandler.kleinStars) {
			int remainingEmc = Utils.getKleinStarMaxEmc(stack) - (int) Math.ceil(ItemPE.getEmc(stack));

			if (tile.getStoredEmc() >= remainingEmc) {
				ItemPE.addEmc(stack, remainingEmc);
				tile.removeEmcWithPKT(remainingEmc);
			} else {
				ItemPE.addEmc(stack, tile.getStoredEmc());
				tile.setEmcValueWithPKT(0);
			}
		}

		if (stack.getItem() != ObjHandler.tome) {
			tile.handleKnowledge(stack.copy());
		} else {
			tile.updateOutputs();
		}

		super.putStack(stack);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
