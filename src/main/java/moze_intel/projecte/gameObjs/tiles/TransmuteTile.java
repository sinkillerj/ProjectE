package moze_intel.projecte.gameObjs.tiles;

import com.google.common.collect.Lists;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncTableEMCPKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.NBTWhitelist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class TransmuteTile extends TileEmc implements ISidedInventory
{
	private EntityPlayer player = null;
	private static final int LOCK_INDEX = 8;
	private static final int[] MATTER_INDEXES = new int[] {12, 11, 13, 10, 14, 21, 15, 20, 16, 19, 17, 18};
	private static final int[] FUEL_INDEXES = new int[] {22, 23, 24, 25};
	private ItemStack[] inventory = new ItemStack[27];
	public int learnFlag = 0;
	public int unlearnFlag = 0;
	public String filter = "";
	public int searchpage = 0;
	public LinkedList<ItemStack> knowledge = Lists.newLinkedList();
	
	public void handleKnowledge(ItemStack stack)
	{
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}

		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0 && stack.getItemDamage() != 0)
		{
			stack.setItemDamage(0);
		}
		
		if (!Transmutation.hasKnowledgeForStack(player, stack) && !Transmutation.hasFullKnowledge(player.getCommandSenderName()))
		{
			learnFlag = 300;
			
			if (stack.getItem() == ObjHandler.tome)
			{
				Transmutation.setAllKnowledge(player.getCommandSenderName());
			}
			else
			{
				if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack))
				{
					stack.stackTagCompound = null;
				}

				Transmutation.addToKnowledge(player.getCommandSenderName(), stack);
			}
			
			if (!this.worldObj.isRemote)
			{
				Transmutation.sync(player);
			}
		}
		
		updateOutputs();
	}

	public void handleUnlearn(ItemStack stack)
	{
		if (stack.stackSize > 1)
		{
			stack.stackSize = 1;
		}

		if (!stack.getHasSubtypes() && stack.getMaxDamage() != 0 && stack.getItemDamage() != 0)
		{
			stack.setItemDamage(0);
		}
		
		if (Transmutation.hasKnowledgeForStack(player, stack) && !Transmutation.hasFullKnowledge(player.getCommandSenderName()))
		{
			unlearnFlag = 300;

			if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack))
			{
				stack.stackTagCompound = null;
			}

			Transmutation.removeFromKnowledge(player.getCommandSenderName(), stack);
			
			if (!this.worldObj.isRemote)
			{
				Transmutation.sync(player);
			}
		}
		
		updateOutputs();
	}
	
	public void checkForUpdates()
	{
		int matterEmc = EMCHelper.getEmcValue(inventory[MATTER_INDEXES[0]]);
		int fuelEmc = EMCHelper.getEmcValue(inventory[FUEL_INDEXES[0]]);
		
		int maxEmc = matterEmc > fuelEmc ? matterEmc : fuelEmc;
		
		if (maxEmc > this.getStoredEmc())
		{
			updateOutputs();
		}
	}
	
	public void updateOutputs()
	{
		knowledge = (LinkedList<ItemStack>) Transmutation.getKnowledge(player.getCommandSenderName()).clone();
		
		for (int i : MATTER_INDEXES)
		{
			inventory[i] = null;
		}
		
		for (int i : FUEL_INDEXES)
		{
			inventory[i] = null;
		}
		
		ItemStack lockCopy = null;

		Collections.sort(knowledge, Comparators.ITEMSTACK_EMC_DESCENDING);

		if (inventory[LOCK_INDEX] != null)
		{
			int reqEmc = EMCHelper.getEmcValue(inventory[LOCK_INDEX]);
			
			if (this.getStoredEmc() < reqEmc)
			{
				return;
			}

			lockCopy = ItemHelper.getNormalizedStack(inventory[LOCK_INDEX]);

			if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy))
			{
				lockCopy.setTagCompound(new NBTTagCompound());
			}

			Iterator<ItemStack> iter = knowledge.iterator();
			int pagecounter = 0;

			while (iter.hasNext())
			{
				ItemStack stack = iter.next();
				
				if (EMCHelper.getEmcValue(stack) > reqEmc)
				{
					iter.remove();
					continue;
				}

				if (ItemHelper.basicAreStacksEqual(lockCopy, stack))
				{
					iter.remove();
					continue;
				}

				String displayName = "";

				try
				{
					displayName = stack.getDisplayName();
				}
				catch (Exception e)
				{
					continue;
				}

				if (displayName == null)
				{
					iter.remove();
					continue;
				}
				else if (filter.length() > 0 && !displayName.toLowerCase().contains(filter))
				{
					iter.remove();
					continue;
				}

				if (pagecounter < (searchpage * 12))
				{
					pagecounter++;
					iter.remove();
					continue;
				}
			}
		}
		else
		{
			Iterator<ItemStack> iter = knowledge.iterator();
			int pagecounter = 0;

			while (iter.hasNext())
			{
				ItemStack stack = iter.next();
				
				if (this.getStoredEmc() < EMCHelper.getEmcValue(stack))
				{
					iter.remove();
					continue;
				}

				String displayName = "";

				try
				{
					displayName = stack.getDisplayName();
				}
				catch (Exception e)
				{
					continue;
				}

				if (displayName == null)
				{
					iter.remove();
					continue;
				}
				else if (filter.length() > 0 && !displayName.toLowerCase().contains(filter))
				{
					iter.remove();
					continue;
				}

				if (pagecounter < (searchpage * 12))
				{
					pagecounter++;
					iter.remove();
					continue;
				}
			}
		}
		
		int matterCounter = 0;
		int fuelCounter = 0;

		if (lockCopy != null)
		{
			if (FuelMapper.isStackFuel(lockCopy))
			{
				inventory[FUEL_INDEXES[0]] = lockCopy;
				fuelCounter++;
			}
			else
			{
				inventory[MATTER_INDEXES[0]] = lockCopy;
				matterCounter++;
			}
		}

		for (ItemStack stack : knowledge)
		{
			if (FuelMapper.isStackFuel(stack))
			{
				if (fuelCounter < 4)
				{
					inventory[FUEL_INDEXES[fuelCounter]] = stack;
				
					fuelCounter++;
				}
			}
			else
			{
				if (matterCounter < 12)
				{
					inventory[MATTER_INDEXES[matterCounter]] = stack;
					
					matterCounter++;
 				}
			}
		}
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
		
		//this.setEmcValue(nbt.getDouble("EMC"));
		
		NBTTagList items = nbt.getTagList("Items", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < items.tagCount(); i++)
		{
			NBTTagCompound tag = items.getCompoundTagAt(i);
			
			inventory[tag.getByte("Slot")] = ItemStack.loadItemStackFromNBT(tag);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		//nbt.setDouble("EMC", this.getStoredEmc());
		
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i <= 8; i++)
		{
			if (inventory[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(tag);
				items.appendTag(tag);
			}
		}
		
		nbt.setTag("Items", items);
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
		return "tile.pe_transmutation_stone.name";
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
		if (!this.worldObj.isRemote)
		{
			this.setEmcValueWithPKT(Transmutation.getStoredEmc(player.getCommandSenderName()));
		}
		
		updateOutputs();
	}

	@Override
	public void closeInventory() 
	{
		if (!this.worldObj.isRemote)
		{
			Transmutation.setStoredEmc(player.getCommandSenderName(), this.getStoredEmc());
			PacketHandler.sendTo(new ClientSyncTableEMCPKT(this.getStoredEmc()), (EntityPlayerMP) player);
		}
		
		player = null;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}

	@Override
	public boolean isRequestingEmc() 
	{
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int par1)
	{
		return new int[0];
	}

	@Override
	public boolean canExtractItem(int par1, ItemStack stack, int par3)
	{
		return false;
	}

	@Override
	public boolean canInsertItem(int par1, ItemStack stack, int par3)
	{
		return false;
	}
}
