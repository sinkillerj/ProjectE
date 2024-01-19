package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.gameObjs.block_entities.DMFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

//[VanillaCopy] Adapted from FurnaceResultSlot
public class MatterFurnaceOutputSlot extends InventoryContainerSlot {

	private final DMFurnaceBlockEntity furnace;
	private final Player player;
	private int removeCount;

	public MatterFurnaceOutputSlot(Player player, DMFurnaceBlockEntity furnace, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.furnace = furnace;
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
		stack.onCraftedBy(player.level(), player, removeCount);
		if (player instanceof ServerPlayer serverPlayer) {
			furnace.awardUsedRecipesAndPopExperience(serverPlayer);
		}

		removeCount = 0;
		EventHooks.firePlayerSmeltedEvent(player, stack);
	}
}