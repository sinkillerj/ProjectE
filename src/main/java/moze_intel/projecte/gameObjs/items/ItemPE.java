package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemPE extends Item
{
	public static final String TAG_ACTIVE = "Active";
	public static final String TAG_MODE = "Mode";
	protected static final ResourceLocation ACTIVE_NAME = new ResourceLocation(PECore.MODID, "active");
	protected static final IItemPropertyGetter ACTIVE_GETTER = (stack, world, entity) -> stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_ACTIVE) ? 1F : 0F;

	public ItemPE()
	{
		this.setCreativeTab(ObjHandler.cTab);
	}

	@Nonnull
	@Override
	public Item setTranslationKey(@Nonnull String message)
	{
		return super.setTranslationKey("pe_" + message);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		if (oldStack.getItem() != newStack.getItem())
			return true;

		boolean diffActive = oldStack.hasTagCompound() && newStack.hasTagCompound()
				&& oldStack.getTagCompound().hasKey(TAG_ACTIVE) && newStack.getTagCompound().hasKey(TAG_ACTIVE)
				&& !oldStack.getTagCompound().getTag(TAG_ACTIVE).equals(newStack.getTagCompound().getTag(TAG_ACTIVE));

		boolean diffMode = oldStack.hasTagCompound() && newStack.hasTagCompound()
				&& oldStack.getTagCompound().hasKey(TAG_MODE) && newStack.getTagCompound().hasKey(TAG_MODE)
				&& !oldStack.getTagCompound().getTag(TAG_MODE).equals(newStack.getTagCompound().getTag(TAG_MODE));

		return diffActive || diffMode;
	}

	public static long getEmc(ItemStack stack)
	{
		return ItemHelper.getOrCreateCompound(stack).getLong("StoredEMC");
	}
	
	public static void setEmc(ItemStack stack, long amount)
	{
		ItemHelper.getOrCreateCompound(stack).setLong("StoredEMC", amount);
	}
	
	public static void addEmcToStack(ItemStack stack, long amount)
	{
		setEmc(stack, getEmc(stack) + amount);
	}
	
	public static void removeEmc(ItemStack stack, long amount)
	{
		long result = getEmc(stack) - amount;
		
		if (result < 0)
		{
			result = 0;
		}
		
		setEmc(stack, result);
	}
	
	public static boolean consumeFuel(EntityPlayer player, ItemStack stack, long amount, boolean shouldRemove)
	{
		if (amount <= 0)
		{
			return true;
		}

		long current = getEmc(stack);
		
		if (current < amount)
		{
			long consume = EMCHelper.consumePlayerFuel(player, amount - current);
			
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
