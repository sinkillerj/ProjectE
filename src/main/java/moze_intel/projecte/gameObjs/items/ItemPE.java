package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemPE extends Item
{
	public static final String TAG_ACTIVE = "Active";
	public static final String TAG_MODE = "Mode";
	protected static final ResourceLocation ACTIVE_NAME = new ResourceLocation(PECore.MODID, "active");
	protected static final IItemPropertyGetter ACTIVE_GETTER = (stack, world, entity) -> stack.hasTag() && stack.getTag().getBoolean(TAG_ACTIVE) ? 1F : 0F;
	protected static final IItemPropertyGetter MODE_GETTER = (stack, world, entity) -> stack.hasTag() ? stack.getTag().getInt(TAG_MODE) : 0F;

	public ItemPE(Properties props)
	{
		super(props);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		if (oldStack.getItem() != newStack.getItem())
			return true;

		boolean diffActive = oldStack.hasTag() && newStack.hasTag()
				&& oldStack.getTag().contains(TAG_ACTIVE) && newStack.getTag().contains(TAG_ACTIVE)
				&& !oldStack.getTag().get(TAG_ACTIVE).equals(newStack.getTag().get(TAG_ACTIVE));

		boolean diffMode = oldStack.hasTag() && newStack.hasTag()
				&& oldStack.getTag().contains(TAG_MODE) && newStack.getTag().contains(TAG_MODE)
				&& !oldStack.getTag().get(TAG_MODE).equals(newStack.getTag().get(TAG_MODE));

		return diffActive || diffMode;
	}

	public static double getEmc(ItemStack stack)
	{
        return stack.getOrCreateTag().getDouble("StoredEMC");
	}
	
	public static void setEmc(ItemStack stack, double amount)
	{
        stack.getOrCreateTag().putDouble("StoredEMC", amount);
	}
	
	public static void addEmcToStack(ItemStack stack, double amount)
	{
		setEmc(stack, getEmc(stack) + amount);
	}
	
	public static void removeEmc(ItemStack stack, double amount)
	{
		double result = getEmc(stack) - amount;
		
		if (result < 0)
		{
			result = 0;
		}
		
		setEmc(stack, result);
	}
	
	public static boolean consumeFuel(PlayerEntity player, ItemStack stack, double amount, boolean shouldRemove)
	{
		if (amount <= 0)
		{
			return true;
		}

		double current = getEmc(stack);
		
		if (current < amount)
		{
			double consume = EMCHelper.consumePlayerFuel(player, amount - current);
			
			if (consume == -1)
			{
				return false;
			}
			
			addEmcToStack(stack, consume);
		}
		
		if (shouldRemove)
		{
			removeEmc(stack, amount);
		}
		
		return true;
	}
	
}
