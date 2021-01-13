package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.gameObjs.container.AlchChestContainer;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

public class AlchChestTile extends ChestTileEmc implements INamedContainerProvider {

	private final ItemStackHandler inventory = new StackHandler(104);

	public AlchChestTile() {
		super(PETileEntityTypes.ALCHEMICAL_CHEST.get());
		itemHandlerResolver = BasicCapabilityResolver.getBasicItemHandlerResolver(inventory);
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
		if (world != null) {
			updateChest();
			for (int i = 0; i < inventory.getSlots(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					stack.getCapability(ProjectEAPI.ALCH_CHEST_ITEM_CAPABILITY).ifPresent(alchChestItem -> alchChestItem.updateInAlchChest(world, pos, stack));
				}
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
		return TextComponentUtil.build(PEBlocks.ALCHEMICAL_CHEST);
	}
}