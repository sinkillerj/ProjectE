package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

//[VanillaCopy] Adapted from FurnaceResultSlot
public class MatterFurnaceOutputSlot extends InventoryContainerSlot {

	private final Player player;
	private int removeCount;

	public MatterFurnaceOutputSlot(Player player, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.player = player;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return false;
	}

	@NotNull
	@Override
	public ItemStack remove(int amount) {
		if (this.hasItem()) {
			this.removeCount += Math.min(amount, this.getItem().getCount());
		}
		return super.remove(amount);
	}

	@Override
	public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
		this.checkTakeAchievements(stack);
		super.onTake(player, stack);
	}

	@Override
	protected void onQuickCraft(@NotNull ItemStack stack, int pAmount) {
		this.removeCount += pAmount;
		this.checkTakeAchievements(stack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(player.level, player, removeCount);
		removeCount = 0;
		ForgeEventFactory.firePlayerSmeltedEvent(player, stack);
	}
}