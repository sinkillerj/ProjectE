package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;

public class RMFurnaceTile extends TileEmc implements IEmcAcceptor
{
	private static final long EMC_CONSUMPTION = 2;
	private final ItemStackHandler inputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler outputInventory = new StackHandler(getInvSize());
	private final ItemStackHandler fuelInv = new StackHandler(1);
	private final IItemHandlerModifiable automationInput = new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN)
	{
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return SlotPredicates.SMELTABLE.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}
	};
	private final IItemHandlerModifiable automationFuel = new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN)
	{
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return SlotPredicates.FURNACE_FUEL.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}
	};
	private final IItemHandlerModifiable automationOutput = new WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT);
	private final IItemHandler automationSides = new CombinedInvWrapper(automationFuel, automationOutput);
	private final CombinedInvWrapper joined = new CombinedInvWrapper(automationInput, automationFuel, automationOutput);
	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
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

	protected float getOreDoubleChance() {
		return 1F;
	}

	public IItemHandler getFuel()
	{
		return fuelInv;
	}
	
	private ItemStack getFuelItem()
	{
		return fuelInv.getStackInSlot(0);
	}

	public IItemHandler getInput()
	{
		return inputInventory;
	}

	public IItemHandler getOutput()
	{
		return outputInventory;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side == null)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(joined);
			}
			else
			{
				switch (side)
				{
					case UP: return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationInput);
					case DOWN: return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationOutput);
					default: return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationSides);
				}
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
		
		if (!this.getWorld().isRemote)
		{
			pullFromInventories();
			ItemHelper.compactInventory(inputInventory);

			if (canSmelt() && !getFuelItem().isEmpty() && getFuelItem().getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) getFuelItem().getItem());
				if (itemEmc.getStoredEmc(getFuelItem()) >= EMC_CONSUMPTION)
				{
					itemEmc.extractEmc(getFuelItem(), EMC_CONSUMPTION);
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
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(getFuelItem());
			
				if (furnaceBurnTime > 0)
				{
					flag1 = true;
					
					if (!getFuelItem().isEmpty())
					{
						ItemStack copy = getFuelItem().copy();

						getFuelItem().shrink(1);

						if (getFuelItem().isEmpty())
						{
							fuelInv.setStackInSlot(0, copy.getItem().getContainerItem(copy));
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
				Block block = world.getBlockState(pos).getBlock();
				
				if (!this.getWorld().isRemote && block instanceof MatterFurnace)
				{
					((MatterFurnace) block).updateFurnaceBlockState(furnaceBurnTime > 0, world, getPos());
				}
			}

			if (flag1)
			{
				markDirty();
			}

			ItemHelper.compactInventory(outputInventory);
			pushToInventories();
		}
	}
	
	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	private void pullFromInventories()
	{
		TileEntity tile = this.getWorld().getTileEntity(pos.up());
		if (tile == null || tile instanceof TileEntityHopper || tile instanceof TileEntityDropper)
			return;
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

		if (handler == null)
		{
			if (tile instanceof ISidedInventory)
			{
				handler = new SidedInvWrapper((ISidedInventory) tile, EnumFacing.DOWN);
			} else if (tile instanceof IInventory)
			{
				handler = new InvWrapper((IInventory) tile);
			} else
			{
				return;
			}
		}

		for (int i = 0; i < handler.getSlots(); i++)
		{
			ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (extractTest.isEmpty())
				continue;

			IItemHandler targetInv = extractTest.getItem() instanceof IItemEmc || TileEntityFurnace.isItemFuel(extractTest)
					? fuelInv : inputInventory;

			ItemStack remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true);
			int successfullyTransferred = extractTest.getCount() - remainderTest.getCount();

			if (successfullyTransferred > 0)
			{
				ItemStack toInsert = handler.extractItem(i, successfullyTransferred, false);
				ItemStack result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
				assert result.isEmpty();
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

		if (world.rand.nextFloat() < getOreDoubleChance()
			&& ItemHelper.getOreDictionaryName(toSmelt).startsWith("ore"))
		{
			smeltResult.grow(smeltResult.getCount());
		}

		ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false);
		
		toSmelt.shrink(1);
	}
	
	private boolean canSmelt() 
	{
		ItemStack toSmelt = inputInventory.getStackInSlot(0);
		
		if (toSmelt.isEmpty())
		{
			return false;
		}
		
		ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(toSmelt);
		if (smeltResult.isEmpty())
		{
			return false;
		}
		
		ItemStack currentSmelted = outputInventory.getStackInSlot(outputInventory.getSlots() - 1);
		
		if (currentSmelted.isEmpty())
		{
			return true;
		}
		if (!smeltResult.isItemEqual(currentSmelted))
		{
			return false;
		}
		
		int result = currentSmelted.getCount() + smeltResult.getCount();
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
		currentItemBurnTime = getItemBurnTime(getFuelItem());
	}
	
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setShort("BurnTime", (short) furnaceBurnTime);
		nbt.setShort("CookTime", (short) furnaceCookTime);
		nbt.setTag("Input", inputInventory.serializeNBT());
		nbt.setTag("Output", outputInventory.serializeNBT());
		nbt.setTag("Fuel", fuelInv.serializeNBT());
		return nbt;
	}

	@Override
	public long acceptEMC(@Nonnull EnumFacing side, long toAccept)
	{
		if (this.getStoredEmc() < EMC_CONSUMPTION)
		{
			long needed = EMC_CONSUMPTION - this.getStoredEmc();
			long accept = Math.min(needed, toAccept);
			this.addEMC(accept);
			return accept;
		}
		return 0;
	}
}
