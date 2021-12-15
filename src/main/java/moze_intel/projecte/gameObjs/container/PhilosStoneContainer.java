package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

/**
 * We *don't* use our own container type here. The only thing we do differently is the interact check and that only applies serverside. So just a inheritor class is
 * enough, the packet/gui clientside can all use vanilla's stuff.
 *
 * Note: We require an {@link IWorldPosCallable} so that the {@link WorkbenchContainer} can properly perform things like the crafting matrix changing. We override
 * canInteractWith however, so that even if the player is moving while being in the crafting grid, they continue to be able to access it.
 */
public class PhilosStoneContainer extends WorkbenchContainer {

	public PhilosStoneContainer(int windowId, PlayerInventory invPlayer, IWorldPosCallable worldPosCallable) {
		super(windowId, invPlayer, worldPosCallable);
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return true;
	}
}