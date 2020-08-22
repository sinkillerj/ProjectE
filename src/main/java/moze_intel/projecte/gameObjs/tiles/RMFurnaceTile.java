package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RMFurnaceTile extends DMFurnaceTile {

	public RMFurnaceTile() {
		super(PETileEntityTypes.RED_MATTER_FURNACE.get(), 3, 4);
	}

	@Override
	protected int getInvSize() {
		return 13;
	}

	@Override
	protected float getOreDoubleChance() {
		return 1F;
	}

	@Override
	public int getCookProgressScaled(int value) {
		return (furnaceCookTime + (isBurning() && canSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
		return new RMFurnaceContainer(windowId, inv, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(PEBlocks.RED_MATTER_FURNACE.getBlock().getTranslationKey());
	}
}