package moze_intel.projecte.gameObjs.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * We *don't* use our own container type here.
 * The only thing we do differently is the interact check and that only applies serverside.
 * So just a inheritor class is enough, the packet/gui clientside can all use vanilla's stuff.
 */
public class PhilosStoneContainer extends WorkbenchContainer
{
	public PhilosStoneContainer(int windowId, PlayerInventory invPlayer)
	{
		super(windowId, invPlayer);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return true;
	}

}
