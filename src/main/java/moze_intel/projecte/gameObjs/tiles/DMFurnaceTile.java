package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class DMFurnaceTile extends RMFurnaceTile
{
	public DMFurnaceTile()
	{
		super(ObjHandler.DM_FURNACE_TILE, 10, 3);
	}
	
	@Override
	protected int getInvSize()
	{
		return 9;
	}

	@Override
	protected float getOreDoubleChance() {
		return 0.5F;
	}

	@Override
	public int getCookProgressScaled(int value)
	{
		return furnaceCookTime * value / ticksBeforeSmelt;
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerIn)
	{
		return new DMFurnaceContainer(windowId, playerInv, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(ObjHandler.dmFurnaceOff.getTranslationKey());
	}
}