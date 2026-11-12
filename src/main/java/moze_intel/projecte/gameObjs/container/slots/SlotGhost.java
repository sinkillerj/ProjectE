package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

public class SlotGhost extends ResourceHandlerSlot implements ISlotGhost {

	private final Predicate<ItemStack> validator;

	public SlotGhost(ResourceHandler<ItemResource> inv, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator) {
		super(inv, PEItemStacksHandler.requireModifier(inv), slotIndex, xPos, yPos);
		this.validator = validator;
	}

	public SlotGhost(PEItemStacksHandler inv, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator) {
		super(inv, inv, slotIndex, xPos, yPos);
		this.validator = validator;
	}

	public SlotGhost(PECombinedItemStacks inv, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator) {
		super(inv, inv, slotIndex, xPos, yPos);
		this.validator = validator;
	}

	public SlotGhost(ResourceHandler<ItemResource> inv, IndexModifier<ItemResource> modifier, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator) {
		super(inv, modifier, slotIndex, xPos, yPos);
		this.validator = validator;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		if (super.mayPlace(stack) && validator.test(stack)) {
			set(stack);
		}
		return false;
	}

	@Override
	protected void setStackCopy(@NotNull ItemStack stack) {
		super.setStackCopy(stack.copyWithCount(1));
	}

	@Override
	public boolean mayPickup(@NotNull Player player) {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		return 1;
	}
}
