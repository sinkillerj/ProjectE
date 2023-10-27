package moze_intel.projecte.gameObjs.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.NotNull;

/**
 * We *don't* use our own container type here. The only thing we do differently is the interact check and that only applies serverside. So just a inheritor class is
 * enough, the packet/gui clientside can all use vanilla's stuff.
 * <p> <p>
 * Note: We require a {@link ContainerLevelAccess} so that the {@link CraftingMenu} can properly perform things like the crafting matrix changing. We override
 * canInteractWith however, so that even if the player is moving while being in the crafting grid, they continue to be able to access it.
 */
public class PhilosStoneContainer extends CraftingMenu {

	public PhilosStoneContainer(int windowId, Inventory invPlayer, ContainerLevelAccess worldPosCallable) {
		super(windowId, invPlayer, worldPosCallable);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
}