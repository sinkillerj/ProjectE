package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class RMFurnaceTile extends TileEmc implements IEmcAcceptor
{
	private static final float EMC_CONSUMPTION = 1.6f;
	private final ItemStackHandler inputInventory = new StackHandler(getInvSize(), true, false) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (FurnaceRecipes.instance().getSmeltingResult(stack) != null)
				return super.insertItem(slot, stack, simulate);
			else return stack;
		}
	};
	private final ItemStackHandler outputInventory = new StackHandler(getInvSize(), false, true);
	private final ItemStackHandler fuelInv = new StackHandler(1, true, false) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (TileEntityFurnace.isItemFuel(stack) || stack.getItem() instanceof IItemEmc)
				return super.insertItem(slot, stack, simulate);
			else return null;
		}
	};
	private final CombinedInvWrapper joined = new CombinedInvWrapper(inputInventory, fuelInv, outputInventory);
	protected final int ticksBeforeSmelt;
	protected final int efficiencyBonus;
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;
	
	public RMFurnaceTile()
	{
		this(3, 4);
	}

	protected RMFurnaceTile(int ticksBeforeSmelt, int efficiencyBonus)
	{
		super(64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;
	}

	protected int getInvSize()
	{
		return 13;
	}

	private ItemStack getFuel()
	{
		return fuelInv.getStackInSlot(0);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side == null)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(joined);
			}
			else if (side == EnumFacing.UP)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputInventory);
			} else if (side == EnumFacing.DOWN)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(outputInventory);
			} else if (side.getAxis().isHorizontal())
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(fuelInv);
			}
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void update()
	{
		boolean flag = furnaceBurnTime > 0;
		boolean flag1 = false;
		
		if (furnaceBurnTime > 0)
		{
			--furnaceBurnTime;
		}
		
		if (!this.worldObj.isRemote)
		{
			pullFromInventories();
			pushSmeltStack();

			if (canSmelt() && getFuel() != null && getFuel().getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) getFuel().getItem());
				if (itemEmc.getStoredEmc(getFuel()) >= EMC_CONSUMPTION)
				{
					itemEmc.extractEmc(getFuel(), EMC_CONSUMPTION);
					this.addEMC(EMC_CONSUMPTION);
				}
			}
			
			if (this.getStoredEmc() >= EMC_CONSUMPTION)
			{
				furnaceBurnTime = 1;
				this.removeEMC(EMC_CONSUMPTION);
			}
			
			if (furnaceBurnTime == 0 && canSmelt())
			{
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(getFuel());
			
				if (furnaceBurnTime > 0)
				{
					flag1 = true;
					
					if (getFuel() != null)
					{
						--getFuel().stackSize;
						
						if (getFuel().stackSize == 0)
						{
							fuelInv.setStackInSlot(0, getFuel().getItem().getContainerItem(getFuel()));
						}
					}
				}
			}
		
			if (furnaceBurnTime > 0 && canSmelt())
			{
				++furnaceCookTime;
			
				if (furnaceCookTime == ticksBeforeSmelt)
				{
					furnaceCookTime = 0;
					smeltItem();
					flag1 = true;
				}
			}

			if (flag != furnaceBurnTime > 0)
			{
				flag1 = true;
				Block block = worldObj.getBlockState(pos).getBlock();
				
				if (!this.worldObj.isRemote && block instanceof MatterFurnace)
				{
					((MatterFurnace) block).updateFurnaceBlockState(furnaceBurnTime > 0, worldObj, getPos());
				}
			}
		}
		
		if (flag1) 
		{
			markDirty();
		}
		
		if (!this.worldObj.isRemote)
		{
			pushOutput();
			pushToInventories();
		}
	}
	
	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}
	
	private void pushSmeltStack()
	{
		// todo compact input
	}
	
	private void pushOutput()
	{
		// todo compact output
	}
	
	private void pullFromInventories()
	{
		TileEntity tile = this.worldObj.getTileEntity(pos.up());
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		if (handler == null && tile instanceof ISidedInventory)
			handler = new SidedInvWrapper(((ISidedInventory) tile), EnumFacing.DOWN);
		if (handler == null && tile instanceof IInventory)
			handler = new InvWrapper(((IInventory) tile));
		if (handler == null)
			return;

		for (int i = 0; i < handler.getSlots(); i++)
		{
			ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (extractTest == null)
				continue;

			IItemHandler targetInv = extractTest.getItem() instanceof IItemEmc || TileEntityFurnace.isItemFuel(extractTest)
					? fuelInv : inputInventory;

			ItemStack remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true);
			int successfullyTransferred = extractTest.stackSize - (remainderTest == null ? 0 : remainderTest.stackSize);

			if (successfullyTransferred > 0)
			{
				ItemStack toInsert = handler.extractItem(i, successfullyTransferred, false);
				ItemStack result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
				assert result == null;
			}
		}
	}
	
	private void pushToInventories()
	{
		// todo push to others
	}
	
	private void smeltItem()
	{
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(toSmelt).copy();
		ItemStack currentSmelted = outputInventory.getStackInSlot(outputInventory.getSlots() - 1);

		if (ItemHelper.getOreDictionaryName(toSmelt).startsWith("ore"))
		{
			smeltResult.stackSize *= 2;
		}
		
		if (currentSmelted == null) 
		{
			outputInventory.setStackInSlot(outputInventory.getSlots() - 1, smeltResult);
		}
		else
		{
			currentSmelted.stackSize += smeltResult.stackSize;
		}
		
		toSmelt.stackSize--;
		if (toSmelt.stackSize == 0)
			inputInventory.setStackInSlot(0, null);
	}
	
	private boolean canSmelt() 
	{
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		
		if (toSmelt == null) 
		{
			return false;
		}
		
		ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(toSmelt);
		if (smeltResult == null) 
		{
			return false;
		}
		
		ItemStack currentSmelted = outputInventory.getStackInSlot(outputInventory.getSlots() - 1);
		
		if (currentSmelted == null) 
		{
			return true;
		}
		if (!smeltResult.isItemEqual(currentSmelted))
		{
			return false;
		}
		
		int result = currentSmelted.stackSize + smeltResult.stackSize;
		return result <= currentSmelted.getMaxStackSize();
	}
	
	private int getItemBurnTime(ItemStack stack)
	{
		int val = TileEntityFurnace.getItemBurnTime(stack);
		return (val * ticksBeforeSmelt) / 200 * efficiencyBonus;
	}
	
	public int getCookProgressScaled(int value)
	{
		return (furnaceCookTime + (isBurning() && canSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
	}
	
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int value)
	{
		if (this.currentItemBurnTime == 0)
			this.currentItemBurnTime = ticksBeforeSmelt;

		return furnaceBurnTime * value / currentItemBurnTime;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		furnaceBurnTime = nbt.getShort("BurnTime");
		furnaceCookTime = nbt.getShort("CookTime");
		inputInventory.deserializeNBT(nbt.getCompoundTag("Input"));
		outputInventory.deserializeNBT(nbt.getCompoundTag("Output"));
		fuelInv.deserializeNBT(nbt.getCompoundTag("Fuel"));
		currentItemBurnTime = getItemBurnTime(getFuel());
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setShort("BurnTime", (short) furnaceBurnTime);
		nbt.setShort("CookTime", (short) furnaceCookTime);
		nbt.setTag("Input", inputInventory.serializeNBT());
		nbt.setTag("Output", outputInventory.serializeNBT());
		nbt.setTag("Fuel", fuelInv.serializeNBT());
	}

	@Override
	public double acceptEMC(EnumFacing side, double toAccept)
	{
		if (this.getStoredEmc() < EMC_CONSUMPTION)
		{
			double needed = EMC_CONSUMPTION - this.getStoredEmc();
			double accept = Math.min(needed, toAccept);
			this.addEMC(accept);
			return accept;
		}
		return 0;
	}
}
