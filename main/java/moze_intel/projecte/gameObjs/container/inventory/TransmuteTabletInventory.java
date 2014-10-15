package moze_intel.projecte.gameObjs.container.inventory;

import java.util.LinkedList;

import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.playerData.TransmutationKnowledge;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class TransmuteTabletInventory implements IInventory
{
	public double emc;
	private EntityPlayer player = null;
	private final int LOCK_INDEX = 8;
	private final int[] MATTER_INDEXES = new int[] {12, 11, 13, 10, 14, 21, 15, 20, 16, 19, 17, 18};
	private final int[] FUEL_INDEXES = new int[] {22, 23, 24, 25};
	private ItemStack[] inventory = new ItemStack[26];
	public int learnFlag = 0;
	public String filter = "";
	
	public TransmuteTabletInventory(ItemStack stack, EntityPlayer player)
	{
		this.player = player;
		
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		readFromNBT(stack.stackTagCompound);
	}
	
	public void handleKnowledge(ItemStack stack)
	{
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}
		
		if (stack.hasTagCompound())
		{
			stack.stackTagCompound = null;
		}
		
		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0 && stack.getItemDamage() != 0)
		{
			stack.setItemDamage(0);
		}
		
		if (!hasKnowledge(stack) && !TransmutationKnowledge.hasFullKnowledge(player.getCommandSenderName()))
		{
			learnFlag = 300;
			
			if (stack.getItem() == ObjHandler.tome)
			{
				TransmutationKnowledge.setAllKnowledge(player.getCommandSenderName());
			}
			else
			{
				TransmutationKnowledge.addToKnowledge(player.getCommandSenderName(), stack);
			}
			
			if (!player.worldObj.isRemote)
			{
				TransmutationKnowledge.sync(player);
			}
		}
		
		updateOutputs();
	}
	
	public void checkForUpdates()
	{
		int matterEmc = Utils.getEmcValue(inventory[MATTER_INDEXES[0]]);
		int fuelEmc = Utils.getEmcValue(inventory[FUEL_INDEXES[0]]);
		
		int maxEmc = matterEmc > fuelEmc ? matterEmc : fuelEmc;
		
		if (maxEmc > emc)
		{
			updateOutputs();
		}
	}
	
	public void updateOutputs()
	{
		LinkedList<ItemStack> knowledge = (LinkedList<ItemStack>) TransmutationKnowledge.getKnowledge(player.getCommandSenderName()).clone();
		
		if (knowledge == null)
		{
			return;
		}
		
		ItemStack[] matter = new ItemStack[12];
		ItemStack[] fuels = new ItemStack[4];
		int currentIndex = 0;
		ItemStack lock = inventory[LOCK_INDEX];
		
		if (lock != null)
		{
			if (FuelMapper.isStackFuel(lock))
			{
				if (emc < Utils.getEmcValue(lock))
				{
					fuels[0] = null;
				}
				else
				{
					fuels[0] = getFromKnowledge(lock);
				}
				
				matter[0] = getMaxEmc(0, matter, false, knowledge);
				currentIndex++;
			}
			else
			{
				if (emc < Utils.getEmcValue(lock))
				{
					matter[0] = null;
				}
				else
				{
					matter[0] = getFromKnowledge(lock);
				}
				
				fuels[0] = getMaxEmc(0, fuels, true, knowledge);
				currentIndex++;
			}
		}
		else
		{
			matter[0] = getMaxEmc(0, matter, false, knowledge);
			fuels[0] = getMaxEmc(0, fuels, true, knowledge);
			currentIndex++;
		}
			
		while (currentIndex < 12)
		{
			if (currentIndex < 4)
			{
				int prevEmc = Utils.getEmcValue(fuels[currentIndex - 1]);
							
				if (prevEmc != 0)
				{
					fuels[currentIndex] = getMaxEmc(prevEmc, fuels, true, knowledge);
				}
			}
					
			int prevEmc = Utils.getEmcValue(matter[currentIndex - 1]);
					
			if (prevEmc == 0)
			{
				currentIndex++;
				continue;
			}
				
			matter[currentIndex] = getMaxEmc(prevEmc, matter, false, knowledge);
			currentIndex++;
		}
		
		for (int i = 0; i < 12; i++)
		{
			ItemStack m = matter[i];
			inventory[MATTER_INDEXES[i]] = m;
			
			if (i < 4)
			{
				ItemStack f = fuels[i];
				inventory[FUEL_INDEXES[i]] = f;
			}
		}
	}
	
	private ItemStack getMaxEmc(int maxValue, ItemStack[] currentInv, boolean isFuel, LinkedList<ItemStack> knowledge)
	{
		ItemStack max = null;
		int currentMax = 0;
		
		for (ItemStack stack : knowledge)
		{
			if (stack == null || stack.getItem() == null)
			{
				continue;
			}
			
			boolean flag = FuelMapper.isStackFuel(stack);
			
			if (flag && !isFuel || !flag && isFuel)
			{
				continue;
			}
			
			if (stack == null || stack.getDisplayName() == null)
			{
				continue;
			}
			
			if (filter.length() > 0 && !stack.getDisplayName().toLowerCase().contains(filter))
			{
				continue;
			}
			
			int emc = Utils.getEmcValue(stack);
			
			if (emc > this.emc)
			{
				continue;
			}
			
			if (maxValue == 0)
			{
				if (emc >= currentMax)
				{
					currentMax = emc;
					max = stack;
				}
			}
			else
			{
				if (emc <= maxValue && !arrayContains(currentInv, stack) && emc >= currentMax)
				{
					currentMax = emc;
					max = stack;
				}
			}
		}
		
		return max;
	}
	
	private ItemStack getFromKnowledge(ItemStack stack)
	{
		for (ItemStack s : TransmutationKnowledge.getKnowledge(player.getCommandSenderName()))
		{
			if (stack.getItem().equals(s.getItem()) && stack.getItemDamage() == s.getItemDamage())
			{
				return s;
			}
		}
		
		return null;
	}
	
	private boolean hasKnowledge(ItemStack stack)
	{
		for (ItemStack s : TransmutationKnowledge.getKnowledge(player.getCommandSenderName()))
		{
			if (s == null)
			{
				continue;
			}
			
			if (stack.getItem().equals(s.getItem()) && stack.getItemDamage() == s.getItemDamage())
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean arrayContains(ItemStack[] array, ItemStack stack)
	{
		for (ItemStack s : array)
		{
			if (s == null)
			{
				continue;
			}
			
			if (s.getItem().equals(stack.getItem()) && s.getItemDamage() == stack.getItemDamage())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void setPlayer(EntityPlayer player)
	{
		this.player = player;
	}
	
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		emc = nbt.getDouble("EMC");
		
		NBTTagList list = nbt.getTagList("Items", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			
			ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
			
			inventory[tag.getByte("slot")] = stack;
		}
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setDouble("EMC", emc);
		
		NBTTagList list = new NBTTagList();
		
		for (int i = 0; i <= 8; i++)
		{
			if (inventory[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				inventory[i].writeToNBT(tag);
				tag.setByte("slot", (byte) i);
				
				list.appendTag(tag);
			}
		}
		
		nbt.setTag("Items", list);
	}
	
	@Override
	public int getSizeInventory() 
	{
		return 26;
	}

	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qty) 
	{
		ItemStack stack = inventory[slot];
		if (stack != null)
		{
			if (stack.stackSize <= qty)
			{
				inventory[slot] = null;
			}
			else
			{
				stack = stack.splitStack(qty);
				if (stack.stackSize == 0)
				{
					inventory[slot] = null;
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) 
	{
		if (inventory[slot] != null)
		{
			ItemStack stack = inventory[slot];
			inventory[slot] = null;
			return stack;
		}
		
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) 
	{
		inventory[slot] = stack;
		
		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		this.markDirty();
	}

	@Override
	public String getInventoryName() 
	{
		return "Transmutation Stone";
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return true;
	}

	@Override
	public void openInventory() 
	{
		updateOutputs();
	}

	@Override
	public void closeInventory() 
	{
		if (player.getHeldItem() != null)
		{
			writeToNBT(player.getHeldItem().stackTagCompound);
		}
		
		player = null;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}

	@Override
	public void markDirty() {}
	
	public void addEmc(double value)
	{
		emc += value;
		
		if (emc >= Constants.TILE_MAX_EMC || emc < 0)
		{
			emc = Constants.TILE_MAX_EMC;
		}
	}
	
	public void removeEmc(double value) 
	{
		emc -= value;
		
		if (emc < 0)
		{
			emc = 0;
		}
	}

	public boolean hasMaxedEmc()
	{
		return emc >= Constants.TILE_MAX_EMC;
	}
}
