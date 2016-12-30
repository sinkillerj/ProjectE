package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class SlotGhost extends SlotItemHandler
{
	private final Predicate<ItemStack> validator;

	public SlotGhost(IItemHandler inv, int slotIndex, int xPos, int yPos, Predicate<ItemStack> validator)
	{
		super(inv, slotIndex, xPos, yPos);
		this.validator = validator;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		if (!stack.isEmpty() && validator.test(stack))
		{
			this.putStack(ItemHelper.getNormalizedStack(stack));
		}
		
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}
	
	@Override
	public int getSlotStackLimit() 
	{
		return 1;
	}
}
