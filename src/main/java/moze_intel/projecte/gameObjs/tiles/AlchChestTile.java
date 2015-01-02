package moze_intel.projecte.gameObjs.tiles;

import java.util.Iterator;
import java.util.List;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.gameObjs.items.rings.RingToggle;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class AlchChestTile extends TileEmcDirection implements IInventory
{
	private ItemStack[] inventory = new ItemStack[104];
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	private int ticksSinceSync;
	
	public AlchChestTile()
	{
		super();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[104];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");
			
			if (slot >= 0 && slot < 104)
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
			}
		}	
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 104; i++)
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
		return 104;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qnt) 
	{
		ItemStack stack = inventory[slot];
		if (stack != null)
		{
			if (stack.stackSize <= qnt)
			{
				inventory[slot] = null;
			}
			else
			{
				stack = stack.splitStack(qnt);
				if (stack.stackSize == 0)
					inventory[slot] = null;
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
		return "Alchemical Chest";
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
	public void updateEntity()
	{
		super.updateEntity();

		if (++ticksSinceSync % 20 * 4 == 0)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);
		}

		prevLidAngle = lidAngle;
		float angleIncrement = 0.1F;
		double adjustedXCoord, adjustedZCoord;
		
		if (numPlayersUsing > 0 && lidAngle == 0.0F)
		{
			adjustedXCoord = xCoord + 0.5D;
			adjustedZCoord = zCoord + 0.5D;
			worldObj.playSoundEffect(adjustedXCoord, yCoord + 0.5D, adjustedZCoord, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F)
		{
			float var8 = lidAngle;

			if (numPlayersUsing > 0)
			{
				lidAngle += angleIncrement;
			}
			else
			{
				lidAngle -= angleIncrement;
			}

			if (lidAngle > 1.0F)
			{
				lidAngle = 1.0F;
			}

			if (lidAngle < 0.5F && var8 >= 0.5F)
			{
				adjustedXCoord = xCoord + 0.5D;
				adjustedZCoord = zCoord + 0.5D;
				worldObj.playSoundEffect(adjustedXCoord, yCoord + 0.5D, adjustedZCoord, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F)
			{
				lidAngle = 0.0F;
			}
		}
		
		if (!this.worldObj.isRemote)
		{
			ItemStack rTalisman = Utils.getStackFromInv(this, new ItemStack(ObjHandler.repairTalisman));
			
			if (rTalisman != null)
			{
				byte coolDown = rTalisman.stackTagCompound.getByte("Cooldown");
				
				if (coolDown > 0)
				{
					rTalisman.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
				}
				else
				{
					boolean hasAction = false;
					
					for (int i = 0; i < 104; i++)
					{
						ItemStack invStack = inventory[i];
					
						if (invStack == null || invStack.getItem() instanceof RingToggle) 
						{
							continue;
						}
					
						if (!invStack.getHasSubtypes() && invStack.getMaxDamage() != 0 && invStack.getItemDamage() > 0)
						{
							invStack.setItemDamage(invStack.getItemDamage() - 1);
							inventory[i] = invStack;
							
							if (!hasAction)
							{
								hasAction = true;
							}
						}
					}
					
					if (hasAction)
					{
						rTalisman.stackTagCompound.setByte("Cooldown", (byte) 19);
					}
				}
			}
			
			ItemStack gemDensity = Utils.getStackFromInv(this, new ItemStack(ObjHandler.eternalDensity, 1, 1));
			
			if (gemDensity != null)
			{
				GemEternalDensity.condense(gemDensity, inventory);
			}
		}
			
		ItemStack blackHoleBand = Utils.getStackFromInv(this, new ItemStack(ObjHandler.blackHole, 1, 1));
			
		if (blackHoleBand != null)
		{
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(this.xCoord - 5, this.yCoord - 5, this.zCoord - 5, this.xCoord + 5, this.yCoord + 5, this.zCoord + 5);
				
			List<EntityItem> itemList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
			List<EntityLootBall> lootList = this.worldObj.getEntitiesWithinAABB(EntityLootBall.class, box);
			
			for (EntityItem item : itemList)
			{
				if (getDistance(item.posX, item.posY, item.posZ) <= 0.5f)
				{
					if (!this.worldObj.isRemote)
					{
					
						if (Utils.hasSpace(this, item.getEntityItem()))
						{
							ItemStack remain = Utils.pushStackInInv(this, item.getEntityItem());
							
							if (remain == null)
							{
								item.setDead();
							}
						}
					}
				}
				else
				{
					double d1 = (this.xCoord - item.posX);
					double d2 = (this.yCoord - item.posY);
					double d3 = (this.zCoord - item.posZ);
					double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

					item.motionX += d1 / d4 * 0.1D;
					item.motionY += d2 / d4 * 0.1D;
					item.motionZ += d3 / d4 * 0.1D;
						
					item.moveEntity(item.motionX, item.motionY, item.motionZ);
				}
			}
			
			for (EntityLootBall loot : lootList)
			{
				if (getDistance(loot.posX, loot.posY, loot.posZ) <= 0.5f)
				{
					if (!this.worldObj.isRemote)
					{
						//Avoids concurrent modification exception
						Iterator<ItemStack> iter = loot.getItemList().iterator();
						
						while (iter.hasNext())
						{
							ItemStack current = iter.next();
							
							if (Utils.hasSpace(this, current))
							{
								ItemStack remain = Utils.pushStackInInv(this, current);
								
								if (remain == null)
								{
									iter.remove();
								}
							}
						}
					}
				}
				else
				{
					double d1 = (this.xCoord - loot.posX);
					double d2 = (this.yCoord - loot.posY);
					double d3 = (this.zCoord - loot.posZ);
					double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

					loot.motionX += d1 / d4 * 0.1D;
					loot.motionY += d2 / d4 * 0.1D;
					loot.motionZ += d3 / d4 * 0.1D;
						
					loot.moveEntity(loot.motionX, loot.motionY, loot.motionZ);
				}
			}
		}
	}
	
	private double getDistance(double x, double y, double z)
	{
		return Math.sqrt((Math.pow((this.xCoord - x), 2) + Math.pow((this.yCoord - y), 2) + Math.pow((this.zCoord - z), 2)));
	}
	
	@Override
	public boolean receiveClientEvent(int number, int arg)
	{
		if (number == 1)
		{
			numPlayersUsing = arg;
			return true;
		}
		else return super.receiveClientEvent(number, arg);
	}
	
	@Override
	public void openInventory()
	{
		++numPlayersUsing;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);
	}
	
	@Override
	public void closeInventory()
	{
		--numPlayersUsing;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return true;
	}

	@Override
	public boolean isRequestingEmc() 
	{
		return false;
	}
}
