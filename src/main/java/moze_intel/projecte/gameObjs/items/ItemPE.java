package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ItemPE extends Item
{
	public ItemPE()
	{
		this.setCreativeTab(ObjHandler.cTab);
	}

	@Override
	public Item setUnlocalizedName(String message)
	{
		return super.setUnlocalizedName("pe_" + message);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChange)
	{
		return !ItemHelper.basicAreStacksEqual(oldStack, newStack);
	}

	public static double getEmc(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		return stack.getTagCompound().getDouble("StoredEMC");
	}
	
	public static void setEmc(ItemStack stack, double amount)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		stack.getTagCompound().setDouble("StoredEMC", amount);
	}
	
	public static void addEmc(ItemStack stack, double amount)
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
	
	public static boolean consumeFuel(EntityPlayer player, ItemStack stack, double amount, boolean shouldRemove)
	{
		if (amount <= 0)
		{
			return true;
		}

		double current = getEmc(stack);
		
		if (current < amount)
		{
			removeEmc(stack, current);
			amount -= current;
			
			double consume = EMCHelper.consumePlayerFuel(player, amount);
			
			if (consume == -1)
			{
				addEmc(stack, current);
				return false;
			}
			
			addEmc(stack, consume);
		}
		
		if (shouldRemove)
		{
			removeEmc(stack, amount);
		}
		
		return true;
	}
	
	public String getTexture(String name)
	{
		return ("projecte:" + name);
	}
	
	public String getTexture(String folder, String name)
	{
		return ("projecte:" + folder + "/" + name);
	}
}
