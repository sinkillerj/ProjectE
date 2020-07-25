package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchChestContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class AlchChestTile extends ChestTileEmc implements INamedContainerProvider {

	private final ItemStackHandler inventory = new StackHandler(104);
	private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

	public AlchChestTile() {
		super(ObjHandler.ALCH_CHEST_TILE);
	}

	@Override
	public void remove() {
		super.remove();
		inventoryCap.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventoryCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
		super.read(state, nbt);
		inventory.deserializeNBT(nbt);
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.merge(inventory.serializeNBT());
		return nbt;
	}

	@Override
	public void tick() {
		updateChest();
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				stack.getCapability(ProjectEAPI.ALCH_CHEST_ITEM_CAPABILITY).ifPresent(alchChestItem -> alchChestItem.updateInAlchChest(world, pos, stack));
			}
		}
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
		return new AlchChestContainer(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ObjHandler.alchChest.getTranslationKey());
	}
}