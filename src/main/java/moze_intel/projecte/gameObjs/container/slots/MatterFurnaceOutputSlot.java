package moze_intel.projecte.gameObjs.container.slots;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.IItemHandler;

//[VanillaCopy] Adapted from FurnaceResultSlot
public class MatterFurnaceOutputSlot extends InventoryContainerSlot {

	private final PlayerEntity player;
	private int removeCount;

	public MatterFurnaceOutputSlot(PlayerEntity player, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.player = player;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return false;
	}

	@Nonnull
	@Override
	public ItemStack remove(int amount) {
		if (this.hasItem()) {
			this.removeCount += Math.min(amount, this.getItem().getCount());
		}
		return super.remove(amount);
	}

	@Nonnull
	@Override
	public ItemStack onTake(@Nonnull PlayerEntity player, @Nonnull ItemStack stack) {
		this.checkTakeAchievements(stack);
		super.onTake(player, stack);
		return stack;
	}

	@Override
	protected void onQuickCraft(@Nonnull ItemStack stack, int pAmount) {
		this.removeCount += pAmount;
		this.checkTakeAchievements(stack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(player.level, player, removeCount);
		removeCount = 0;
		BasicEventHooks.firePlayerSmeltedEvent(player, stack);
	}
}