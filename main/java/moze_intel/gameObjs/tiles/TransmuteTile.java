package moze_intel.gameObjs.tiles;

import java.util.List;

import moze_intel.utils.Constants;
import moze_intel.utils.PlayerKnowledge;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TransmuteTile extends TileEmc implements IInventory
{
	private EntityPlayer player = null;
	private final int LOCK_INDEX = 8;
	private final int[] MATTER_INDEXES = new int[] {12, 11, 13, 10, 14, 21, 15, 20, 16, 19, 17, 18};
	private final int[] FUEL_INDEXES = new int[] {22, 23, 24, 25};
	private final int MAX_MATTER_SIZE = 12;
	private ItemStack[] inventory = new ItemStack[26];
	private List<ItemStack> knowledge = null;
	public int learnFlag = 0;
	
	
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
		
		if (!hasKnowledge(stack))
		{
			learnFlag = 300;
			PlayerKnowledge.addKnowledge(player, stack);
			
			if (!this.worldObj.isRemote)
			{
				PlayerKnowledge.syncPlayerProps(player);
			}
		}
		
		updateOutputs();
	}
	
	public void checkForUpdates()
	{
		int maxEmc = Utils.getEmcValue(inventory[MATTER_INDEXES[0]]);
		
		if (maxEmc > this.getStoredEMC())
		{
			updateOutputs();
		}
	}
	
	public void updateOutputs()
	{
		ItemStack[] matter = new ItemStack[12];
		ItemStack[] fuels = new ItemStack[4];
		int currentIndex = 0;
		ItemStack lock = inventory[LOCK_INDEX];
		
		if (lock != null)
		{
			if (Constants.isStackFuel(lock))
			{
				if (this.getStoredEMC() < Utils.getEmcValue(lock))
				{
					fuels[0] = null;
				}
				else
				{
					fuels[0] = getFromKnowledge(lock);
				}
				
				matter[0] = getMaxEmc(0, matter, false);
				currentIndex++;
			}
			else
			{
				if (this.getStoredEMC() < Utils.getEmcValue(lock))
				{
					matter[0] = null;
				}
				else
				{
					matter[0] = getFromKnowledge(lock);
				}
				
				fuels[0] = getMaxEmc(0, fuels, true);
				currentIndex++;
			}
		}
		else
		{
			matter[0] = getMaxEmc(0, matter, false);
			fuels[0] = getMaxEmc(0, fuels, true);
			currentIndex++;
		}
			
		while (currentIndex < 12)
		{
			if (currentIndex < 4)
			{
				int prevEmc = Utils.getEmcValue(fuels[currentIndex - 1]);
							
				if (prevEmc != 0)
				{
					fuels[currentIndex] = getMaxEmc(prevEmc, fuels, true);
				}
			}
					
			int prevEmc = Utils.getEmcValue(matter[currentIndex - 1]);
					
			if (prevEmc == 0)
			{
				currentIndex++;
				continue;
			}
				
			matter[currentIndex] = getMaxEmc(prevEmc, matter, false);
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
	
	private ItemStack getMaxEmc(int maxValue, ItemStack[] currentInv, boolean isFuel)
	{
		ItemStack max = null;
		int currentMax = 0;
		
		for (ItemStack stack : knowledge)
		{
			boolean flag = Constants.isStackFuel(stack);
			
			if (flag && !isFuel || !flag && isFuel)
			{
				continue;
			}
			
			int emc = Utils.getEmcValue(stack);
			
			if (emc > this.getStoredEMC())
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
		for (ItemStack s : knowledge)
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
		for (ItemStack s : knowledge)
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
	
	private List<ItemStack> getKnowledge()
	{
		return PlayerKnowledge.getPlayerKnowledge(player);
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
	
	public boolean isUsed()
	{
		return player != null;
	}
	
	public void setPlayer(EntityPlayer player)
	{
		this.player = player;
	}
	
	@Override	
	public Packet getDescriptionPacket() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) 
	{
		this.readFromNBT(packet.func_148857_g());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		this.setEmcValue(nbt.getDouble("EMC"));
		
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[26];
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");
			
			if (slot >= 0 && slot < 26)
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		nbt.setDouble("EMC", this.getStoredEMC());
		
		NBTTagList list = new NBTTagList();
		
		for (int i = 0; i < 26; i++)
		{
			if (inventory[i] == null) 
			{
				continue;
			}
			
			NBTTagCompound subNBT = new NBTTagCompound();
			subNBT.setByte("Slot", (byte) i);
			inventory[i].writeToNBT(subNBT);
			list.appendTag(subNBT);
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
		this.knowledge = getKnowledge();
		updateOutputs();
	}

	@Override
	public void closeInventory() 
	{
		player = null;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}
}
