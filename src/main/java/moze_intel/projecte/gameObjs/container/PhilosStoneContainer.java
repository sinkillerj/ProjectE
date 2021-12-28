package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

/**
 * We *don't* use our own container type here. The only thing we do differently is the interact check and that only applies serverside. So just a inheritor class is
 * enough, the packet/gui clientside can all use vanilla's stuff.
 *
 * Note: We require a {@link ContainerLevelAccess} so that the {@link CraftingMenu} can properly perform things like the crafting matrix changing. We override
 * canInteractWith however, so that even if the player is moving while being in the crafting grid, they continue to be able to access it.
 */
public class PhilosStoneContainer extends CraftingMenu {

	public PhilosStoneContainer(int windowId, Inventory invPlayer, ContainerLevelAccess worldPosCallable) {
		super(windowId, invPlayer, worldPosCallable);
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return true;
	}
}