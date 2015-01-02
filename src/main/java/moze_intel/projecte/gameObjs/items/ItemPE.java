package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Utils;
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

	public static double getEmc(ItemStack stack)
	{
		if (stack.stackTagCompound == null)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		return stack.stackTagCompound.getDouble("StoredEMC");
	}
	
	public static void setEmc(ItemStack stack, double amount)
	{
		if (stack.stackTagCompound == null)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		stack.stackTagCompound.setDouble("StoredEMC", amount);
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
		double current = getEmc(stack);
		
		if (current < amount)
		{
			removeEmc(stack, current);
			amount -= current;
			
			double consume = Utils.consumePlayerFuel(player, amount);
			
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
	
	/*public void setTickCounter(ItemStack stack, byte count)
	{
		stack.stackTagCompound.setByte("TickCounter", count);
	}
	
	public byte getTickCount(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("TickCounter");
	}
	
	public void increaseTickCounter(ItemStack stack)
	{
		byte current = getTickCount(stack);
		
		if (current < 20)
		{
			setTickCounter(stack, ++current);
		}
		else
		{
			setTickCounter(stack, (byte) 0);
		}
	}
	
	public boolean isReady(ItemStack stack)
	{
		return getTickCount(stack) == 0;
	}*/
	
	public String getTexture(String name)
	{
		return ("projecte:" + name);
	}
	
	public String getTexture(String folder, String name)
	{
		return ("projecte:" + folder + "/" + name);
	}
}
