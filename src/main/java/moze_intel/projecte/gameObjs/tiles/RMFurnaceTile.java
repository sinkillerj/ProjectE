package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RMFurnaceTile extends DMFurnaceTile {

	public RMFurnaceTile() {
		super(ObjHandler.RM_FURNACE_TILE, 3, 4);
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
		return new TranslationTextComponent(ObjHandler.rmFurnace.getTranslationKey());
	}
}